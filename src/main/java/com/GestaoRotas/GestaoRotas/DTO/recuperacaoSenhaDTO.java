package com.GestaoRotas.GestaoRotas.DTO;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class recuperacaoSenhaDTO {
	
	    private String username; 
	    private String email;
	    private String nuit;
	    private String respostaSeguranca;
	    private String novaSenha;
	    private String token;  
	    private String codigoVerificacao;
	   

    
	       
}


