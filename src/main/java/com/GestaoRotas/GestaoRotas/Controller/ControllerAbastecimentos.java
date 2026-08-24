package com.GestaoRotas.GestaoRotas.Controller;

import jakarta.persistence.*;
import jakarta.validation.Valid;
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
   @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
  public ResponseEntity<Map<String, String>> salvar(@RequestBody @Valid AbastecimentoDTO abastecimentoDTO) {
       return ResponseEntity.ok(abastecimentosService.save(abastecimentoDTO));

    }            
  @PutMapping("/update/{id}")
  @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')") 
  public ResponseEntity<String> update(@PathVariable long id, @RequestBody AbastecimentoDTO abastecimentoDTO) {
      return ResponseEntity.ok(abastecimentosService.update(abastecimentoDTO, id));
  } 

@GetMapping("/por-veiculo")
@PreAuthorize("hasAuthority('ADMIN')")  
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorVeiculo() {
	 return ResponseEntity.ok(abastecimentosService.relatorioPorVeiculo());
}  

@GetMapping("/relatorio-por-periodo")
@PreAuthorize("hasAuthority('ADMIN')")   
public ResponseEntity<List<RelatorioCombustivelDTO>> relatorioPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
    return ResponseEntity.ok(abastecimentosService.relatorioPorPeriodo(inicio, fim));
}      
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')") 
@GetMapping("/abastecimentoRealizado")   
  public ResponseEntity<Long> abastecimentosRealizados(){
		return ResponseEntity.status(HttpStatus.OK).body(abastecimentosService.numeroAbastecimentoRealizados()); 
  }                   
@GetMapping("/abastecimtosPlaneados") 
public ResponseEntity<Optional<Long>> abastecimentosPlaneados(){
	return ResponseEntity.status(HttpStatus.OK).body(abastecimentosService.numeroAbastecimentoPlaneado()); 
} 
@GetMapping("/abastecimentosCancelar")
public ResponseEntity<Optional<Long>> abastecimentosCancelados(){
	return ResponseEntity.status(HttpStatus.OK).body(abastecimentosService.numeroAbastecimentoCancelados()); 
}
//Busca todos os abastecimentos  
@GetMapping("/findAll")  
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
public ResponseEntity<List<abastecimentos>> findAll(){
     return ResponseEntity.ok(abastecimentosService.findAll());
 }
@DeleteMapping("/delete/{id}")
@PreAuthorize("hasAuthority('ADMIN')")  
    public ResponseEntity<String> deleteById(@PathVariable long id){
       return ResponseEntity.ok(abastecimentosService.deletar(id));

} 
@GetMapping("/findById/{id}") 
public ResponseEntity<abastecimentos> findById(@PathVariable long id){
		return ResponseEntity.ok(abastecimentosService.findById(id));

}
 	    
}