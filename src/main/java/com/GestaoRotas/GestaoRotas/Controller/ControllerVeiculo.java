package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;

import lombok.RequiredArgsConstructor;

import java.util.*;


@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin("*")
@RequiredArgsConstructor 
public class ControllerVeiculo {
    
	 private final ServiceVeiculo serviceVeiculo;
     private final RepositoryVeiculo repositoryVeiculo;

@PostMapping("/salvar")
  public ResponseEntity<Map<String, String>> salvar(@RequestBody Veiculo veiculo) {
    try {
        String saved = serviceVeiculo.salvar(veiculo);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Veículo cadastrado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {  
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
	
 @PatchMapping("/{veiculoId}/kilometragem")
 public ResponseEntity<Veiculo> atualizarKilometragem(
         @PathVariable Long veiculoId,
         @RequestBody Map<String, Double> body) {

     Double kilometragemAtual = body.get("kilometragemAtual");

     Veiculo veiculo = repositoryVeiculo.findById(veiculoId)
         .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

     veiculo.setKilometragemAtual(kilometragemAtual);

     return ResponseEntity.ok(repositoryVeiculo.save(veiculo));
 }

     @PutMapping("/update/{id}") 
    public ResponseEntity<String> update(@RequestBody Veiculo veiculo, @PathVariable long id){
    	try {
    		 return ResponseEntity.ok(serviceVeiculo.update(veiculo, id)); 
		 }catch(Exception e) {
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST );	
    	}
    }
     @GetMapping("/findById/{id}")
     public ResponseEntity<Veiculo> findById(Long id){
    	 try { 
    		Veiculo veiculo = repositoryVeiculo.findById(id).get(); 
          return new ResponseEntity<>( veiculo, HttpStatus.OK) ;   		 
    	 } catch(Exception e) {
    		 e.getMessage();
    	 return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    	 }	
     }
     @DeleteMapping("/delete/{id}")
     public ResponseEntity<String> delete(@PathVariable Long id){
         try { 
	    	serviceVeiculo.deletar(id);
	        return ResponseEntity.ok("Veículo apagado com sucesso");
	     } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	               .body("Erro ao apagar veículo: " + e.getMessage());
	    }
	    
     }
     
     
}
