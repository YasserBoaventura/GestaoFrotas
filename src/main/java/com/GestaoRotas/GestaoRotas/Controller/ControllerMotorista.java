package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceMotorista;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/motorista")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ControllerMotorista {
	
	private final ServiceMotorista serviceMotorista;
	 
	 
 @PostMapping("/save")
  public ResponseEntity<Map<String, String>> salvar(@RequestBody Motorista motorista) {
    try { 
        return ResponseEntity.ok(serviceMotorista.salvar(motorista)); 
       }catch (Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    } 
}
 @DeleteMapping("/delete/{id}")
 public ResponseEntity<String> delete(@PathVariable Long id){
     try { 
        return ResponseEntity.ok(serviceMotorista.deleteById(id));
     } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
         .body("Erro ao apagar Motorista: " + e.getMessage());
    } 
 }
@PutMapping("/update/{id}")  
public ResponseEntity<String> update(@RequestBody Motorista motorista ,@PathVariable long id){
	 try {
		 return ResponseEntity.ok(serviceMotorista.update(motorista, id)); 
 }catch(Exception e) {
	return new ResponseEntity<>("nao foi possivel actualizar", HttpStatus.BAD_REQUEST);
}
  }
	@GetMapping("/findAll")
	 public ResponseEntity<List<Motorista>>  findAll(){
      try {
    	  return ResponseEntity.ok(serviceMotorista.findAll()); 
   	 }catch(Exception e) {
   		 return ResponseEntity.badRequest().build();  
   	 }
   	} 
	@GetMapping("/findByNome/{nomeMotorista}")
	public ResponseEntity <List<Motorista>>  findByNome(@PathVariable String nomeMotorista){
	try {
		return ResponseEntity.ok(serviceMotorista.findByNome(nomeMotorista)); 
		} catch(Exception e) {
			e.getMessage(); 
			 return ResponseEntity.badRequest().build();   
		}
	}
	
	

}
