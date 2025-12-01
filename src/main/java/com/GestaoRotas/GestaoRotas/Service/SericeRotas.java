package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;

@Service
public class SericeRotas {

	 
	private final RepositoryRotas repositoryRotas;

	public SericeRotas(RepositoryRotas repositoryRotas) {
		this.repositoryRotas=repositoryRotas;
	}
	public String save(Rotas rotas ) {
	 repositoryRotas.save(rotas);
	  return "salvo com sucesso";
	}
	public String deleteById(long id) {
		repositoryRotas.deleteById(id);
		return "deletado com sucesso";
	}
	
	 public String update(Rotas  rotas, long id) {
	 rotas.setId(id);
	 repositoryRotas.save(rotas);
		return "Rota actualizada com sucesso";
	}
	  public List<Rotas> findAll() {
	   List<Rotas> lista=this.repositoryRotas.findAll();
		return lista;  
	  }
	public Rotas findById(long id) {
	  return this.repositoryRotas.findById(id).get();
	  }
}
