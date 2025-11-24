package com.GestaoRotas.GestaoRotas.auth;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {
 
	
	private final LoginService loginService; 
	private final LoginRepository loginRepository;
	
	@Autowired
	public LoginController(LoginService loginService, LoginRepository loginRepository) {
		this.loginRepository=loginRepository;
		this.loginService=loginService;
	
	}
	

	// CORRETO - POST para login
@PostMapping("/login")
private ResponseEntity<?> logar(@RequestBody Login login) {
	try {
        String token = loginService.logar(login);
        if(token.isEmpty()) {
        	throw new IllegalArgumentException("Credenciais inválidas");
        }
        return ResponseEntity.ok(token);
        } catch(Exception e){
       // Retorna um objeto JSON com a mensagem de erro
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        System.out.println("Na console  "+e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
            
        }
    }
 
    //  POST para registro (corrigindo typo "resgister")
@PostMapping("/register")
private ResponseEntity<?> save(@RequestBody Usuario user) {
    try {     
        if(this.loginRepository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("User name em uso Escolha outro");
        } 
     String resultado = this.loginService.registar(user);
        return ResponseEntity.ok(resultado);   
      } catch (Exception e){
        // Retorna um objeto JSON com a mensagem de erro
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        System.out.println("Na console  "+e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }   
}  
    @GetMapping("/findAll")
    private ResponseEntity<List<Usuario>> findAll(){
    	try {
    	 	List<Usuario> lista=this.loginService.findAll();
    		return new ResponseEntity<>(lista, HttpStatus.OK);
    	    }catch(Exception e) {
    		return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    	  }
    } 
	

   
}
