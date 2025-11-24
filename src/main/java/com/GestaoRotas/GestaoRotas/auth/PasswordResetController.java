package com.GestaoRotas.GestaoRotas.auth;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
public class PasswordResetController {

	private final PasswordRestService passWordService;
	
	public PasswordResetController(PasswordRestService passWordService) {
		this.passWordService=passWordService;
	}    
	//Solicitar recuperacao  
	@PostMapping("/request")  
	public String requestRest(@RequestBody Map<String, String> request) {
	    String username = request.get("username");
	    return passWordService.requestPassorRest(username);
	}

	@PostMapping("/reset")        
	public String restPassword(@RequestBody Map<String, String> request) {
	    String token = request.get("token");
	    String newPassword = request.get("newPassword");
	    passWordService.restPassword(token, newPassword);
	    System.out.println(token);
	    return "Senha alterada com sucesso";

	}        
	  
}
