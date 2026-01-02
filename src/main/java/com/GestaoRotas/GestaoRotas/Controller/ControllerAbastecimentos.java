package com.GestaoRotas.GestaoRotas.Controller;

import jakarta.persistence.*;

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

import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Service.ServiceAbastecimentos;

@RestController
@RequestMapping("api/abastecimentos")
@CrossOrigin("*")
public class ControllerAbastecimentos {

	
  private final ServiceAbastecimentos abastecimentosService;
  
   public ControllerAbastecimentos(ServiceAbastecimentos abastecimentos) {
	this.abastecimentosService=abastecimentos;   
   }
   
	   // sava o abastecimento
   @PostMapping("/save")
  public ResponseEntity<abastecimentos> salvar(@RequestBody AbastecimentoDTO abastecimentoDTO) {
   try { 
	  abastecimentos abastecimentos= this.abastecimentosService.save(abastecimentoDTO);
	  return  ResponseEntity.ok(abastecimentos); 
	  }catch(Exception e) {
	   System.out.print(e.getStackTrace());  
	  return ResponseEntity.badRequest().build();
	  }
    }            
  @PutMapping("/update/{id}")
  public ResponseEntity<String> update(@PathVariable long id, @RequestBody AbastecimentoDTO abastecimentoDTO) {
      try {
     
      
      String response = this.abastecimentosService.update(abastecimentoDTO, id);
      return ResponseEntity.status(HttpStatus.OK).body(response);
  } catch(Exception e) {
      System.err.println("Erro ao atualizar abastecimento: " + e.getMessage());
      e.printStackTrace();
      String erro = "erro ao actualizar abastecimento: " + e.getMessage();
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
      }
  } 

  // relatio de abastecimento por veiculo 
@GetMapping("/por-veiculo")
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorVeiculo() {
    return ResponseEntity.ok(abastecimentosService.relatorioPorVeiculo());
}  
 
//busca relatorios por periodo dataInicio e dataFim
@GetMapping("/relatorio-por-periodo")
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    System.out.println("Recebendo requisição com datas: " + inicio + " até " + fim); // Para debug
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
@DeleteMapping("/delete/{id}")
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

	       	
	    
}