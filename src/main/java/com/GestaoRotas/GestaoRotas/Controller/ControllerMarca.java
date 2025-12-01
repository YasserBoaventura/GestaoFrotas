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
import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Service.ServiceMarca;

@RestController
@RequestMapping("/api/marca")
public class ControllerMarca {

	
	 private final ServiceMarca serviceMarca;
	 public ControllerMarca(ServiceMarca serviceMarca) {
		 
		this.serviceMarca=serviceMarca;
	 } 
	 
	 @PostMapping("/save") 
	 public ResponseEntity<String> save(@RequestBody Marca marca){
		 try {
			 String frase=this.serviceMarca.save(marca);
		      return new ResponseEntity<>(frase, HttpStatus.OK); 
			}catch(Exception e) {
			 return new ResponseEntity<>("erro", HttpStatus.BAD_REQUEST);
		 }
	 }
	 
	 @DeleteMapping("/deleteById/{id}")
	 public ResponseEntity<String> delete(@PathVariable long id){
		 try {
	        String frase=this.serviceMarca.delete(id);
	        return new ResponseEntity<>(frase, HttpStatus.OK);
		 }catch(Exception e) {
	      return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);
		 }
		 
	 }
	 @PutMapping("/update/{id}")
	 public ResponseEntity<String> update(@RequestBody Marca marca , @PathVariable long id){
		 try {
			 String frase=this.serviceMarca.update(marca, id);
			 return new ResponseEntity<>(frase, HttpStatus.OK);
			 }catch(Exception e) {
			   return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST); 
		    }
	 }
	 
 @GetMapping("/findAll")
 public ResponseEntity<List<Marca>> findAll(){
	 try {
	  List<Marca> lista=this.serviceMarca.findAll();
	  if(lista.isEmpty()) {
		  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	  }
	  return new ResponseEntity<>(lista, HttpStatus.OK);
    }catch(Exception e) {
	 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	 
 }
	 }
}
