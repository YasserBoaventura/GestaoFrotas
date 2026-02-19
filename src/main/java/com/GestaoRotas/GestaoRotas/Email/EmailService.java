package com.GestaoRotas.GestaoRotas.Email;

import org.springframework.stereotype.Service;

@Service
public class EmailService  implements EmailServiceImp{
	
	
	    private final JavaMailSe;
	    
	    public void enviarAlertaManutencao(String emailDestinatario, String placa, String detalhes) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(emailDestinatario);
	        message.setSubject("⚠️ Alerta de Manutenção - Veículo " + placa);
	        message.setText(String.format(
	            "Prezado(a),\n\n" +
	            "O veículo de placa %s possui uma manutenção programada para os próximos dias.\n\n" +
	            "Detalhes: %s\n\n" +
	            "Por favor, agende a manutenção o quanto antes para evitar problemas.\n\n" +
	            "Atenciosamente,\nSistema de Gestão de Frotas",
	            placa, detalhes
	        ));
	        
	        mailSender.send(message);
	    }
	    
	    public void enviarAlertaManutencaoVencida(String emailDestinatario, String placa, String detalhes) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(emailDestinatario);
	        message.setSubject("🔴 ALERTA: Manutenção Vencida - Veículo " + placa);
	        message.setText(String.format(
	            "Prezado(a),\n\n" +
	            "O veículo de placa %s está com manutenção VENCIDA!\n\n" +
	            "Detalhes: %s\n\n" +
	            "Ação imediata é necessária!\n\n" +
	            "Atenciosamente,\nSistema de Gestão de Frotas",
	            placa, detalhes
	        ));
	        
	        mailSender.send(message);
	    }
	}

}
