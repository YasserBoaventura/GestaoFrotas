package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor 
public final class RecuperacaoSenhaService  {
	
	  
	//Ja que nao possuo o usuarioRepository uso o loginRepository
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
     
  

public Map<String, String> solicitarRecuperacaoSenha(String username, String email) {
    Optional<Usuario> usuarioOpt = loginRepository.findByUsernameAndEmail(username, email);
    
    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        if(!usuario.isEnabled()) {
        	Map<String , String > naoAtivo = new HashMap<>();
        	naoAtivo.put("status","Usuario nao pode fazer altercoes. sua conta esta inativa");
    	
    	return naoAtivo;  
        }
    String token = UUID.randomUUID().toString();
    usuario.setResetToken(token);
   usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
    usuario.setTokenUtilizado(false);
    loginRepository.save(usuario);  
     
    // Retorna tanto a pergunta quanto o token
    Map<String, String> response = new HashMap<>();
    response.put("perguntaSeguranca", usuario.getPerguntaSeguranca()); // Supondo que tenha este campo
    response.put("token", token);
    response.put("status", "sucesso");
    
        // Enviar email com token (opcional)
        // emailServiceImpl.enviarEmailRecuperacao(usuario.getEmail(), token);
        
    return response;
      }
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("status", "erro");
    errorResponse.put("mensagem", "Usuário não encontrado");
    return errorResponse;
   
      }

public boolean verificarRespostaSeguranca(String username, String respostaSeguranca) {
  Optional<Usuario> usuarioOpt = loginRepository.findByUsername(username);
    
    if (!usuarioOpt.isEmpty()) {
        Usuario usuario = usuarioOpt.get();
        return respostaSeguranca.equalsIgnoreCase(usuario.getRespostaSeguranca());
    }
    return false; 
}  
public boolean redefinirSenhaComToken(String token, String novaSenha) {
    Optional<Usuario> usuarioOpt = loginRepository.findByResetToken(token);
    
    if (usuarioOpt.isPresent() && usuarioOpt.get().isTokenValido()) {
        Usuario usuario = usuarioOpt.get();
        usuario.setTokenUtilizado(true);
        usuario.setPassword(passwordEncoder.encode(novaSenha));
      
         loginRepository.save(usuario);
        
        return true;
    }
    return false;
}

 public boolean redefinirSenhaComVerificacao(recuperacaoSenhaDTO dto) {
    Optional<Usuario> usuarioOpt = loginRepository.findByUsernameAndResetToken(dto.getUsername(), dto.getToken());

    if (usuarioOpt.isPresent() && usuarioOpt.get().isTokenValido()) {
    Usuario usuario = usuarioOpt.get();
    //criacao de objecto pra o token 
  
    // Verificar múltiplos fatores
    boolean tokenValido  = usuario.getResetToken().equalsIgnoreCase(dto.getToken());
    boolean emailValido = usuario.getEmail().equalsIgnoreCase(dto.getEmail());
    boolean nuitValido = usuario.getNuit().equals(dto.getNuit());
    boolean respostaValida = usuario.getRespostaSeguranca()
        .equalsIgnoreCase(dto.getRespostaSeguranca());
    
    if (emailValido &&  nuitValido && respostaValida && tokenValido) {
        usuario.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
  
        usuario.setTokenUtilizado(true); 
          loginRepository.save(usuario);
        return true; 
    } 
} 
    return false;   
}
	
		
	

}
