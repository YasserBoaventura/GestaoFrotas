package com.GestaoRotas.GestaoRotas.Email;

import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


public sealed interface EmailService permits EmailService_Impl{
	//para enviar o token
    void enviarEmailRecuperacao(String destinatario, String token);
    void enviarCodigoVerificacao(String emailDestino, String nome, String codigo);
    
   //servico de envio de email de alertas de manutencao
    void enviarAlertaManutencao(String emailDestinatario, String placa, String detalhes); 
    void enviarAlertaManutencaoVencida(String emailDestinatario, String placa, String detalhes);
     //enviar as viagens dos motoristas
    void enviarNotificacaoDaViagemMotorista(String destinatario, String viagem, String detalhes);

    void enviarBoasVindasAoUsuario(String emailDestinatario, String mensagem);

    void enviarNotificacaoManutencaoDoProximoDia(String destinatario,
                                                        String matricula,
                                                        TipoManutencao tipoManutencao,
                                                        LocalDate dataManutencao);
}
