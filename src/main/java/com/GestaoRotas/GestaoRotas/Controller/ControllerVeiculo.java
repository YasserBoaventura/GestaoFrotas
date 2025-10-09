package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;
import java.util.*;


@RestController
@RequestMapping("/veiculos")
public class ControllerVeiculo {
    
	 private final ServiceVeiculo serviceVeiculo;
	
	@Autowired
	public ControllerVeiculo (ServiceVeiculo serviceVeiculo) {
		this.serviceVeiculo=serviceVeiculo; 
	}
     @PostMapping("/salvar")
	public ResponseEntity<String> salvar(@RequestBody Veiculo veiculo){
	 
		try {
			String frase=this.serviceVeiculo.salvar(veiculo);
			if(frase.isBlank()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else if(frase.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}  
			return new ResponseEntity<>(frase, HttpStatus.OK);
			
			
		}catch(Exception e) {
		 return new ResponseEntity<>(HttpStatus.BAD_REQUEST);	
		}
	} 
     @GetMapping("/findAll")
     public ResponseEntity<List<Veiculo>> findAll(){
    	  try {
    		 List<Veiculo> lista=this.serviceVeiculo.findAll();
         if(lista.isEmpty()) {
    			return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    		 }
    	 	 return new ResponseEntity<>(lista, HttpStatus.OK);
       }catch(Exception e) {
    		 System.out.println("Erro a listar os Veiculos "+ e.getStackTrace());
    		 return new ResponseEntity<>(null, HttpStatus.OK);
    		 
    	 }
     }
	
     @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Veiculo veiculo, @PathVariable long id){
    	try {
    		String frase= this.serviceVeiculo.update(veiculo, id);
    		if(frase.isEmpty() || frase.isBlank()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		  }else{
			return new ResponseEntity<>(frase, HttpStatus.OK);
		 }
		 }catch(Exception e) {
    		System.out.println("");
    	  return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST );	
    	}
    }
     @DeleteMapping("/delete/{id}")
     public ResponseEntity<String> delete(@PathVariable Long id){
    	try{
         String frase=this.serviceVeiculo.deletar(id);
    		 if(frase.isBlank() || frase.isBlank()) {
    			 return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    		 }else{ 
    		return new ResponseEntity<>(frase, HttpStatus.OK);
    		 }
    		 }catch(Exception e) {
    			System.out.println("Erro ao deletar veiculo: "+ e.getStackTrace());
    		 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    	 }  
     }
}
