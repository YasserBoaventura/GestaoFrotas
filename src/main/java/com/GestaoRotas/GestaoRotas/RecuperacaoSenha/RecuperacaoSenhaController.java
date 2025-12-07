package com.GestaoRotas.GestaoRotas.RecuperacaoSenha;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;

import jakarta.persistence.*;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;

@RestController
@RequestMapping("api/auth")
public class RecuperacaoSenhaController {
	
	private  final RecuperacaoSenhaService recuperacaoService;
	 
	public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoService) {
		this.recuperacaoService=recuperacaoService;
		  
	}  
 @PostMapping("/solicitar-recuperacao")
 public ResponseEntity<Map<String, String>> solicitarRecuperacao(@RequestBody SolicitarRecuperacaoRequest dto) {
     Map<String, String> response = recuperacaoService.solicitarRecuperacaoSenha(dto.getUsername(), dto.getEmail());
     
     if ("sucesso".equals(response.get("status"))) {
         return ResponseEntity.ok(response);  //traz a pergunta e o Token do repository
     }  else if("Usuario nao pode fazer altercoes. sua conta esta inativa".equals(response.get("status"))) { 
    	 return ResponseEntity.status(201).body(response);
     }else {
    	   
         return ResponseEntity.status(404).body(response);
     }
 }
    
@PostMapping("/redefinir-senha-token")
public ResponseEntity<?> redefinirSenhaComToken(@RequestBody RedefinirSenhaTokenRequest request) {
    boolean sucesso = recuperacaoService.redefinirSenhaComToken(
        request.getToken(), 
        request.getNovaSenha()
    ); 
    
    if (sucesso) {
        return ResponseEntity.ok("Senha redefinida com sucesso");
    } else {
        return ResponseEntity.badRequest().body("Token inválido ou expirado");
    } 
}   

@PostMapping("/redefinir-senha-verificacao")
public ResponseEntity<?> redefinirSenhaComVerificacao(@RequestBody recuperacaoSenhaDTO dto) {
    boolean sucesso = recuperacaoService.redefinirSenhaComVerificacao(dto);
    if (sucesso) {   
        return ResponseEntity.ok("Senha redefinida com sucesso");
    } else {
        return ResponseEntity.badRequest().body("Dados de verificação inválidos");
    }
}
 
}
