package com.GestaoRotas.GestaoRotas.auth;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.UserSaveDTO;
import com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController 
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor  
public class LoginController {
 
    private final LoginService loginService;  

    @PostMapping("/login")
    public ResponseEntity<?> logar(@RequestBody Login login) {
        return ResponseEntity.ok(loginService.logar(login));
    }
    @PostMapping("/trocar-senha")  
    public ResponseEntity<String> alterSenhaNoPrimeiroLogin(@RequestBody trocarSenhaDTO dto){ 
    	return ResponseEntity.ok(loginService.trocarSenha(dto));  
    }
@PostMapping("/save")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<?> save(@RequestBody Usuario userSave){
		return ResponseEntity.ok(loginService.registar(userSave)); 	
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/findAll")
    public ResponseEntity<List<Usuario>> findAll(){ return ResponseEntity.ok(loginService.findAll());
  }   
    //desbloquear/bloquear
    @PutMapping("/bloqueio/{id}") 
    @PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<Map<String, String>> bloquearConta(@PathVariable long id){
        return ResponseEntity.ok(loginService.bloquearConta(id));
}
//ativar/destivar conta
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/ativo/{id}")
    public ResponseEntity<Map<String, String>> desativarConta(@PathVariable long id){
        return ResponseEntity.ok(loginService.desativarEndActivarConta(id));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody @Valid Usuario usuario) {
        return ResponseEntity.ok(loginService.atualizarUsuario(id, usuario));
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> delete(@PathVariable long id){
    	return  ResponseEntity.ok(loginService.delete(id));

    }

 
   
}
