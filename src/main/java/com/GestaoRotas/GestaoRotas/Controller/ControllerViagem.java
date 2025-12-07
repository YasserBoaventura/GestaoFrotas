package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceViagem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/api/viagens")
@CrossOrigin("*")
@JsonIgnoreProperties(ignoreUnknown = true) // na classe
public class ControllerViagem {

      
	private final ServiceViagem serviceViagem;
	private final RepositoryViagem RepositoryViagem;
	
	public ControllerViagem(ServiceViagem serviceViagem, RepositoryViagem RepositoryViagem ) {
       this.serviceViagem=serviceViagem;
       this.RepositoryViagem=RepositoryViagem;
	}
	@PostMapping("/save")
	public ResponseEntity<Viagem> criarViagem(@RequestBody ViagensDTO viagemDTO) {
	    Viagem viagem = serviceViagem.salvar(viagemDTO);
	    return ResponseEntity.ok(viagem);
	}  
 @GetMapping("/findAll")
 public ResponseEntity<List<Viagem>> findAll(){
	try {
		List<Viagem> lista=this.serviceViagem.findAll();
		if(lista.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(lista, HttpStatus.OK);
		
	}catch(Exception e) {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
	}
}
	//
	@DeleteMapping("/delete/{id}")
	 @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            String frase = this.serviceViagem.delete(id);
            if (frase.equals("Viagem não encontrada")) {
                return new ResponseEntity<>(frase, HttpStatus.NOT_FOUND);
            }
           return new ResponseEntity<>(frase, HttpStatus.OK);
            } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar Viagem", HttpStatus.BAD_REQUEST);
    }
	}
	//busca por viagem pelo iD do motorista
	@GetMapping("/findByIdMotorista/{id}")
	public ResponseEntity<List<Viagem>> findByIDMotorista(@PathVariable long id){
		try {
			List<Viagem> lista=this.serviceViagem.findByIdMotorista(id);
			if(lista.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(null ,HttpStatus.BAD_REQUEST);
		}
	}
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@RequestBody ViagensDTO viagemDTO, @PathVariable long id) {
	    try {
	    	Viagem viagem = this.serviceViagem.update(viagemDTO, id);
	        return ResponseEntity.ok(viagem);

	    } catch (Exception e) {

	        return ResponseEntity.badRequest().body("Erro ao actualizar: " + e.getMessage());
	    }
	}

//No controller Java
@PutMapping("/concluir/{id}")
@PreAuthorize("hasAuthority('ADMIN')") 
public ResponseEntity<Viagem> ConcluirViagem(
                                           @RequestBody ConcluirViagemRequest request, @PathVariable long id) {
  Viagem viagem = RepositoryViagem.findById(id)
      .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
             // Atualizar a quilometragem final
  viagem.setKilometragemFinal(request.getKilometragemFinal());
  viagem.setObservacoes(request.getObservacoes());
  viagem.setDataHoraChegada(request.getDataHoraChegada());
    
  // Chamar o método de negócio que já atualiza status e data
  viagem.concluirViagem();
   
  Viagem viagemAtualizada = RepositoryViagem.save(viagem);
  return ResponseEntity.ok(viagemAtualizada);
}
//Mostra o relatorio nome do mortista do carro , totalViagens , totalEmKm e totalConbustivel usado
	

    @GetMapping("/motoristas")
    public ResponseEntity<List<RelatorioMotoristaDTO>> relatorioPorMotorista() {
        return ResponseEntity.ok(serviceViagem.relatorioPorMotorista());
    }
    //Mostra o relatorio placa do carro , totalViagens , totalEmKm e totalConbustivel usado
    @GetMapping("/veiculos")
     public ResponseEntity<List<RelatorioPorVeiculoDTO>> relatorioPorVeiculo() {
        return ResponseEntity.ok(serviceViagem.gerarRelatorioPorVeiculo());
    }  
    @GetMapping("/findById/{id}")
    public ResponseEntity<Viagem> findById(@PathVariable long id){
    	try {
    		Viagem viagem=this.serviceViagem.findById(id);
    		if(viagem!=null) return new ResponseEntity<>(viagem, HttpStatus.OK);
        	}catch(Exception e) {
    		 return new ResponseEntity<>(null , HttpStatus.BAD_REQUEST);
    	}
    	return null;
    }  
    
    
} 
