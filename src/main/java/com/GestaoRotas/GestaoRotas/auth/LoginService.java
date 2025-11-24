//AuthenticationService.java
package com.GestaoRotas.GestaoRotas.auth;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.config.JwtServiceGenerator;

@Service
public class LoginService {
   

	private final LoginRepository repository;
	
	private final JwtServiceGenerator jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	private final PasswordEncoder passwordEncoder;
	
	  public LoginService (LoginRepository repo,JwtServiceGenerator jwtServiceGenerator, AuthenticationManager AuthenticationManger, PasswordEncoder passwordEncoder) {
		this.repository=repo;
		this.jwtService=jwtServiceGenerator;
	    this.authenticationManager=AuthenticationManger;
	    this.passwordEncoder=passwordEncoder;
	}

	public String logar(Login login) {

		String token = this.gerarToken(login);
	
		return token;

	} 
// Como pegar os dados do usuario a se cadastrar
	public String registar(Usuario usuario) { 
   String passwordEncoderStrings = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(passwordEncoderStrings);
		   this.repository.save(usuario);
				return "Usuario Salvo com sucesso"; 
	}  
 
	public List<Usuario> findAll(){
		List<Usuario> lista= new  ArrayList<>();
		return  lista=this.repository.findAll();
	}
	public String gerarToken(Login login) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						login.getUsername(),
						login.getPassword()
						)
				);
		Usuario user = repository.findByUsername(login.getUsername());
		String jwtToken = jwtService.generateToken(user);
		return jwtToken;
	}
	
	
	
 

}
