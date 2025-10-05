package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

@Service
public class ServiceVeiculo {

	
	
	private final RepositoryVeiculo repositoryVeiculo;
	
	@Autowired 
    public ServiceVeiculo(RepositoryVeiculo repositoryVeiculo ){
		this.repositoryVeiculo=repositoryVeiculo;
	}
	public String salvar(Veiculo veiculo) {
		this.repositoryVeiculo.save(veiculo);
		 return "Veiculo Salvo com sucesso";
	}
	public List<Veiculo> findAll(){
	List<Veiculo> lista=this.repositoryVeiculo.findAll();
		return  lista;
	}
	public String update(Veiculo veiculo, long id) {
		
		 veiculo.setId(id);
		this.repositoryVeiculo.save(veiculo);
		return "veiculo actualizado com sucesso";
	}
	public String deletar(long id) {
		this.repositoryVeiculo.deleteById(id);
		return "Veiculo deletado com sucesso";
	}
	
	
}
