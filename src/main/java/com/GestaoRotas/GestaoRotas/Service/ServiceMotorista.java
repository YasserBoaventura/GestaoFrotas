package com.GestaoRotas.GestaoRotas.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;

import lombok.Setter;

@Service
public class ServiceMotorista {
   
	private final RepositoryMotorista repositoryMotorista;
	 
	@Autowired 
	public ServiceMotorista(RepositoryMotorista repositoryMotorista ) {
		this.repositoryMotorista=repositoryMotorista ;
		
	}
   public String salvar(Motorista motorista) {
	  this.repositoryMotorista.save(motorista);
		return "Motorista salvo com sucesso";
		}
	  public String deleteById(long id) {
		  this.repositoryMotorista.deleteById(id);
		  return "deletado com sucesso";
	  }
	  public List<Motorista> findAll(){
		  List<Motorista> lista=this.repositoryMotorista.findAll();
		  return lista; 
	  }
	  public String update(Motorista motorista, long id)  {
		  motorista.setId(id);
		  this.repositoryMotorista.save(motorista);
		  return "Motorista actualizado com sucesso" ;
	  }
	 public Motorista findById(long id) {
           Motorista motorista  =new Motorista();
           motorista =this.repositoryMotorista.findById(id).get();
	 return motorista;
	 } 	
	
}
