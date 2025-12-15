package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

import jakarta.transaction.Transactional;

@Service
public class ServiceManutencoes {
    private final RepositoryManutencao repositoryManuntencao;
	private final RepositoryVeiculo repositoryVeiculo; // ja que vou precisar presistir com o veiculo	

	public ServiceManutencoes(RepositoryManutencao repositoryManuntencao, RepositoryVeiculo repositoryVeiculo) {
	     this.repositoryManuntencao=repositoryManuntencao;
	     this.repositoryVeiculo = repositoryVeiculo;
	}
	
 public Manutencao salvar(manuntecaoDTO manutencaoDTO) {
	   Manutencao manutencao = new Manutencao();
    Veiculo veiculo = repositoryVeiculo.findById( manutencaoDTO.getVeiculo_id()).orElseThrow(()-> new RuntimeException("Veiculo nao encontrado"));
    manutencao.setVeiculo(veiculo);
    manutencao.setDataManutencao(manutencaoDTO.getDataManutencao());
    manutencao.setDescricao(manutencaoDTO.getDescricao());
    
    manutencao.setKilometragemVeiculo(manutencaoDTO.getKilometragemVeiculo());;
    manutencao.setProximaManutencaoData(manutencaoDTO.getProximaManutencaoData());;
    manutencao.setTipoManutencao(manutencaoDTO.getTipoManutencao());
    manutencao.setCusto(manutencaoDTO.getCusto());
    manutencao.setProximaManutencaoKm(manutencaoDTO.getProximaManutencaoKm());
    return repositoryManuntencao.save(manutencao);
 }
  
 
 public String deleteById(Long id) { 
	    if (!repositoryManuntencao.existsById(id)) {
	        return "Manutenção não encontrada";
	    }  else if(repositoryManuntencao.existsById(id)) {
	       this.repositoryManuntencao.deleteById(id);
	       repositoryManuntencao.flush(); // força execução imediata
	    }
	    if (repositoryManuntencao.existsById(id)) { 
	        return "Erro: manutenção não foi removida";
	    }

	    return "Manutenção deletada com sucesso";
	}
  
  public List<Manutencao> findAll(){
	  List<Manutencao> lista=this.repositoryManuntencao.findAll();
	  return lista; 
  }  
  //atualizar a manutencao caso haja erros
  public Manutencao update(manuntecaoDTO manutencaoDTO, long id)  {
	   
	    Manutencao manutencao = repositoryManuntencao.findById(id).orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));
	    Veiculo veiculo = repositoryVeiculo.findById( manutencaoDTO.getVeiculo_id()).orElseThrow(()-> new RuntimeException("Veiculo nao encontrado"));
	    manutencao.setVeiculo(veiculo);
	    manutencao.setDataManutencao(manutencaoDTO.getDataManutencao());
	    manutencao.setDescricao(manutencaoDTO.getDescricao());
	    manutencao.setKilometragemVeiculo(manutencaoDTO.getKilometragemVeiculo());;
	    
	    manutencao.setProximaManutencaoData(manutencaoDTO.getProximaManutencaoData());;
	    manutencao.setTipoManutencao(manutencaoDTO.getTipoManutencao());
	    manutencao.setCusto(manutencaoDTO.getCusto());
	    manutencao.setProximaManutencaoKm(manutencaoDTO.getProximaManutencaoKm());
	    return repositoryManuntencao.save(manutencao);
  }
  public Manutencao findById(long id) {
	    return repositoryManuntencao.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manutenção não encontrada"));
	}
   
 public List<Manutencao>  listarPorVeiculo(long veiculoId){
	    // Buscar todas as manutenções de um veículo
	 List<Manutencao> lista=  this.repositoryManuntencao.findByVeiculoId(veiculoId);
	   return lista;
 }
 public List<Manutencao> listarPorTipo(String tipo) {
     return repositoryManuntencao.findBytipoManutencao(tipo);
 } 
 //relatorio de manuntencoes feitas por cada veiculo
 public List<RelatorioManutencaoDTO> gerarRelatorioPorVeiculo() {
     return  repositoryManuntencao.relatorioPorVeiculo();
 } 
 
//No seu ServiceManutencoes, atualize o método gerarAlertas para usar os parâmetros:
 
