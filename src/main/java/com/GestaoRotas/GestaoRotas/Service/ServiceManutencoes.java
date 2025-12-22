package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

import jakarta.transaction.Transactional;

@Service
public class ServiceManutencoes {
    private final RepositoryManutencao repositoryManuntencao;
	private final RepositoryVeiculo repositoryVeiculo; 
	private final ServiceVeiculo veiculoService;// ja que vou precisar presistir com o veiculo	
	//pra a  utilizacao 
    private statusManutencao statusManutencao; 
    
public ServiceManutencoes(RepositoryManutencao repositoryManuntencao, RepositoryVeiculo repositoryVeiculo, ServiceVeiculo veiculoService) {
	     this.repositoryManuntencao=repositoryManuntencao;
	     this.repositoryVeiculo = repositoryVeiculo;
	     this.veiculoService= veiculoService;
	}
	 
 public String salvar(manuntecaoDTO manutencaoDTO) {
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
    // Define o status inicial
    LocalDate hoje = LocalDate.now();
    LocalDate dataManutencao = manutencaoDTO.getDataManutencao();
     
    if (dataManutencao.isEqual(hoje)) {
        manutencao.setStatus(manutencaoDTO.getStatus().AGENDADA_HOJE);
        // Atualiza o veículo para EM_MANUTENCAO
        veiculo.setStatus("EM_MANUTENCAO");
        veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
        repositoryVeiculo.save(veiculo); 
    } else if (dataManutencao.isBefore(hoje)) {
        manutencao.setStatus(manutencaoDTO.getStatus().ATRASADA);
    } else {
        manutencao.setStatus(manutencaoDTO.getStatus().AGENDADA);
    }
    repositoryManuntencao.save(manutencao);
    return "manutencao salva com sucesso";
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
  public String update(manuntecaoDTO manutencaoDTO, long id)  {
	   
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
	  ///// repositoryManuntencao.save(manutencao);

       // Define o status inicial
       LocalDate hoje = LocalDate.now();
       LocalDate dataManutencao = manutencaoDTO.getDataManutencao();
        
       if (dataManutencao.isEqual(hoje)) {
           manutencao.setStatus(manutencaoDTO.getStatus().AGENDADA_HOJE);
           // Atualiza o veículo para EM_MANUTENCAO
           veiculo.setStatus("EM_MANUTENCAO");
           veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
           repositoryVeiculo.save(veiculo); 
       } else if (dataManutencao.isBefore(hoje)) {
           manutencao.setStatus(manutencaoDTO.getStatus().ATRASADA);
       } else {
           manutencao.setStatus(manutencaoDTO.getStatus().AGENDADA);
       }
       repositoryManuntencao.save(manutencao);
   
	    return "manuntencao atualizada com sucesso";
  }
  //
  @Scheduled(cron = "0 0 0 * * *") // Executa todos os dias à meia-noite
  @Transactional
  public void verificarManutencoesDoDia() {
      System.out.println("Verificando manutenções do dia...");
      LocalDate hoje = LocalDate.now();
      
      // Busca todas as manutenções agendadas para hoje
      List<Manutencao> manutencoesHoje = repositoryManuntencao
    		    .findByDataManutencaoAndStatusNotIn(
    		        hoje,
    		        List.of( 
    		        		statusManutencao.CONCLUIDA,
    		        	    statusManutencao.CANCELADA,
    		                statusManutencao.EM_ANDAMENTO
    		        ) 
    		    );
      
      for (Manutencao manutencao : manutencoesHoje) {
          // Atualiza o status da manutenção 
    	  String stringEnum ="AGENDADA_HOJE";
          manutencao.setStatus(statusManutencao.valueOf(stringEnum));
          
          repositoryManuntencao.save(manutencao);
          
          // Atualiza o status do veículo para EM_MANUTENCAO
          Veiculo veiculo = manutencao.getVeiculo();
          if (veiculo != null && !veiculo.getStatus().equals("EM_MANUTENCAO")) {
              veiculo.setStatus("EM_MANUTENCAO");
              veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
              repositoryVeiculo.save(veiculo);
              
              System.out.println("Veículo " + veiculo.getMatricula() + 
                               " colocado em EM_MANUTENCAO para manutenção ID: " + manutencao.getId());
          }
      }
      
      System.out.println("Total de manutenções verificadas hoje: " + manutencoesHoje.size());
  }

  /**
   * Verifica e atualiza manutenções vencidas
   * Executa a cada 6 horas
   */
  @Scheduled(cron = "0 0 */6 * * *") // A cada 6 horas
  @Transactional
  public void verificarManutencoesVencidas() {
      System.out.println("Verificando manutenções vencidas...");
      LocalDate hoje = LocalDate.now();
      
      // Busca manutenções com data passada e status não finalizados 
      List<Manutencao> manutencoesVencidas = repositoryManuntencao.findByDataManutencaoBeforeAndStatusNotIn(hoje, 
    		  List.of(
    		  statusManutencao.CONCLUIDA,
              statusManutencao.CANCELADA,
              statusManutencao.ATRASADA
          ));
       
      for (Manutencao manutencao : manutencoesVencidas) {
          // Atualiza o status da manutenção
    	  String status = "ATRASADA";
          manutencao.setStatus( statusManutencao.valueOf(status)); 
           repositoryManuntencao.save(manutencao);
          
          // Atualiza o status do veículo
          Veiculo veiculo = manutencao.getVeiculo();
          if (veiculo != null) {
              veiculoService.atualizarStatusVeiculo(veiculo.getId());
          }
      }
      
      System.out.println("Total de manutenções vencidas: " + manutencoesVencidas.size());
  }
  
  
  /**
   * Marca uma manutenção como concluída e atualiza o veículo
   */
  @Transactional
  public Manutencao concluirManutencao(Long id) {
      Manutencao manutencao =   repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      
      // Atualiza o status da manutenção
      
      String status ="CONCLUIDA";
      manutencao.setStatus(statusManutencao.valueOf(status));
      manutencao.setDataConclusao(LocalDateTime.now());
      repositoryManuntencao.save(manutencao);
       
      // Atualiza o status do veículo
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null) {
          veiculoService.atualizarStatusVeiculo(veiculo.getId());
      }
      
      return manutencao;
  }
 
