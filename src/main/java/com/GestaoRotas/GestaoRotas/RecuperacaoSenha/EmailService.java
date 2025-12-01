package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public interface EmailService  {
	 
	//para enviar o token 
    void enviarEmailRecuperacao(String destinatario, String token);
    
    void enviarEmailConfirmacao(String destinatario, String assunto, String mensagem);
    void enviarEmailSimples(String destinatario, String assunto, String mensagem);
}
   