package com.GestaoRotas.GestaoRotas.Controller;

import jakarta.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Service.ServiceAbastecimentos;

@RestController
@RequestMapping("/Abastecimentos")
@CrossOrigin("*")
public class ControllerAbastecimentos {

	
  private final ServiceAbastecimentos abastecimentosService;
  
   @Autowired
   public ControllerAbastecimentos(ServiceAbastecimentos abastecimentos) {
	this.abastecimentosService=abastecimentos;   
   }
   
	   // sava o abastecimento
   @PostMapping("/save")
public ResponseEntity<String> salvar(@RequestBody abastecimentos abastecimento) {
   try {
	  String frase=this.abastecimentosService.save(abastecimento);
	  if(frase.isEmpty()) {
		  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	  }
	  return new ResponseEntity<>(frase, HttpStatus.OK);  
	  
   }catch(Exception e) {
	  return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);
       }
    }                 


   // relatio de abastecimento por veiculo 
@GetMapping("/relatorio")
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorVeiculo() {
    return ResponseEntity.ok(abastecimentosService.relatorioPorVeiculo());
}  

//busca relatorios por periodo dataInicio e dataFim
@GetMapping("/relatorio/periodo")
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorPeriodo(
        @RequestParam LocalDate inicio,
        @RequestParam LocalDate fim) {
    return ResponseEntity.ok(abastecimentosService.relatorioPorPeriodo(inicio, fim));
    
}  
	    //Busca todos os abastecimentos  
@GetMapping("/findAll")
public ResponseEntity<List<abastecimentos>> findAll(){
	 try {
		  List<abastecimentos> lista=this.abastecimentosService.findAll();
		   if(lista.isEmpty()) {
			   return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		   }
		 return new ResponseEntity<>(lista, HttpStatus.OK);
		}catch(Exception e) {
		 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	 } 
       
  
   }
@DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteById(@PathVariable long id){
     try { 
    	  String frase=this.abastecimentosService.deletar(id);
    	  return new ResponseEntity<>(frase, HttpStatus.OK);
   }catch(Exception e) {
	   e.getStackTrace();
    	 return new ResponseEntity<>("Erro", HttpStatus.BAD_REQUEST);
    }
	
}
	    
@GetMapping("/findById/{id}")
public ResponseEntity<abastecimentos> findById(@PathVariable long id){
	try {
		abastecimentos abastecimento=new abastecimentos();
		if(abastecimento==null) 
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	
   return new ResponseEntity<>(abastecimento,HttpStatus.OK);
	  }catch(Exception e) {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}
@PutMapping("/update/{id}")
public ResponseEntity<String> update(abastecimentos abastecimento, long id){
	try {
		String frase=this.abastecimentosService.update(abastecimento, id);
		if(frase==null) {
			return new ResponseEntity<>( HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(frase, HttpStatus.OK);
	}catch(Exception e) {
		return new ResponseEntity<>(HttpStatus.OK);
	}
	 }
	       	
	    
}