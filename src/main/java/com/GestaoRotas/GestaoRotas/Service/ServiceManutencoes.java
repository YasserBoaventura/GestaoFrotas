package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;

import jakarta.transaction.Transactional;

@Service
public class ServiceManutencoes {
	
	 private final RepositoryManutencao repositoryManuntencao;
			
	@Autowired
	public ServiceManutencoes(RepositoryManutencao repositoryManuntencao) {
	
		this.repositoryManuntencao=repositoryManuntencao;
	}
	
 public String salvar(Manutencao manutencao) {
  this.repositoryManuntencao.save(manutencao);
	return "Manutencao salva com sucesso";
	}
 
 public String deleteById(Long id) { 
	    if (!repositoryManuntencao.existsById(id)) {
	        return "Manutenção não encontrada";
	    }  else if(repositoryManuntencao.existsById(id)) {
	    	
	    }
	  this.repositoryManuntencao.deleteById(id);
	    repositoryManuntencao.flush(); // força execução imediata

	    if (repositoryManuntencao.existsById(id)) { 
	        return "Erro: manutenção não foi removida";
	    }

	    return "Manutenção deletada com sucesso";
	}
  
  public List<Manutencao> findAll(){
	  List<Manutencao> lista=this.repositoryManuntencao.findAll();
	  return lista; 
  }  
  public String update(Manutencao manutencao, long id)  {
	  manutencao.setId(id);
	  this.repositoryManuntencao.save(manutencao);
	  return "Manutencao actualizada com sucesso" ;
  }
  public Manutencao findById(long id) {
	    return repositoryManuntencao.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manutenção não encontrada"));
	}
 
 public List<Manutencao>  listarPorVeiculo(long veiculoId){
	    // Buscar todas as manutenções de um veículo
	 List<Manutencao> lista=  this.repositoryManuntencao.findByVeiculoId(veiculoId);
	   return lista;
 }

 public List<Manutencao> listarPorTipo(String tipo) {
     return repositoryManuntencao.findBytipoManutencao(tipo);
 } 
 
 
 
 
 
}