package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.GestaoRotas.GestaoRotas.CustoDTO.VerificarCodigoDTO;
import com.GestaoRotas.GestaoRotas.DTO.SolicitarCodigoDTO;
import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor 
public final class RecuperacaoSenhaService  {
	
	   

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
      
    private final  EmailService emailService; 
       
     
    private static final Logger logger = LoggerFactory.getLogger(RecuperacaoSenhaService.class);
    
  
public Map<String, String> solicitarRecuperacaoSenha(String username, String email) {
    Optional<Usuario> usuarioOpt = loginRepository.findByUsernameAndEmail(username, email);
    Map<String, String> response = new HashMap<>();
    if (usuarioOpt.isPresent()) { 
        Usuario usuario = usuarioOpt.get();
        if(!usuario.isEnabled()) {
        	Map<String , String > naoAtivo = new HashMap<>();
        	naoAtivo.put("status","Usuario nao pode fazer altercoes. sua conta esta inativa");
    	
    	return naoAtivo;  
        } 
    String token = UUID.randomUUID().toString();
    System.out.println(token.toString());  
    usuario.setResetToken(token);
   usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
    usuario.setTokenUtilizado(false);
    
    
    // Gerar código de 6 dígitos
    String codigo = String.format("%06d", new Random().nextInt(999999));
    
    // Salvar código no banco
    usuario.setCodigoVerificacao(codigo);
    usuario.setCodigoVerificacaoExpiry(LocalDateTime.now().plusMinutes(10)); // Expira em 10 minutos
    usuario.setCodigoVerificado(false);
   
    // Enviar código por email de uma forma ASSÍNCRONO
    emailService.enviarCodigoVerificacao(usuario.getEmail(), usuario.getUsername(), codigo);
     
    // Retornar resposta NÃO retorna o código por segurança! 
    response.put("status", "sucesso");
    response.put("mensagem", "Código de verificação enviado para seu email");
    response.put("username", usuario.getUsername());
    response.put("email", usuario.getEmail());
    response.put("expiraEm", "10 minutos");

    loginRepository.save(usuario);  
     
    // Retorna tanto a pergunta quanto o token

    response.put("perguntaSeguranca", usuario.getPerguntaSeguranca()); // Supondo que tenha este campo
    response.put("token", token);
    response.put("status", "sucesso");
  
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
    Optional<Usuario> usuarioOpt = loginRepository.findByUsername(dto.getUsername());

    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();  
    
        boolean emailValido = usuario.getEmail().equalsIgnoreCase(dto.getEmail());
        boolean nuitValido = usuario.getNuit().equals(dto.getNuit());
        boolean respostaValida = usuario.getRespostaSeguranca()
            .equalsIgnoreCase(dto.getRespostaSeguranca());
        boolean codigoVerificacao = usuario.getCodigoVerificacao().equalsIgnoreCase(dto.getCodigoVerificacao()) ; 
         
        if (emailValido && nuitValido && respostaValida && codigoVerificacao && usuario.isTokenValido()) {
            usuario.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
            loginRepository.save(usuario);
            return true; 
        }  
    } 
    return false;   

}
	
		
	

}
