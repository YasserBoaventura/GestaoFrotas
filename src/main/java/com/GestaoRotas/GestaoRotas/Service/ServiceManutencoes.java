package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.concluirManutencaoRequest;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ServiceManutencoes {
    private final RepositoryManutencao repositoryManuntencao;
	private final RepositoryVeiculo repositoryVeiculo; 
	private final ServiceVeiculo veiculoService;// ja que vou precisar presistir com o veiculo	
	//pra a  utilizacao 
   
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
        veiculo.setStatus("MANUTENCAO_HOJE");
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
	   this.repositoryManuntencao.deleteById(id);  
	    return "Manutenção deletada com sucesso";
	}
   
  public List<Manutencao> findAll(){  
	  return this.repositoryManuntencao.findAll();
  }  
  //atualizar a manutencao caso haja erros
  public String update(manuntecaoDTO manutencaoDTO, long id)  {
	   
	    Manutencao manutencao = repositoryManuntencao.findById(id).orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));
	    Veiculo veiculo = repositoryVeiculo.findById(manutencaoDTO.getVeiculo_id()).orElseThrow(()-> new RuntimeException("Veiculo nao encontrado"));
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
           veiculo.setStatus("MANUTENCAO_HOJE");
           veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
           repositoryVeiculo.save(veiculo);   
       } else if (dataManutencao.isBefore(hoje)) {
           manutencao.setStatus(manutencaoDTO.getStatus().ATRASADA);
       } else {
           manutencao.setStatus(manutencaoDTO.getStatus().AGENDADA);
       }
       manutencao.setStatus(manutencaoDTO.getStatus());
       repositoryManuntencao.save(manutencao);
    return "manuntencao atualizada com sucesso";
  } 
  //
  @Scheduled(cron = "0 0 0 * * *") // Executa todos os dias à meia-noite
  @Transactional
  public void verificarManutencoesDoDia() {
      System.out.println("=== INÍCIO: Verificando manutenções do dia ===");
  LocalDate hoje = LocalDate.now();
  
  // Busca todas as manutenções agendadas para hoje
  List<Manutencao> manutencoesHoje = repositoryManuntencao
          .findByDataManutencaoAndStatusNotIn(
              hoje,
              List.of(     
                  statusManutencao.CONCLUIDA,
                  statusManutencao.CANCELADA,
                  statusManutencao.EM_ANDAMENTO,
                  statusManutencao.AGENDADA_HOJE // isso para não buscar as que já têm este status
              ) 
          );
  
  System.out.println("Manutenções encontradas para hoje: " + manutencoesHoje.size());
  
  for (Manutencao manutencao : manutencoesHoje) {
      System.out.println("Processando manutenção ID: " + manutencao.getId() + 
    ", Status atual: " + manutencao.getStatus());
  
  try {
      // Atualiza o status da manutenção
  manutencao.setStatus(statusManutencao.AGENDADA_HOJE);
  
  // REMOVA UMA DAS CHAMADAS SAVE - mantenha apenas uma:
  repositoryManuntencao.save(manutencao);
  // repositoryManuntencao.saveAndFlush(manutencao); 
  
  System.out.println("Status atualizado para AGENDADA_HOJE na manutenção ID: " + manutencao.getId());
  
  // Atualiza o status do veículo para EM_MANUTENCAO
  Veiculo veiculo = manutencao.getVeiculo();
  if (veiculo != null && !veiculo.getStatus().equals("EM_MANUTENCAO")) {
  veiculo.setStatus("EM_MANUTENCAO");
  veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
  repositoryVeiculo.save(veiculo);
  
  System.out.println("Veículo " + veiculo.getMatricula() + 
           " colocado em EM_MANUTENCAO para manutenção ID: " + manutencao.getId());
  } else if (veiculo != null) {
      System.out.println("Veículo " + veiculo.getMatricula() + 
           " já está em EM_MANUTENCAO");
      }
  } catch (Exception e) {
      System.out.println("ERRO ao processar manutenção ID: " + manutencao.getId() + 
       " - " + e.getMessage());
      }
  }
  
  System.out.println("=== FIM: Total de manutenções verificadas hoje: " + manutencoesHoje.size());
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
          manutencao.setStatus(statusManutencao.ATRASADA);  
           repositoryManuntencao.save(manutencao);
           repositoryManuntencao.saveAndFlush(manutencao);
          
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
  public Map<String, String> concluirManutencao(Long id, String observacoes) {
	  Map<String , String> response = new HashMap<>();
      Manutencao manutencao =   repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      manutencao.setDescricao(observacoes);
      // Atualiza o status da manutenção
      
      manutencao.setStatus(statusManutencao.CONCLUIDA);
      LocalDateTime agora = LocalDateTime.now();

      DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  
      String dataHoraFormatada = agora.format(formatter);
      
      manutencao.setDataConclusao(dataHoraFormatada);
      //a data da manutencao so vai ser cadastrada se for inicializada
      manutencao.setDataManutencao(LocalDate.now());
      repositoryManuntencao.save(manutencao);
       
      // Atualiza o status do veículo
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null) {
       veiculo.setStatus("DISPONIVEL");
       veiculo.setDataAtualizacaoStatus(LocalDateTime.now()); 
       this.repositoryVeiculo.save(veiculo);  
      }
      response.put("sucesso", "sucesso") ;     
      return  response; 
  }
 
  /**
   * Marca uma manutenção como em andamento
   */
  @Transactional
  public  Map<String , String> iniciarManutencao(Long id) {
      Manutencao manutencao =  repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      
      Map<String, String> response = new HashMap<>();
      // Atualiza o status da manutenção
     
      manutencao.setStatus(statusManutencao.EM_ANDAMENTO);
      LocalDateTime agora = LocalDateTime.now();

      DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  
      String dataHoraFormatada = agora.format(formatter);
      System.out.print(dataHoraFormatada);
      manutencao.setDataInicio(dataHoraFormatada); 
       
        repositoryManuntencao.save(manutencao);
       
      // Garante que o veículo está como EM_MANUTENCAO
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null && !veiculo.getStatus().equals("EM_MANUTENCAO")) {
          veiculo.setStatus("EM_MANUTENCAO");
          veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
          repositoryVeiculo.save(veiculo);
      } 
      
      response.put("sucesso", "Manutencao inicializada com sucesso");
      return response;
  }

  /**
   * Cancela uma manutenção e atualiza o veículo
   */
  @Transactional
  public Map<String, String> cancelarManutencao(Long id, String motivo) {
	  Map<String ,String> response = new HashMap<>();
      Manutencao manutencao =   repositoryManuntencao.findById(id)
          .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
      
      // Atualiza o status da manutenção
      String status = "CANCELADA";
      manutencao.setStatus(statusManutencao.CANCELADA); 
      manutencao.setDescricao("Motivo: "+ motivo);
      manutencao.setDescricao("Cancelada: " + motivo);
      repositoryManuntencao.save(manutencao);
      
      // Atualiza o status do veículo
      Veiculo veiculo = manutencao.getVeiculo();
      if (veiculo != null) { 
          veiculoService.atualizarStatusVeiculo(veiculo.getId());
      }
      response.put("sucesso", "Manutencao cancelada com sucesso");
      return response;
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
           
          List.of(statusManutencao.CONCLUIDA,
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
 public Manutencao findById(long id){
	    return repositoryManuntencao.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manutenção não encontrada"));
	}
   
 public List<Manutencao>  listarPorVeiculo(long veiculoId){
 return repositoryManuntencao.findByVeiculoId(veiculoId);  
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
	    LocalDate hoje = LocalDate.now();
	    
	    // Manutenções vencidas 
repositoryManuntencao.findManutencoesVencidas()
    .forEach(m -> {
        String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
        String detalhes = "";
         
        if (m.getProximaManutencaoData() != null && m.getProximaManutencaoData().isBefore(hoje)) {
            long diasAtraso = ChronoUnit.DAYS.between(m.getProximaManutencaoData(), hoje);
            detalhes = "atrasada há " + diasAtraso + " dias (desde " + m.getProximaManutencaoData() + ")";
        } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null && 
                   m.getVeiculo().getKilometragemAtual() >= m.getProximaManutencaoKm()) {
            double kmExcedido = m.getVeiculo().getKilometragemAtual() - m.getProximaManutencaoKm();
            detalhes = "atingiu " + m.getVeiculo().getKilometragemAtual() + "km (excedeu " + kmExcedido + "km do limite)";
        }
        
        alertas.add("⚠️ Revisão vencida do veículo " + placa + " - " + detalhes);
    });

// Próximas manutenções (até 30 dias)
List<Manutencao> proximas30dias = repositoryManuntencao.findProximasManutencoes(hoje.plusDays(30));

proximas30dias.stream()
    .filter(m -> !m.isVencida()) // Filtra apenas não vencidas
    .forEach(m -> {
        String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
        String detalhes = "";

        if (m.getProximaManutencaoData() != null) { 
            long diasRestantes = ChronoUnit.DAYS.between(hoje, m.getProximaManutencaoData());
            if (diasRestantes <= 30) {
                detalhes = "em " + diasRestantes + " dias (" + m.getProximaManutencaoData() + ")";
            }  
        } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
            double kmRestantes = m.getProximaManutencaoKm() - m.getVeiculo().getKilometragemAtual();
            if (kmRestantes <= 1000 && kmRestantes > 0) {
                detalhes = "faltam " + kmRestantes + "km";
            }
        }
        
        if (!detalhes.isEmpty()) {
            alertas.add("ℹ️ Próxima revisão do veículo " + placa + " - " + detalhes);
        }
    });

// Manutenções muito próximas (até 7 dias)
List<Manutencao> proximas7dias = repositoryManuntencao.findManutencoesProximas7Dias(hoje.plusDays(7));

proximas7dias.stream()
    .filter(m -> !m.isVencida()) // Filtra apenas não vencidas
    .forEach(m -> {
        String placa = m.getVeiculo() != null ? m.getVeiculo().getMatricula() : "Veículo não encontrado";
        String detalhes = "";
        
        if (m.getProximaManutencaoData() != null) {
            long diasRestantes = ChronoUnit.DAYS.between(hoje, m.getProximaManutencaoData());
            if (diasRestantes <= 7) {
                detalhes = "em " + diasRestantes + " dias";
            }
        } else if (m.getProximaManutencaoKm() != null && m.getVeiculo() != null) {
            double kmRestantes = m.getProximaManutencaoKm() - m.getVeiculo().getKilometragemAtual();
            if (kmRestantes <= 200 && kmRestantes > 0) {
                detalhes = "faltam " + kmRestantes + "km";
            }
        }
        
        if (!detalhes.isEmpty()) {
            alertas.add("⏰ Atenção! Veículo " + placa + " precisa de manutenção " + detalhes);
        }
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