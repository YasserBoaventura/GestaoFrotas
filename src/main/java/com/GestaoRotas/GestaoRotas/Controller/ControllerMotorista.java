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
        return ResponseEntity.ok(serviceMotorista.salvar(motorista)); 

}
 @DeleteMapping("/delete/{id}")
 public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(serviceMotorista.deleteById(id));
 }
@PutMapping("/update/{id}")  
public ResponseEntity<String> update(@RequestBody Motorista motorista ,@PathVariable long id){
     return ResponseEntity.ok(serviceMotorista.update(motorista, id));
  }
	@GetMapping("/findAll")
	 public ResponseEntity<List<Motorista>>  findAll(){
     return ResponseEntity.ok(serviceMotorista.findAll());

   	} 
	@GetMapping("/findByNome/{nomeMotorista}")
	public ResponseEntity <List<Motorista>>  findByNome(@PathVariable String nomeMotorista){
		return ResponseEntity.ok(serviceMotorista.findByNome(nomeMotorista)); 

	}
	
	

}
