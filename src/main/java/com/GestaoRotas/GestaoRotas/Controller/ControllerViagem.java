package com.GestaoRotas.GestaoRotas.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceViagem;

@RestController
@RequestMapping("/viagens")
public class ControllerViagem {


	private final ServiceViagem serviceViagem;
	
	@Autowired
	public ControllerViagem(ServiceViagem serviceViagem) {
       this.serviceViagem=serviceViagem;
	}
 @PostMapping("/save")
   public ResponseEntity<String> salvar(@RequestBody Viagem viagem){
	  try {
	 String frase=this.serviceViagem.salvar(viagem);
		  return new ResponseEntity<>(frase, HttpStatus.OK);
	  }catch(Exception e) {
		  e.printStackTrace();
		  return new ResponseEntity<>("Erro ao salvar Viagem", HttpStatus.BAD_REQUEST);
	 }
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
	@DeleteMapping("/deleteById/{id}")
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
public ResponseEntity<String> update(@RequestBody Viagem viagem, @PathVariable long id){
	try {
		String frase=this.serviceViagem.update(viagem, id);
		if(frase.equals("nao existem uma viagem com id")) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
		}
		return new ResponseEntity<>(frase, HttpStatus.OK);
		
	}catch(Exception e) {
		return new ResponseEntity<>("Erro: ", HttpStatus.BAD_REQUEST); 
	}
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
