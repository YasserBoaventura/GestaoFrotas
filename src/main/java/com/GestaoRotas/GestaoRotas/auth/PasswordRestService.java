package com.GestaoRotas.GestaoRotas.auth;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordRestService {
	
	private final LoginRepository loginRepository;
	private final PasswordEncoder passwordEncoder;
	
	public PasswordRestService(LoginRepository loginRepository,  PasswordEncoder passwordEncoder){
		this.loginRepository=loginRepository;
		this.passwordEncoder=passwordEncoder;
	}
	   
	//Faz a requisao para a recuperacao da senha
	public String requestPassorRest(String username) {
		Usuario user = this.loginRepository.findByUsername(username);	
		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		loginRepository.save(user);    
		return token; 
	          
	}       
	//reneviar a senha
	public void restPassword(String token , String newPassword) {
		Usuario user = this.loginRepository.findByResetToken(token);
		if(user==null) {
			throw new RuntimeException("Invalid token!");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
	    user.setResetToken(token);
		loginRepository.save(user);	     
	}  
	    

}
