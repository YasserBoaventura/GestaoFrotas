package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Service.SericeRotas;

@RestController
@RequestMapping("/rotas")
public class ControllerRotas {

	private final SericeRotas serviceRotas;
	
    public ControllerRotas(SericeRotas serviceRotas) {
	 	this.serviceRotas=serviceRotas;
	 }
	
	 @PostMapping("/save") 
	public ResponseEntity<String>  salvar(@RequestBody Rotas rotas){
		 try { 
		 	String frase=this.serviceRotas.save(rotas);
		 	if(frase.isEmpty()) {
			 	return new ResponseEntity<>("Nao foi possivel salvar a rota", HttpStatus.NO_CONTENT);
			 }   
			 return new ResponseEntity<>(frase, HttpStatus.OK);
					
		}catch(Exception e) {
			e.getStackTrace().notifyAll();
		return new ResponseEntity<>(" Erro" , HttpStatus.BAD_REQUEST);
		}
                
	}
	 @DeleteMapping("/delete/{id}")
	 public ResponseEntity<String> deleteByID(@PathVariable long id){
		 try {
			 String frase=this.serviceRotas.deleteById(id);
			 if(frase.isEmpty()) {
				 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			 }
			 return new ResponseEntity<>(frase, HttpStatus.OK);
		 }catch(Exception e) {
			 return new ResponseEntity<>("Erro: ",HttpStatus.BAD_REQUEST);
		}
	 }
	 @GetMapping("/findAll")
	public ResponseEntity<List<Rotas>> findAll(){
		try {
			 List<Rotas> lista=this.serviceRotas.findAll();
			 return new ResponseEntity<>(lista, HttpStatus.OK);
			 
		 }catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
		 }
		 
	}

	 @PutMapping("/update/{id}")
  public ResponseEntity<String> update(@RequestBody Rotas rotas, @PathVariable long id){
	  try {
		  String frase=this.serviceRotas.update(rotas, id);
		  return new ResponseEntity<>(frase, HttpStatus.OK);
	  }catch(Exception e) {
		  return new ResponseEntity<>("Erro: ", HttpStatus.BAD_REQUEST);
		  
	  }
  }
 @GetMapping("findById/{id}")
 public ResponseEntity<Rotas> findById(@PathVariable long id){
	 try {
		 Rotas rota=this.serviceRotas.findById(id);
		 if(rota==null) {
			 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		 } else {
			 
		return new ResponseEntity<>(rota, HttpStatus.OK);
		 }
	 }catch(Exception e) {
		 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		 
	 }
		 
	 }
}
