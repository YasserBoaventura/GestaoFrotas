package com.GestaoRotas.GestaoRotas.DTO;

import com.GestaoRotas.GestaoRotas.auth.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class usuarioUpdateDTO {


    
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotNull(message = "Role é obrigatório")
    private Usuario role;
    
    private String telefone;
    
    private String nuit;
    
    private boolean ativo;
    
    private boolean contaBloqueada;
}



