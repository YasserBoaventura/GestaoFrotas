package com.GestaoRotas.GestaoRotas.Service;

import java.util.List;
import java.util.*;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class ServiceMotorista {
    
	private final RepositoryMotorista repositoryMotorista;
	  
	
   public Map<String, String> salvar(Motorista motorista) {
	  Map<String, String> sucesso = new HashMap<>();
	   this.repositoryMotorista.save(motorista);
	  sucesso.put("sucesso","Motorista salvo com sucesso"); 
		return sucesso;
		} 
	  public String deleteById(long id) {
		  this.repositoryMotorista.deleteById(id);
		  return "deletado com sucesso"; 
	  }
	  public List<Motorista> findAll(){ 
		  return this.repositoryMotorista.findAll();
	  }
	  public String update(Motorista motorista, long id)  {
		  motorista.setId(id); 
		  motorista.getTotalViagens(); 
		  this.repositoryMotorista.save(motorista);
		  return "Motorista actualizado com sucesso" ;
	  }
	  //Busca pelo id do motorista
	 public Motorista findById(long id) {
   return this.repositoryMotorista.findById(id).get();
	 
	 } 	
	 //Faz a busca pelo nome do motorista
	 public List<Motorista> findByNome(String nomeMotorista){
		return this.repositoryMotorista.findByNomeContainingIgnoreCase(nomeMotorista);
 
	 }
	
}
