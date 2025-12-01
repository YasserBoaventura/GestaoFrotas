package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoCadastroDTO {

	
	    private String username;
	    private String password;
	    private String email;
	    private String perguntaSeguranca;
	    private String respostaSeguranca;
	    private String telefone;
	    private String nuit;
	    private LocalDateTime dataNascimento;
}
