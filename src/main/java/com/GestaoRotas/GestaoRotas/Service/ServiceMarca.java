package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMarca;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
public class ServiceMarca {

	 
	private final RepositoryMarca repositoryMarca;
	
	public String save(Marca marca) {
		this.repositoryMarca.save(marca);
		return "marca salva com sucesso";
	}
	public String delete(long id) { 
		this.repositoryMarca.deleteById(id);
		return "marca deletada com sucesso";
	}
	
	public String update(Marca marca, long id) {
		marca.setId(id);
		this.repositoryMarca.save(marca);
		return "marca atualizadaa com sucesso";
	}
	 public List<Marca> findAll(){
	return this.repositoryMarca.findAll();
	}
}
