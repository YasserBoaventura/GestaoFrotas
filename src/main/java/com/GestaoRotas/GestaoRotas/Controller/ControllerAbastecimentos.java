package com.GestaoRotas.GestaoRotas.Controller;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor 
@CrossOrigin("*")
public class ControllerAbastecimentos {

	
  private final ServiceAbastecimentos abastecimentosService;
  
  
     // sava o abastecimento
   @PostMapping("/save")
   @PreAuthorize("hasAuthority('ADMIN')") 
  public ResponseEntity<Map<String, String>> salvar(@RequestBody AbastecimentoDTO abastecimentoDTO) {
   try { 
	    return ResponseEntity.ok(abastecimentosService.save(abastecimentoDTO)); 
	  }catch(Exception e) {
	   System.err.print(e.getStackTrace());  
	  return ResponseEntity.badRequest().build();
	  } 
    }            
  @PutMapping("/update/{id}")
  @PreAuthorize("hasAuthority('ADMIN')")  
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
@PreAuthorize("hasAuthority('ADMIN')")  
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorVeiculo() {
	 return ResponseEntity.ok(abastecimentosService.relatorioPorVeiculo());
}  
   
//busca relatorios por periodo dataInicio e dataFim
@GetMapping("/relatorio-por-periodo") 
@PreAuthorize("hasAuthority('ADMIN')")  
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    System.out.println("Recebendo requisição com datas: " + inicio + " até " + fim); // Para debug
    return ResponseEntity.ok(abastecimentosService.relatorioPorPeriodo(inicio, fim));
}   
@GetMapping("/abastecimentoRealizado")
  public ResponseEntity<Long> abastecimentosRealizados(){
		return ResponseEntity.status(HttpStatus.OK).body(abastecimentosService.numeroAbastecimento()); 
  }
	    
//Busca todos os abastecimentos  
@GetMapping("/findAll")
//@PreAuthorize("hasAuthority('ADMIN')") 
public ResponseEntity<List<abastecimentos>> findAll(){
 try { 
	 return ResponseEntity.ok(abastecimentosService.findAll()); 
	}catch(Exception e) {
	 return ResponseEntity.badRequest().build();
  }  
   }
@DeleteMapping("/delete/{id}")
@PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> deleteById(@PathVariable long id){
     try { 
     return ResponseEntity.ok(abastecimentosService.deletar(id)); 
   }catch(Exception e) {
	   e.getStackTrace();
    	 return  ResponseEntity.badRequest().build(); 
    }
	
} 
@GetMapping("/findById/{id}")
@PreAuthorize("hasAuthority('ADMIN')")  
public ResponseEntity<abastecimentos> findById(@PathVariable long id){
	try {
		return ResponseEntity.ok(abastecimentosService.findById(id));
	  }catch(Exception e) {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}
 	    
}