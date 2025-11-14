package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMarca;
import java.util.*;
import lombok.Setter;

@Service
public class ServiceMarca {

	
	private final RepositoryMarca repositoryMarca;
	@Autowired
	public ServiceMarca(RepositoryMarca repositoryMarca) {
		this.repositoryMarca=repositoryMarca;
	}
	public String save(Marca marca) {
		this.repositoryMarca.save(marca);
		return "marca salva com sucesso";
	}
	public String delete(long id) {
		
		if(!this.repositoryMarca.existsById(id)) {
			return "nao existe essa marca no banco";
		} 
		this.repositoryMarca.deleteById(id);
		return "marca deletada com sucesso";
	}
	
	public String update(Marca marca, long id) {
		marca.setId(id);
		this.repositoryMarca.save(marca);
		return "marca atualizadaa com sucesso";
	}
	public List<Marca> findAll(){
		List<Marca> lista=this.repositoryMarca.findAll();
		return lista;
	}
}
