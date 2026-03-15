package com.GestaoRotas.GestaoRotas.Email;

import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class AgendamentoEmailService {

    
    private static final Logger logger = LoggerFactory.getLogger(AgendamentoEmailService.class);
    

    private  final RepositoryViagem viagemRepository;
    
    
    private final EmailService emailService;
    
    // Executa todo sábado às 8:00 da manhã
@Scheduled(cron = "0 0 8 * * SAT")
public void enviarEmailsViagensSemana() {
    logger.info("📅 ===== INICIANDO ENVIO DE EMAILS DE VIAGENS (SÁBADO) =====");
    
    LocalDate hoje = LocalDate.now();
    LocalDate inicioSemana = hoje.plusDays(1); // Domingo
    LocalDate fimSemana = hoje.plusDays(7);    // Próximo sábado
    
    logger.info("Período: {} a {}", inicioSemana, fimSemana);
    
    try {
        // Busca todas as viagens da próxima semana
        List<Viagem> viagensSemana = viagemRepository.findViagensEntreDatas(inicioSemana, fimSemana);
    
    if (viagensSemana.isEmpty()) {
        logger.info("Nenhuma viagem agendada para a próxima semana");
        return;
    }
    
    logger.info("Encontradas {} viagens para a próxima semana", viagensSemana.size());
    
    // Agrupa por motorista e envia emails
    for (Viagem  viagem : viagensSemana) {
        try {
            enviarEmailViagemMotorista(viagem);
        } catch (Exception e) {
            logger.error("Erro ao enviar email para viagem ID {}: {}", 
                viagem.getId(), e.getMessage());
        }
        }
        
        logger.info("✅ Processo de envio de emails concluído!");
        
    } catch (Exception e) {
        logger.error("❌ Erro no agendamento de emails: {}", e.getMessage());
    }
    }
    
    private void enviarEmailViagemMotorista(Viagem viagem) {
        // Verifica se tem motorista e email
        if (viagem.getMotorista() == null || viagem.getMotorista().getEmail() == null) {
            logger.warn("Viagem ID {} não tem motorista ou email cadastrado", viagem.getId());
            return;
        }
        
        String emailMotorista = viagem.getMotorista().getEmail();
        String nomeMotorista = viagem.getMotorista().getNome();
        
        // Formata os dados da viagem
        String assunto = "🚚 Suas viagens da próxima semana - " + nomeMotorista;
        
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Olá ").append(nomeMotorista).append(",\n\n");
        detalhes.append("Aqui estão suas viagens programadas para a próxima semana:\n\n");
        
        // Formata a viagem atual
        String dataFormatada = viagem.getData() != null ? 
            viagem.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Data não definida";
        
        detalhes.append("📅 Data: ").append(dataFormatada).append("\n");
        detalhes.append("📍 Origem: ").append(viagem.getRota().getOrigem() != null ? viagem.getRota().getOrigem() : "Não definida").append("\n");
        detalhes.append("🏁 Destino: ").append(viagem.getRota().getDescricao() != null ? viagem.getRota().getDestino() : "Não definido").append("\n");
        detalhes.append("🚛 Veículo: ").append(viagem.getVeiculo() != null ? 
            viagem.getVeiculo().getMatricula() : "Não definido").append("\n");
        detalhes.append("📦 Carga: ").append(viagem.getTipoCarga() != null ? viagem.getTipoCarga() : "Não definida").append("\n");
        detalhes.append("📏 Distância: ").append(viagem.getDistanciaPercorrida()  != null ? 
            viagem.getDistanciaPercorrida() + " km" : "Não definida").append("\n");
        
        if (viagem.getObservacoes() != null && !viagem.getObservacoes().isEmpty()) {
            detalhes.append("📝 Observações: ").append(viagem.getObservacoes()).append("\n");
        }
        
        detalhes.append("\n---\n\n");
        detalhes.append("Por favor, confirme sua disponibilidade.\n\n");
        detalhes.append("Atenciosamente,\nSistema de Gestão de Frotas");
        
        // Envia o email usando o serviço existente
        emailService.enviarNotificacaoDaViagemMotorista(
            emailMotorista,
            "Viagem " + viagem.getId(),
            detalhes.toString()
        );
        
        logger.info("Email enviado para motorista {} - Viagem ID {}", nomeMotorista, viagem.getId());
    }
     
    // Método para envio manual (útil para testes)
    public void enviarEmailManual(Long viagemId) {
        Viagem viagem = viagemRepository.findById(viagemId).orElse(null);
        if (viagem != null) {
            enviarEmailViagemMotorista(viagem);
        }
    }

}
