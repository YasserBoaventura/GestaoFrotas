package com.GestaoRotas.GestaoRotas.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public non-sealed class EmailService implements EmailServiceImp{
	
	private final JavaMailSender mailSender; 
 
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
	
	@Value("${spring.mail.username}")
	private String emailFrom;
	
	
public void enviarEmailRecuperacao(String destinatario, String token) {

}
public void enviarEmailConfirmacao(String destinatario, String assunto, String mensagem) {}


@Async
public void enviarBoasVindasAoUsuario(String emailDestinatario, String mensagem) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailDestinatario);
        message.setSubject("🎉 Bem-vindo ao Sistema");

        message.setText(mensagem);

        mailSender.send(message);

        logger.info("✅ Email de boas-vindas enviado para: {}", emailDestinatario);

    } catch (Exception e) {
        logger.error("❌ Erro ao enviar email: {}", e.getMessage());
    }

}
//para o envio de codigos
@Async
public void enviarCodigoVerificacao(String emailDestino, String nome, String codigo) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailDestino);
        message.setSubject("🔐 Código de Verificação - Recuperação de Senha");
        message.setText(String.format(
            "Olá %s,\n\n" +
            "Você solicitou a recuperação de senha.\n\n" +
            "Seu código de verificação é: %s\n\n" + 
            "Este código expira em 10 minutos.\n\n" +
            "Se não foi você, ignore este email.\n\n" +
            "Atenciosamente,\nSistema de Gestão de Frotas",
            nome, codigo
        ));
         
        mailSender.send(message);
        logger.info("✅ Código de verificação enviado para: {}", emailDestino);
        
    } catch (Exception e) {
        logger.error("❌ Erro ao enviar código: {}", e.getMessage());
    }
}

// Cache para controlar emails já enviados evita duplicação
private final ConcurrentHashMap<String, Long> emailsEnviados = new ConcurrentHashMap<>();

 
@Async 
public void enviarAlertaManutencao(String emailDestinatario, String placa, String detalhes) {
    // Chave única para este alerta 
    String chaveUnica = placa + "_" + detalhes + "_" + LocalDate.now();
     
    // Verifica se já enviou nas últimas 24 horas
    Long ultimoEnvio = emailsEnviados.get(chaveUnica);
    if (ultimoEnvio != null && System.currentTimeMillis() - ultimoEnvio < 86400000) { // 24h
        logger.info("⏭️ Email já enviado para {} nas últimas 24h, ignorando duplicata", placa);
        return;
    }
    
    logger.info("📧 ===== ENVIANDO EMAIL PARA {} =====", placa);
    
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom); 
        message.setTo(emailDestinatario);
        message.setFrom("yasserboaventura78@gmail.com");
        message.setSubject("🔧 Alerta de Manutenção - Veículo " + placa);
        message.setText(
            "Prezado(a),\n\n" +
            "O veículo de placa " + placa + " possui uma manutenção programada.\n\n" +
            "Detalhes: " + detalhes + "\n\n" +
            "Por favor, agende a manutenção o quanto antes.\n\n" +
            "Atenciosamente,\nSistema de Gestão de Frotas"
        );
        
        mailSender.send(message);
          
        // Registra o envio bem-sucedido
        emailsEnviados.put(chaveUnica, System.currentTimeMillis());
        logger.info(" EMAIL ENVIADO com sucesso para veículo {}", placa);
        
    } catch (MailException e) {
        logger.error(" Falha no envio de email para {}: {}", placa, e.getMessage());
    }
}

@Async
public void enviarAlertaManutencaoVencida(String emailDestinatario, String placa, String detalhes) {
    String chaveUnica = placa + "_VENCIDA_" + LocalDate.now();
    
    if (emailsEnviados.containsKey(chaveUnica)) {
        logger.info("⏭️ Alerta vencido já enviado para {}", placa);
        return;
    }
    
    logger.info("📧 ===== ENVIANDO ALERTA VENCIDO PARA {} =====", placa);
    
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestinatario);
        message.setFrom(emailFrom);
        message.setSubject("🔴 ALERTA: Manutenção VENCIDA - Veículo " + placa);
        message.setText(
            "Prezado(a),\n\n" +
            "O veículo de placa " + placa + " está com manutenção VENCIDA!\n\n" +
            "Detalhes: " + detalhes + "\n\n" +
            "Ação imediata é necessária!\n\n" +
            "Atenciosamente,\nSistema de Gestão de Frotas"
        );
        
        mailSender.send(message);
        emailsEnviados.put(chaveUnica, System.currentTimeMillis());
        logger.info(" ALERTA VENCIDO enviado para {}", placa);
        
    } catch (MailException e) {
        logger.error(" Falha no envio de alerta vencido: {}", e.getMessage());
    }
}

@Async
public void enviarNotificacaoDaViagemMotorista(String emailDestinatario, String viagemRef, String detalhes) {
    // Chave única para este alerta (viagem + semana)
    String chaveUnica = viagemRef + "_SEMANA_" + LocalDate.now();
      
    // Verifica se já enviou nas últimas 24 horas
    Long ultimoEnvio = emailsEnviados.get(chaveUnica);
    if (ultimoEnvio != null && System.currentTimeMillis() - ultimoEnvio < 86400000) {
        logger.info("⏭️ Email já enviado para viagem {} nas últimas 24h, ignorando duplicata", viagemRef);
        return;
    }
    
    logger.info("📧 ===== ENVIANDO EMAIL DE VIAGEM PARA {} =====", emailDestinatario);
    
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailDestinatario);
        message.setSubject("🚚 Programação de Viagens - " + viagemRef);
        message.setText(detalhes);
        
        mailSender.send(message);
          
        // Registra o envio bem-sucedido
        emailsEnviados.put(chaveUnica, System.currentTimeMillis());
        logger.info("✅ EMAIL DE VIAGEM enviado com sucesso para {}", emailDestinatario);
        
    } catch (MailException e) {
        logger.error(" Falha no envio de email para {}: {}", emailDestinatario, e.getMessage());
    }
}
}