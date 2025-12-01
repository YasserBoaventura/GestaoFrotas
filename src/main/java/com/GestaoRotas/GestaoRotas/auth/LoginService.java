//AuthenticationService.java
package com.GestaoRotas.GestaoRotas.auth;
import java.util.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
	    try {
	        // 1. Autenticação com tratamento de erro
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                login.getUsername(),
	                login.getPassword()
	            )
	        );
	        
	        // 2. Buscar usuário com tratamento de Optional
	        Usuario user = repository.findByUsername(login.getUsername())
	            .orElseThrow(() -> new UsernameNotFoundException(
	                "Usuário não encontrado: " + login.getUsername()));
	        
	        // 3. Validar se o usuário está ativo
	        if (!user.isEnabled()) {
	            throw new DisabledException("Usuário desativado: " + login.getUsername());
	        }
	         
	        // 4. Gerar token JWT
	        String jwtToken = jwtService.generateToken(user);
	        
	        // 5. Registrar login bem-sucedido (opcional)
	        
	        
	        return jwtToken;
	        
	    } catch (BadCredentialsException e) {
	       
	        throw new BadCredentialsException("Credenciais inválidas para usuário: " + login.getUsername());
	    } catch (DisabledException e) {
	        throw new DisabledException("Conta desativada: " + login.getUsername());
	    } catch (LockedException e) {
	        throw new LockedException("Conta bloqueada: " + login.getUsername());
	    }
	}
	
	
	
 

}