public List<String> gerarAlertas() {
  List<String> alertas = new ArrayList<>();

  // Manutenções vencidas
  repositoryManuntencao.findManutencoesVencidas()
      .forEach(m -> {
          String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
          String detalhes = "";
          
          if (m.getProximaManutencaoData() != null) {
              detalhes = "desde " + m.getProximaManutencaoData();
          } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
              detalhes = "atingiu " + m.getVeiculo().getKilometragemAtual() + "km (limite: " + m.getProximaManutencaoKm() + "km)";
          }
          
          alertas.add("⚠️ Revisão vencida do veículo " + placa + " - " + detalhes);
      });

  // Próximas manutenções (30 dias ou 1000km)
  LocalDate dataLimite30Dias = LocalDate.now().plusDays(30);
  repositoryManuntencao.findProximasManutencoes(dataLimite30Dias)
      .forEach(m -> {
          if (m.isVencida()) return; // Não mostrar como próxima se está vencida

          String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
          String detalhes = "";

          if (m.getProximaManutencaoData() != null) {
              long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                  java.time.LocalDate.now(), m.getProximaManutencaoData());
              detalhes = "em " + diasRestantes + " dias (" + m.getProximaManutencaoData() + ")";
          } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
              double kmRestantes = m.getProximaManutencaoKm() - m.getVeiculo().getKilometragemAtual();
              detalhes = "faltam " + kmRestantes + "km";
          } 

          alertas.add("ℹ️ Próxima revisão do veículo " + placa + " - " + detalhes);
      });

  // Manutenções muito próximas (7 dias ou 200km)
  LocalDate dataLimite7Dias = LocalDate.now().plusDays(7);
  repositoryManuntencao.findManutencoesProximas7Dias(dataLimite7Dias)
      .forEach(m -> {
          if (isVencida(m)) return; // Não mostrar como próxima se está vencida
          
          String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
          String detalhes = "";  
                   
          if (m.getProximaManutencaoData() != null) {
              long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(
                  java.time.LocalDate.now(), m.getProximaManutencaoData());
              detalhes = "em " + diasRestantes + " dias";
          } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
              double kmRestantes = m.getProximaManutencaoKm() - m.getVeiculo().getKilometragemAtual();
              detalhes = "faltam " + kmRestantes + "km";
          } 
          
          alertas.add("⏰ Atenção! Veículo " + placa + " precisa de manutenção em até 7 dias (" + detalhes + ")");
      });
		  if (alertas.isEmpty()) {
			  alertas.add("Sem Alertas por agora");
		  }

  return alertas; 
}

private boolean isVencida(Manutencao m) {

//Implementar mas estou usado o verificaco vencida da classe manutencao	
  return false; // Implemente conforme sua necessidade
}
 
 // Método alternativo mais simples
 public List<String> gerarAlertasSimplificado() {
     List<String> alertas = new ArrayList<>();
     
     // Manutenções vencidas
     List<Manutencao> vencidas =repositoryManuntencao.findManutencoesVencidas();
     for (Manutencao m : vencidas) {
         String placa = m.getVeiculo().getMatricula();
         alertas.add("🚨 MANUTENÇÃO VENCIDA - Veículo: " + placa + 
                    " | Tipo: " + m.getTipoManutencao());
     }
      
     // Próximas manutenções (7 dias)
     List<Manutencao> proximas = repositoryManuntencao.findManutencoesProximas7Dias(null);
     for (Manutencao m : proximas) {
         if (!m.isVencida()) { // Só adicionar se não estiver vencida
             String placa = m.getVeiculo().getMatricula();
             alertas.add("🔔 MANUTENÇÃO PRÓXIMA - Veículo: " + placa + 
                        " | Tipo: " + m.getTipoManutencao());
         }
     }
    return alertas;
 }
 // novas consultas
 public List<Manutencao> buscarVencidas() {
	    return repositoryManuntencao.findManutencoesVencidas();
	}  

	public List<Manutencao> buscarProximas30Dias() {
	    return repositoryManuntencao.findProximasManutencoes(
	        LocalDate.now().plusDays(30)
	    );
	}

	public List<Manutencao> buscarProximas7Dias() {
	    return repositoryManuntencao.findManutencoesProximas7Dias(
	        LocalDate.now().plusDays(7)
	    );
	}
}