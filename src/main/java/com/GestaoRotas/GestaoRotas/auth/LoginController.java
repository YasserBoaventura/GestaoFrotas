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
    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;
 

    @PostMapping("/login")
    public ResponseEntity<?> logar(@RequestBody Login login) {
        try {
            String token = loginService.logar(login);
             return ResponseEntity.ok(token); 

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/auto-cadastro")
    public ResponseEntity<?> autoCadastro(@RequestBody AutoCadastroDTO dto) {
       return loginService.autoCadastro(dto);  
    }   
    @PostMapping("/trocar-senha")  
    public ResponseEntity<String> alterSenhaNoPrimeiroLogin(@RequestBody trocarSenhaDTO dto){ 
    	return ResponseEntity.ok(loginService.trocarSenha(dto));  
    }
@PostMapping("/save")
public ResponseEntity<?> save(@RequestBody Usuario userSave){ 
	try {      
		return ResponseEntity.ok(loginService.registar(userSave)); 
		
	}catch (Exception e) {
		Map<String,String> erroResponse = new HashMap<>();
		erroResponse.put("Erro Ao Cadastrar User", e.getMessage()); 
	return ResponseEntity.badRequest().body(erroResponse); 
	}
}
   //Devo fazer aqui ate porque o Repositorio e do tipo usuario
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/findAll")
    public ResponseEntity<List<Usuario>> findAll(){
	  try { 
	 	List<Usuario> lista=this.loginService.findAll();
		return new ResponseEntity<>(lista, HttpStatus.OK);
	    }catch(Exception e) {
		return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
	   }
  }   
    //desbloquear/bloquear contas 
    @PutMapping("/bloqueio/{id}") 
    @PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<Map<String, String>> bloquearConta( @PathVariable long id){
	try { 
 		Map<String, String> response = this.loginService.bloquearConta(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
  	}catch(ClassCastException  e) {
		Map<String, String> erro = new HashMap<>();
	 return	ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	 	
	}
}
      //ativar/destivar conta
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/ativo/{id}")
    public ResponseEntity<Map<String, String>> desativarConta(@PathVariable long id){
    	try {
    		Map<String, String> response = this.loginService.desativarConta(id);
    		return ResponseEntity.status(HttpStatus.OK).body(response);
    	}catch(ClassCastException e) { 
    		Map<String , String> erro =  new HashMap<>();
    		erro.put("erro", "erro ao tentar fazer altercoes");
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);     		
    	}
    }
    
    @PutMapping("/{id}") 
    @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<Usuario> atualizarUsuario( 
            @PathVariable Long id, 
            @RequestBody @Valid Usuario usuario) {
          
        Usuario usuarioAtualizadoo = this.loginService.atualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioAtualizadoo);
    }
    
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> delete(@PathVariable long id){
    	try {
    		String frase=this.loginService.delete(id);
    		return new ResponseEntity<>(frase, HttpStatus.OK);
    	    }catch(Exception e) {
    		return new ResponseEntity<>("erro ao deletar usuario", HttpStatus.BAD_REQUEST);
    	}
    	
    }

 
   
}
