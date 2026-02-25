package com.GestaoRotas.GestaoRotas.CustoDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerificarCodigoDTO {

	    private String username;
	    private String email;
	    private String codigo;
	    private String novaSenha;
	    private String respostaSeguranca;
	    private String nuit;
}
