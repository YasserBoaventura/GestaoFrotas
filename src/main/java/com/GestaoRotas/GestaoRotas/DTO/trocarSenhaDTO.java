package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter 
public class trocarSenhaDTO {

	    private String username;
	    private String senhaAtual;
	    private String novaSenha;
	    private String confirmarSenha; 
	
}
