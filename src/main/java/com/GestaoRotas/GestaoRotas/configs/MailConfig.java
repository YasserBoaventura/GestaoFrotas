package com.GestaoRotas.GestaoRotas.configs;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	
	 @Bean
	 public JavaMailSender javaMailSender() { 
	     JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	      
	     mailSender.setHost("smtp.gmail.com");
	     mailSender.setPort(465);
	     //uvei o meu proprio porque a aplicao nao tem email proprio
	     mailSender.setUsername("yasserboaventura78@gmail.com");
	     mailSender.setPassword("ljuh vqvc rcbx ptre");
	               
	     Properties props = mailSender.getJavaMailProperties();
	     props.put("mail.transport.protocol", "smtp");
	     props.put("mail.smtp.auth", "true"); 
	     props.put("mail.smtp.ssl.enable", "true");
	     props.put("mail.smtp.socketFactory.port", "465");
	     props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	     props.put("mail.smtp.socketFactory.fallback", "false");
	     
	     // Desabilitar validação SSL (APENAS DESENVOLVIMENTO!)
	     props.put("mail.smtp.ssl.trust", "*");
	     props.put("mail.smtp.ssl.checkserveridentity", "false");
	     
	     // Timeouts
	     props.put("mail.smtp.connectiontimeout", "30000");
	     props.put("mail.smtp.timeout", "30000");
	     props.put("mail.smtp.writetimeout", "30000");
	     
	     // Debug
	     props.put("mail.debug", "true");
	     
	     return mailSender;
	 }

}
