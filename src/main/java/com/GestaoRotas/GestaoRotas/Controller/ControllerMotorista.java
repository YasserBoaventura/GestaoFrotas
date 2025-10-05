package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Service.ServiceMotorista;
import jakarta.persistence.*;

@RestController
@RequestMapping("/motorista")
public class ControllerMotorista {
	
	private final ServiceMotorista serviceMotorista;
	 
	@Autowired
	public ControllerMotorista(ServiceMotorista serviceMotorista) {
		this.serviceMotorista=serviceMotorista;
	}
	
	@PostMapping("/save")
	public ResponseEntity<String> save(Motorista motorista){
		
		try {
	   String frase=this.serviceMotorista.salvar(motorista);
	   if(frase.isEmpty()) {
		   return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	   } else if(frase.isBlank()) {
		   return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	   }else {
		   return new ResponseEntity<>(frase, HttpStatus.OK);
		 }	 
		}catch(Exception e) {
			 return new ResponseEntity<>( null, HttpStatus.BAD_REQUEST);
		}
		
	}
	@DeleteMapping("/deleteById/{id}")
	public ResponseEntity<String> deleteById(@PathVariable long id){
	
		try {
			String frase=this.serviceMotorista.deleteById(id);
			if(frase.isBlank()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<>(frase, HttpStatus.OK);
			}	 
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<String>  update(@RequestBody Motorista motorista ,@PathVariable long id){
		 
		try {
			String frase=this.serviceMotorista.update(motorista, id);
			if(frase.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<>(frase, HttpStatus.OK);
			}
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	

}
