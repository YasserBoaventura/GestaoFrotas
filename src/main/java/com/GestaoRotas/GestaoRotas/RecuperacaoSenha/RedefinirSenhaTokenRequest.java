package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RedefinirSenhaTokenRequest {
	
	private String token;
	private String novaSenha;
	

}
