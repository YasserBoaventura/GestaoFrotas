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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rotas")
@RequiredArgsConstructor
public class ControllerRotas { 

	private final SericeRotas serviceRotas;
	
	
	 @PostMapping("/save") 
	public ResponseEntity<String>  salvar(@RequestBody Rotas rotas){
		 return ResponseEntity.ok(serviceRotas.save(rotas));

	}
	 @DeleteMapping("/delete/{id}")
	 public ResponseEntity<String> deleteByID(@PathVariable long id){
		 return ResponseEntity.ok(serviceRotas.deleteById(id));
	 }
	 @GetMapping("/findAll")
	public ResponseEntity<List<Rotas>> findAll(){
			 return ResponseEntity.ok(serviceRotas.findAll());
} 
   @PutMapping("update/{id}")
public ResponseEntity<String> update(@RequestBody Rotas rotas, @PathVariable Long id) {
        return ResponseEntity.ok(serviceRotas.update(rotas, id)); 

}
 @GetMapping("findById/{id}")
  public ResponseEntity<Rotas> findById(@PathVariable long id){
		 return  ResponseEntity.ok(serviceRotas.findById(id));

		 
	 }
}
