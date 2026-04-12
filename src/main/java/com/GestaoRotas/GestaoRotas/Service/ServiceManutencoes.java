package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.GestaoRotas.GestaoRotas.Custos.custoService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.concluirManutencaoRequest;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.Email.EmailServiceImp;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
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
	private final ServiceVeiculo veiculoService; 	
	private final custoService custoService; 
	private final EmailService emailService;
    
 
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
    Manutencao saved =  repositoryManuntencao.save(manutencao);
    custoService.criarCustoParaManutencao(saved); 
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
  public String updateL(manuntecaoDTO manutencaoDTO, long id)  { 
	   
	    Manutencao manutencao = repositoryManuntencao.findById(id).orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));
	    Veiculo veiculo = repositoryVeiculo.findById(manutencaoDTO.getVeiculo_id()).orElseThrow(()-> new RuntimeException("Veiculo nao encontrado"));
	    System.out.println(manutencaoDTO.getVeiculo_id()); 
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
       manutencao.setStatus(manutencaoDTO.getStatus());
       
     Manutencao manutencaoActualizada =  repositoryManuntencao.save(manutencao);
     //metodo pra actualizacao de manutencao em custos
       custoService.actualizarCustoManutencao(manutencaoActualizada);  
      
    return "manutencao atualizada com sucesso";
  } 
  public String update(manuntecaoDTO manutencaoDTO, long id) { 

	    Manutencao manutencao = repositoryManuntencao
	        .findById(id)
	        .orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));

	    Veiculo veiculo = manutencao.getVeiculo(); // usa o veículo existente

	    manutencao.setDataManutencao(manutencaoDTO.getDataManutencao());
	    manutencao.setDescricao(manutencaoDTO.getDescricao());
	    manutencao.setKilometragemVeiculo(manutencaoDTO.getKilometragemVeiculo());
	    manutencao.setProximaManutencaoData(manutencaoDTO.getProximaManutencaoData());
	    manutencao.setTipoManutencao(manutencaoDTO.getTipoManutencao());
	    manutencao.setCusto(manutencaoDTO.getCusto());
	    manutencao.setProximaManutencaoKm(manutencaoDTO.getProximaManutencaoKm());

	    Manutencao manutencaoActualizada = repositoryManuntencao.save(manutencao);

	    custoService.actualizarCustoManutencao(manutencaoActualizada);

	    return "manutencao atualizada com sucesso";
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

  repositoryManuntencao.save(manutencao);

  
  System.out.println("Status atualizado para AGENDADA_HOJE na manutenção ID: " + manutencao.getId());
  

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
    	  
          //  por emplemntar notificacoes de manha por email com o JavaMailSender
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
	  TipoManutencao tipoEnum = TipoManutencao.valueOf(tipo.toUpperCase());
     return repositoryManuntencao.findBytipoManutencao(tipoEnum);
 }  
 //relatorio de manuntencoes feitas por cada veiculo
 public List<RelatorioManutencaoDTO> gerarRelatorioPorVeiculo() {
     return  repositoryManuntencao.relatorioPorVeiculo();
    }  
 
