//AuthenticationService.java
package com.GestaoRotas.GestaoRotas.auth;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.authConfig.JwtServiceGenerator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


public  sealed interface LoginService permits  LoginService_Impl{


 String logar(@Valid Login login) ;
String trocarSenha(@Valid trocarSenhaDTO dto);
	
	


	 String registar(@Valid Usuario userSave);
 ResponseEntity<?> autoCadastro(@Valid AutoCadastroDTO dto) ;

 Usuario atualizarUsuario(Long id, @Valid Usuario usuarioAtualizado);

 Map<String , String> bloquearConta(long id);

 Map<String, String > desativarEndActivarConta(long id);
	
 List<Usuario> findAll();

	 String delete(long id) ;

 String gerarToken(Login login) ;


}
