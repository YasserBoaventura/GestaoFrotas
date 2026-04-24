//AuthenticationService.java
package com.GestaoRotas.GestaoRotas.auth;
import java.beans.Transient;
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
import org.springframework.web.bind.annotation.RequestBody;

import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.UserSaveDTO;
import com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.config.JwtServiceGenerator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class LoginService {
   

	private final LoginRepository repository;
	
	private final JwtServiceGenerator jwtService;
	
	private final AuthenticationManager authenticationManager;
	 
	private final PasswordEncoder passwordEncoder;
	
	private final EmailService emailService; 
	

	public String logar(@Valid Login login) {
	    Usuario user = repository.findByUsername(login.getUsername())
	            .orElseThrow(() ->  new RuntimeException("Usuário não encontrado"));
        if(user.getPrimeiroLogin()) {
        	throw new RuntimeException("PRIMEIRO_LOGIN!");  
        }
	  
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
	//funcao pra trocar a senha se  for o primerio login
	@Transactional  
	public String trocarSenha(@Valid trocarSenhaDTO dto) {
	    Usuario user = repository.findByUsername(dto.getUsername())
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            // validar senha atual
	    if (!passwordEncoder.matches(dto.getSenhaAtual(), user.getPassword())) {
	        throw new RuntimeException("Senha atual inválida");
	    }// validar nova senha
	    if (dto.getNovaSenha().length() < 6) {
	        throw new RuntimeException("Senha deve ter no mínimo 6 caracteres");
	    }
    if(!dto.getNovaSenha().equalsIgnoreCase(dto.getConfirmarSenha())) {
	    throw new RuntimeException("Senhas não coincidem");   
 }
	    // atualizar senha
	    user.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
       // remover flag de primeiro login
	    user.setPrimeiroLogin(false);
          repository.save(user);
       return "Senha alterada com sucesso";
	}
	
	
// Como pegar os dados do usuario a se cadastrar
	@Transactional 
	public String registar(@Valid Usuario userSave) { 
	    Usuario usuario = new Usuario(); 
        usuario.setUsername(userSave.getUsername());
	    usuario.setEmail(userSave.getEmail());       
	    usuario.setNuit(userSave.getNuit());
	    usuario.setContaBloqueada(userSave.getContaBloqueada()); 
	    usuario.setTelefone(userSave.getTelefone());
	    usuario.setDataNascimento(userSave.getDataNascimento());   
	    usuario.setRole(userSave.getRole()); 
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setTentativasLogin(0);  
        usuario.setPerguntaSeguranca(userSave.getPerguntaSeguranca());
        usuario.setRespostaSeguranca(userSave.getRespostaSeguranca());
		usuario.setContaBloqueada(false);
		usuario.setPrimeiroLogin(true); 
		usuario.setPassword(passwordEncoder.encode("0000")); 
	    repository.save(usuario);

       emailService.enviarBoasVindasAoUsuario(
	        usuario.getEmail(),
	        "Olá " + usuario.getUsername() +
	        ",\n\nSeja bem-vindo ao Sistema de Gestão de Frotas.\n" +
	        "Sua conta foi criada com sucesso.\n\n" +
	        "  Com o Password 0000, Por Favor Faca suas alteracoes .\nEquipe do Sistema"
	    );

	    return "Usuário salvo com sucesso!";
	}  
	@Transactional
	   public ResponseEntity<?> autoCadastro(@Valid AutoCadastroDTO dto) {
	       // Verificar se username, email ou nuit já existem
	   if (repository.existsByUsername(dto.getUsername())) {
	       return ResponseEntity.badRequest().body("Username já está em uso");
	   }
	   if (repository.existsByEmail(dto.getEmail())) {
	       return ResponseEntity.badRequest().body("Email já está em uso");
	   }
	   if (repository.existsByNuit(dto.getNuit())) {
	       return ResponseEntity.badRequest().body("NUIT já está em uso");
	   }
	  
	   // Criar novo usuário com os dados do DTO   
	   Usuario usuario = new Usuario();  
	   usuario.setUsername(dto.getUsername());
	   usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
	   usuario.setEmail(dto.getEmail());
	   usuario.setPerguntaSeguranca(dto.getPerguntaSeguranca());
	   usuario.setRespostaSeguranca(dto.getRespostaSeguranca());
	   usuario.setTelefone(dto.getTelefone());
	   usuario.setNuit(dto.getNuit());
	   usuario.setDataNascimento(dto.getDataNascimento());
	   
	   // Definir valores padrão 
	       usuario.setRole("USER"); // Cargo padrão
	       usuario.setAtivo(false); // Conta desativada até ativação pelo admin
		   usuario.setDataCriacao(LocalDateTime.now());
		   usuario.setTentativasLogin(0);
		   usuario.setContaBloqueada(false);
		 //save
		   repository.save(usuario);  
		    // enviar boas vindas ao usuario 
		   emailService.enviarBoasVindasAoUsuario(
				    usuario.getEmail(),
				    "Olá " + usuario.getUsername() +  
				    ",\n\nSeja bem-vindo ao Sistema de Gestão de Frotas.\n" +
				    "Sua conta foi criada com sucesso.\n\n" +
				    "Atenciosamente, Por Favor Aguarde ativação da conta por um administrador.,\nEquipe do Sistema"
				);
		   return ResponseEntity.ok("Cadastro realizado com sucesso. Aguarde ativação da conta por um administrador.");
	   } 
   //Apenas o adminstrador pode fazer alteracoes nos usuarios
	@Transactional 
	public Usuario atualizarUsuario(Long id, @Valid Usuario usuarioAtualizado) {
	    Usuario usuarioExistente = this.repository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
	    
	    // Atualizar apenas campos permitidos        
	    usuarioExistente.setUsername(usuarioAtualizado.getUsername());
	    usuarioExistente.setEmail(usuarioAtualizado.getEmail()); 
	    usuarioExistente.setRole(usuarioAtualizado.getRole());
	    usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
	    usuarioExistente.setNuit(usuarioAtualizado.getNuit());
	    usuarioExistente.setAtivo(usuarioAtualizado.isEnabled());
	    usuarioExistente.setDataNascimento(usuarioAtualizado.getDataNascimento()); 
	    usuarioExistente.setContaBloqueada(usuarioAtualizado.getContaBloqueada());
	         
	     
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
		return repository.findAll();
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
