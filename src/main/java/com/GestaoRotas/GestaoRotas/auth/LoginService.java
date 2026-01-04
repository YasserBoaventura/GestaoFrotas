//AuthenticationService.java
package com.GestaoRotas.GestaoRotas.auth;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.config.JwtServiceGenerator;

import jakarta.persistence.EntityNotFoundException;

@Service
public class LoginService {
   

	private final LoginRepository repository;
	
	private final JwtServiceGenerator jwtService;
	
	private final AuthenticationManager authenticationManager;
	 
	private final PasswordEncoder passwordEncoder;
	@Autowired
	  public LoginService (LoginRepository repo,JwtServiceGenerator jwtServiceGenerator, AuthenticationManager AuthenticationManger, PasswordEncoder passwordEncoder) {
		this.repository=repo;
		this.jwtService=jwtServiceGenerator;
	    this.authenticationManager=AuthenticationManger;
	    this.passwordEncoder=passwordEncoder;
	}

	public String logar(Login login) {
	    Usuario user = repository.findByUsername(login.getUsername())
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	    // Verifica se a conta está bloqueada
	    if (user.getTentativasLogin() == 5) {   // Supondo que tenha um método getter
	        throw new RuntimeException("Conta bloqueada devido a múltiplas tentativas de login");
	    }
	    if(!user.isAccountNonLocked()) {
	    	  throw new RuntimeException("Conta bloqueada");
	    }
	    if(!user.isEnabled()) {
	    	throw new RuntimeException("conta desativada solicite um administrador");
	    }
          try {
	        // Tenta autenticar (gerar token)
	        String token = this.gerarToken(login);
	        // Se chegou aqui, login foi bem-sucedido
	        // Reseta as tentativas de login
	        user.setTentativasLogin(0);   
	         
	        user.setUltimoAcesso(LocalDateTime.now());
	        this.repository.save(user);  
	        
	        return token;
	    } catch (Exception e) {
	        // Login falhou - incrementa tentativas
	        user.incrementarTentativasLogin();
	        this.repository.save(user);
	        
	        throw new RuntimeException("Credenciais inválidas");
	    }
	} 
// Como pegar os dados do usuario a se cadastrar
	public String registar(Usuario usuario) { 
   String passwordEncoderStrings = passwordEncoder.encode(usuario.getPassword());
		usuario.setPassword(passwordEncoderStrings);
		   this.repository.save(usuario);
				return "Usuario Salvo com sucesso"; 
	}  
   //Apenas o adminstrador pode fazer alteracoes nos usuarios
	public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
	    Usuario usuarioExistente = this.repository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
	    
	    // Atualizar apenas campos permitidos
	    usuarioExistente.setUsername(usuarioAtualizado.getUsername());
	    usuarioExistente.setEmail(usuarioAtualizado.getEmail());
	    usuarioExistente.setRole(usuarioAtualizado.getRole());
	    usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
	    usuarioExistente.setNuit(usuarioAtualizado.getNuit());
	    usuarioExistente.setAtivo(usuarioAtualizado.isEnabled());
	    usuarioExistente.setContaBloqueada(usuarioAtualizado.getContaBloqueada());
	    
	    // NÃO atualizar campos sensíveis
	    // usuarioExistente.setPassword(usuarioAtualizado.getPassword()); // REMOVER
	    // usuarioExistente.setDataCriacao(usuarioAtualizado.getDataCriacao()); // REMOVER
	    // outros campos como reset_token, etc.
	    
	    return repository.save(usuarioExistente);
	}
	//Bloquear/Desbloquar
public Map<String , String> bloquearConta(long id){
	Map<String, String> response = new HashMap<>();
	Usuario usuario = repository.findById(id).orElseThrow(()->new RuntimeException(" usuario nao econtrado"));
  // desbloquar se estiver bloqueado
	if(usuario.getContaBloqueada() == true) {
	   Map<String, String> contaDesbloqueada = new HashMap<>();
	   usuario.setContaBloqueada(false);
	   repository.save(usuario); 
	   contaDesbloqueada.put("sucesso", "conta desbloqueada com sucesso");
	   return contaDesbloqueada; 
   } 
   else {
	usuario.setContaBloqueada(true);   
	repository.save(usuario);
 response.put("sucesso", "conta bloqueada com suceso");
	return response;  
   }
} 
//desativar/ativar
public Map<String, String > desativarConta(long id){
	Map<String , String> contaAtivada = new HashMap<>();
	Usuario usuario = repository.findById(id).orElseThrow(()-> new RuntimeException("usuario nao encontrado"));
    // desativar a conta se estiver ativa     
	if(usuario.getAtivo() == true) {
		Map<String , String> contaDesativada = new  HashMap<>();
		contaDesativada.put("sucesso", "conta desativada com sucesso");
				usuario.setAtivo(false);
        	 repository.save(usuario);
        	 return contaDesativada;
       } else {
    	   usuario.setAtivo(true); 
    	   repository.save(usuario);
        	 contaAtivada.put("sucesso", "conta ativada com sucesso");
        	 return  contaAtivada;
         }
	 
}
	
	//metodo para listar
	public List<Usuario> findAll(){
		List<Usuario> lista= new  ArrayList<>();
		return  lista= repository.findAll();
	}
	//Metodo pra iliminar usuario
	public String delete(long id) {
		this.repository.deleteById(id);
		return "Usuario deletado com sucesso";
	}
	
	
	
	//Metodo para gerar token
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
	        //validar se o usuario esta bloqueada
	        else if(user.getContaBloqueada()==true) {
	         throw new DisabledException("Conta bloqueada porfavor entre em Contato com o administrador: "+login.getUsername());	
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
