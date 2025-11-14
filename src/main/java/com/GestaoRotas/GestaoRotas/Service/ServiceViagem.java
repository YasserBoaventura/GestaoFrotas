package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;



@Service

public class ServiceViagem {

  
	private final RepositoryViagem repositoriViagem;
	
	@Autowired
	public ServiceViagem(RepositoryViagem repositoryViagem) {
		this.repositoriViagem=repositoryViagem;
	}
	public String salvar(Viagem viagem) {
		this.repositoriViagem.save(viagem);
		return "Viagem salva com sucesso";
	}
	public List<Viagem> findAll(){
    return this.repositoriViagem.findAll();
    }
	
	public String delete(long id) {
		this.repositoriViagem.deleteById(id);
		if(!this.repositoriViagem.existsById(id))
			return "Viagem nao encontrada";
		return "deletado com sucesso";
	}
	public List<Viagem> findByIdMotorista(long id){
		return this.repositoriViagem.findByMotorista_Id(id);
	} 
	
	public String update(Viagem viagem , long id) {
		viagem.setId(id);
		this.repositoriViagem.save(viagem);
		if(!this.repositoriViagem.existsById(id))
			return "nao existem uma viagem com id"; 
		return "viagem actualizada com sucesso";
	}
	//Mostra o motorista totalViagens , totalEmKm e totalConbustivel usado

    public List<RelatorioMotoristaDTO> relatorioPorMotorista() {
        return repositoriViagem.relatorioPorMotorista();
    }
    
    //Mostra o plca do carro , totalViagens , totalEmKm e totalConbustivel usado

    public List<RelatorioPorVeiculoDTO> gerarRelatorioPorVeiculo() {
        return repositoriViagem.relatorioPorVeiculo();  
    }
    
    //Busca pelo o id da viagem
    public Viagem findById(long id) {
    	return this.repositoriViagem.findById(id).get();
    }
    
	                   
	
}
