package com.GestaoRotas.GestaoRotas.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserSaveDTO {


	 @NotBlank
	    private String username;

	    @Email
	    @NotBlank
	    private String email;

	    @NotBlank
	    private String role; // 🔥 igual ao Angular

	    @NotBlank
	    private String telefone;

	    @NotBlank
	    private String nuit;

	    private boolean contaBloqueada;

}