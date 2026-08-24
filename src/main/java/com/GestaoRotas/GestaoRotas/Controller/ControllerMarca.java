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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/marca")
@RequiredArgsConstructor 
public class ControllerMarca {

	private final ServiceMarca serviceMarca;

	 @PostMapping("/save") 
	 public ResponseEntity<String> save(@RequestBody Marca marca){
		 return ResponseEntity.ok(serviceMarca.save(marca));
	 }
	 @DeleteMapping("/deleteById/{id}")
	 public ResponseEntity<String> delete(@PathVariable long id){
		 return ResponseEntity.ok(serviceMarca.delete(id));
	 }
	 @PutMapping("/update/{id}")
	 public ResponseEntity<String> update(@RequestBody Marca marca , @PathVariable long id){
		 return ResponseEntity.ok(serviceMarca.update(marca, id));
	 }
	 @GetMapping("/findAll")
	 public ResponseEntity<List<Marca>> findAll(){
		  return  ResponseEntity.ok(serviceMarca.findAll());

	 }
}