  /**
   * Marca uma manutenção como em andamento
   */
  @Transactional
  public Manutencao iniciarManutencao(Long id) {
      Manutencao manutencao =  repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      
      // Atualiza o status da manutenção
      String status = "EM_ANDAMENTO";
      manutencao.setStatus(statusManutencao.valueOf(status));
      manutencao.setDataInicio(LocalDateTime.now());
        repositoryManuntencao.save(manutencao);
       
      // Garante que o veículo está como EM_MANUTENCAO
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null && !veiculo.getStatus().equals("EM_MANUTENCAO")) {
          veiculo.setStatus("EM_MANUTENCAO");
          veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
          repositoryVeiculo.save(veiculo);
      } 
      
      return manutencao;
  }

  /**
   * Cancela uma manutenção e atualiza o veículo
   */
  @Transactional
  public Manutencao cancelarManutencao(Long id, String motivo) {
      Manutencao manutencao =   repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      
      // Atualiza o status da manutenção
      String status = "CANCELADA";
      manutencao.setStatus(statusManutencao.valueOf(status));
      manutencao.setDescricao("Motivo: "+ motivo);
      manutencao.setDescricao("Cancelada: " + motivo);
      repositoryManuntencao.save(manutencao);
      
      // Atualiza o status do veículo
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null) {
          veiculoService.atualizarStatusVeiculo(veiculo.getId());
      }
      
      return manutencao;
  }

  /**
   * Verifica se um veículo tem manutenção para hoje
   */
  public boolean veiculoTemManutencaoHoje(Long veiculoId) {
      LocalDate hoje = LocalDate.now();
      List<Manutencao> manutencoes = repositoryManuntencao.
          findByVeiculoIdAndDataManutencaoAndStatusNotIn(
              veiculoId,
              hoje,
              List.of(
            		  statusManutencao.CONCLUIDA,
            		  statusManutencao.CANCELADA)
              );
              
         
       
      return !manutencoes.isEmpty();
  }

  /**
   * Retorna as manutenções de hoje
   */
  public List<Manutencao> getManutencoesHoje() {
      LocalDate hoje = LocalDate.now();
      return repositoryManuntencao.findByDataManutencaoAndStatusNotIn(
          hoje,  
          
          List.of(  statusManutencao.CONCLUIDA,
        		  statusManutencao.CANCELADA)
      );
  }

  /**
   * Notifica sobre manutenções do dia seguinte
   * Executa todos os dias às 8h da manhã
   */
  @Scheduled(cron = "0 0 8 * * *") // Todos os dias às 8h
  @Transactional
  public void notificarManutencoesAmanha() {
      LocalDate amanha = LocalDate.now().plusDays(1);
      
      List<Manutencao> manutencoesAmanha =  repositoryManuntencao
          .findByDataManutencaoAndStatus(amanha, statusManutencao.AGENDADA);
      
      for (Manutencao manutencao : manutencoesAmanha) {
          // Aqui você pode implementar notificações (email, push, etc.)
          System.out.println("Notificação: Manutenção agendada para amanhã - " + 
                           "Veículo: " + manutencao.getVeiculo().getMatricula() + 
                           ", Tipo: " + manutencao.getTipoManutencao());
      }
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
    	  if(m.isVencida()) {  
    	 
          String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
          String detalhes = "";
          
          if (m.getProximaManutencaoData() != null) {
              detalhes = "desde " + m.getProximaManutencaoData();
          } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
              detalhes = "atingiu " + m.getVeiculo().getKilometragemAtual() + "km (limite: " + m.getProximaManutencaoKm() + "km)";
          }
          
          alertas.add("⚠️ Revisão vencida do veículo " + placa + " - " + detalhes);
      }});

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
          if (m.isVencida()) return; // Não mostrar como próxima se está vencida
          
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