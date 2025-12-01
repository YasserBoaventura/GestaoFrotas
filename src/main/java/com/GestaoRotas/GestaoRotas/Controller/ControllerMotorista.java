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

@RestController
@RequestMapping("api/motorista")
@CrossOrigin("*")
public class ControllerMotorista {
	
	private final ServiceMotorista serviceMotorista;
	 
	public ControllerMotorista(ServiceMotorista serviceMotorista) {
		this.serviceMotorista=serviceMotorista;  
	}
	 
         
@PostMapping("/save")
  public ResponseEntity<Map<String, String>> salvar(@RequestBody Motorista motorista) {
    try {
        String saved = serviceMotorista.salvar(motorista);
   
        Map<String, String> response = new HashMap<>();
        response.put("message", "Motorista cadastrado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
       }catch (Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    } 
}
	    
  @DeleteMapping("/delete/{id}")
 public ResponseEntity<String> delete(@PathVariable Long id){
     try {
    	 serviceMotorista.deleteById(id);
        return ResponseEntity.ok("Motorista apagado com sucesso");
     } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
         .body("Erro ao apagar Motorista: " + e.getMessage());
    }
    
 }
@PutMapping("/update/{id}")
public ResponseEntity<String>  update(@RequestBody Motorista motorista ,@PathVariable long id){
	 
	try {
	String frase=this.serviceMotorista.update(motorista, id);
	if(frase.isEmpty()) {
		return new ResponseEntity<>("Nao existe um motorista com esse nome",HttpStatus.NO_CONTENT);
	}else {
		return new ResponseEntity<>(frase, HttpStatus.OK);
	}
 }catch(Exception e) {
	return new ResponseEntity<>("nao foi possivel actualizar", HttpStatus.BAD_REQUEST);
}
  }
	@GetMapping("/findAll")
	 public ResponseEntity<List<Motorista>>  findAll(){
   	 try {
   	List<Motorista> lista=this.serviceMotorista.findAll();
   	if(lista.isEmpty()) {
        return ResponseEntity.noContent().build();
   	}else {
   		return new ResponseEntity<>(lista, HttpStatus.OK);
   	}
   	 }catch(Exception e) {
   		return new ResponseEntity<>(null,  HttpStatus.BAD_REQUEST); 
   	 }
   	} 
	@GetMapping("/findByNome/{nomeMotorista}")
	public ResponseEntity <List<Motorista>>  findByNome(@PathVariable String nomeMotorista){
	try {
		List<Motorista> lista=this.serviceMotorista.findByNome(nomeMotorista);
		if(lista.isEmpty()) {
            return ResponseEntity.noContent().build();
		}
		else {
	   		return new ResponseEntity<>(lista, HttpStatus.OK);
		}
		} catch(Exception e) {
			   e.printStackTrace();
			return new ResponseEntity<>(null,  HttpStatus.BAD_REQUEST);
		}
	}
	
	

}