//relatorios por periodo data fim e data inicio 
public List<RelatorioManutencaoDTO> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
  return repositoryManuntencao.relatorioPorPeriodo(inicio, fim);  
       
} 
@Scheduled(cron = "0 0 0 * * *") 
public List<String> gerarAlertas() { 
List<String> alertas = new ArrayList<>();
LocalDate hoje = LocalDate.now();
Set<String> emailsDisparados = new HashSet<>(); // Controle local

try {
    // 1. Manutenções vencidas
    List<Manutencao> manutencoesVencidas = repositoryManuntencao.findManutencoesVencidas();
if (manutencoesVencidas != null) {
    for (Manutencao m : manutencoesVencidas) {  
        try {  
            if (m == null || m.getVeiculo() == null) continue;
            
            String placa = m.getVeiculo().getMatricula();
            String emailResp = m.getVeiculo().getEmailResponsavel();
            String detalhes = "";
        boolean deveEnviarEmail = false;
        
        if (m.getProximaManutencaoData() != null && m.getProximaManutencaoData().isBefore(hoje)) {
            long diasAtraso = ChronoUnit.DAYS.between(m.getProximaManutencaoData(), hoje);
            detalhes = "atrasada há " + diasAtraso + " dias";
            deveEnviarEmail = true;
        } else if (m.getProximaManutencaoKm() != null && 
                   m.getVeiculo().getKilometragemAtual() != null &&
                   m.getVeiculo().getKilometragemAtual() >= m.getProximaManutencaoKm()) {
            double kmExcedido = m.getVeiculo().getKilometragemAtual() - m.getProximaManutencaoKm();
            detalhes = "excedeu " + kmExcedido + "km";
            deveEnviarEmail = true;
        }
        
if (m.getDataManutencao() != null && 
    m.getDataInicio() == null && 
    m.getDataConclusao() == null) {
    
    alertas.add("⚠️ Revisão vencida do veículo " + placa + " - " + detalhes);
    
    // ENVIA EMAIL APENAS UMA VEZ POR VEÍCULO 
    String chave = "VENCIDA_" + placa;
    if (deveEnviarEmail && emailResp != null && !emailsDisparados.contains(chave)) {
        emailService.enviarAlertaManutencaoVencida(emailResp, placa, detalhes);
        emailsDisparados.add(chave);
    }
                }
        } catch (Exception e) {
            // Ignora erro e continua
            }
        }
    }
    
    // 2. Próximas manutenções (30 dias)
    List<Manutencao> proximas30dias = repositoryManuntencao.findProximasManutencoes(hoje.plusDays(30));
if (proximas30dias != null) {
    for (Manutencao m : proximas30dias) {
        try {
            if (m == null || m.getVeiculo() == null) continue;
             
            String placa = m.getVeiculo().getMatricula();
            String emailResp = m.getVeiculo().getEmailResponsavel();
            String detalhes = "";
    boolean deveEnviarEmail = false;
    
    if (m.getProximaManutencaoData() != null) {
        long diasRestantes = ChronoUnit.DAYS.between(hoje, m.getProximaManutencaoData());
        if (diasRestantes <= 30 && diasRestantes > 0) {
            detalhes = "em " + diasRestantes + " dias";
            
            // ALERTA DE 10 DIAS apenas uma vez
            if (diasRestantes == 10) {
                alertas.add("📅 Alerta: Veículo " + placa + " tem manutenção em 10 dias");
                String chave = "10DIAS_" + placa;
                if (emailResp != null && !emailsDisparados.contains(chave)) {
                    deveEnviarEmail = true;
                    emailsDisparados.add(chave);
                }
            }
        }
    } else if (m.getProximaManutencaoKm() != null && 
               m.getVeiculo().getKilometragemAtual() != null) {
        double kmRestantes = m.getProximaManutencaoKm() - m.getVeiculo().getKilometragemAtual();
        if (kmRestantes <= 1000 && kmRestantes > 0) {
            detalhes = "faltam " + kmRestantes + "km";
            
            if (kmRestantes <= 200) {
                alertas.add("⛽ Alerta: Veículo " + placa + " - " + detalhes);
                String chave = "KM_" + placa;
                if (emailResp != null && !emailsDisparados.contains(chave)) {
                    deveEnviarEmail = true;
                    emailsDisparados.add(chave);
                }
            }
        }
    }

    if (!detalhes.isEmpty()) {
        alertas.add("ℹ️ Próxima revisão do veículo " + placa + " - " + detalhes);
        
        if (deveEnviarEmail && emailResp != null) {
            emailService.enviarAlertaManutencao(emailResp, placa, detalhes);
        }
    }
} catch (Exception e) {
    // Ignora erro
    }
        }
    }
    
    // 3. Manutenções próximas (7 dias)
List<Manutencao> proximas7dias = repositoryManuntencao.findManutencoesProximas7Dias(hoje.plusDays(7));
if (proximas7dias != null) {
    for (Manutencao m : proximas7dias) {
        try {
            if (m == null || m.getVeiculo() == null || m.isVencida()) continue;
    
    String placa = m.getVeiculo().getMatricula();
        String emailResp = m.getVeiculo().getEmailResponsavel();
        String detalhes = "";
    boolean deveEnviarEmail = false;
    
    if (m.getProximaManutencaoData() != null) {
        long diasRestantes = ChronoUnit.DAYS.between(hoje, m.getProximaManutencaoData());
        if (diasRestantes <= 7 && diasRestantes > 0) {
            detalhes = "em " + diasRestantes + " dias";
            
            if (diasRestantes <= 3) {
                alertas.add("🔴 URGENTE: Veículo " + placa + " - " + detalhes);
            String chave = "URGENTE_" + placa;
            if (emailResp != null && !emailsDisparados.contains(chave)) {
                deveEnviarEmail = true;
                emailsDisparados.add(chave);
            }
        }
    }
}
    
   if (!detalhes.isEmpty() && !detalhes.contains("URGENTE")) {
    alertas.add("🔔 Revisão próxima do veículo " + placa + " - " + detalhes);
        }
        
        if (deveEnviarEmail && emailResp != null) {
            emailService.enviarAlertaManutencao(emailResp, placa, "URGENTE: " + detalhes);
        }
        
    } catch (Exception e) {
        // Ignora erro
            }
        }
    }
    
} catch (Exception e) {
    alertas.add("Não foi possível carregar todos os alertas");
}

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