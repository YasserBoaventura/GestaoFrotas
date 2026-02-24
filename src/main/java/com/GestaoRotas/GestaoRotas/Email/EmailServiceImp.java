package com.GestaoRotas.GestaoRotas.Email;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@Service
@Primary  
public sealed interface EmailServiceImp permits EmailService {
	//para enviar o token 
    void enviarEmailRecuperacao(String destinatario, String token);
    
    void enviarEmailConfirmacao(String destinatario, String assunto, String mensagem);
    void enviarEmailSimples(String destinatario, String assunto, String mensagem);
   //servico de envio de email de alertas de manutencao
    void enviarAlertaManutencao(String emailDestinatario, String placa, String detalhes); 
    void enviarAlertaManutencaoVencida(String emailDestinatario, String placa, String detalhes);

     
}
