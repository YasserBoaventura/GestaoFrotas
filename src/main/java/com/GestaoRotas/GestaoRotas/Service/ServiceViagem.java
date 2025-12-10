package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;



@Service
public class ServiceViagem {

  
	 private final RepositoryViagem repositoriViagem;
	 @Autowired
	    private RepositoryViagem viagemRepository;
	    
	    @Autowired
	    private RepositoryMotorista motoristaRepository;
	    
	    @Autowired
	    private RepositoryVeiculo veiculoRepository;
	    
	    @Autowired
	    private RepositoryRotas rotaRepository;
	public ServiceViagem(RepositoryViagem repositoryViagem) {
		this.repositoriViagem=repositoryViagem;
		
	}

public Viagem update(ViagensDTO viagemDTO, long id) {

    Viagem viagem = viagemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));

    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
            .orElseThrow(() -> new RuntimeException("Rota não encontrada"));

    viagem.setMotorista(motorista);
    viagem.setVeiculo(veiculo);  
    viagem.setRota(rota);
    viagem.setDataHoraPartida(viagemDTO.getDataHoraPartida());
    viagem.setDataHoraChegada(viagemDTO.getDataHoraChegada());
    viagem.setStatus(viagemDTO.getStatus());
    viagem.setKilometragemInicial(viagemDTO.getKilometragemInicial());
    viagem.setKilometragemFinal(viagemDTO.getKilometragemFinal());
    viagem.setObservacoes(viagemDTO.getObservacoes());

    return viagemRepository.save(viagem);
}
 
	public Viagem salvar(ViagensDTO viagemDTO) {
	    Viagem viagem = new Viagem();
	    // Buscar motorista, veiculo e rota pelos IDs
	    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
	        .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
	    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
	        .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
	    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
	        .orElseThrow(() -> new RuntimeException("Rota não encontrada"));

	    viagem.setMotorista(motorista);
	    viagem.setVeiculo(veiculo);  
	    viagem.setRota(rota);
	    viagem.setDataHoraPartida(viagemDTO.getDataHoraPartida());
	    viagem.setDataHoraChegada(viagemDTO.getDataHoraChegada());
	    viagem.setStatus(viagemDTO.getStatus());
	    viagem.setKilometragemInicial(viagemDTO.getKilometragemInicial());
	    viagem.setKilometragemFinal(viagemDTO.getKilometragemFinal());
	    viagem.setObservacoes(viagemDTO.getObservacoes());
	    viagem.setData(LocalDateTime.now());

	    return viagemRepository.save(viagem);
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
		return this.repositoriViagem.findByMotoristaId(id);
	} 
	
	//Mostra o motorista totalViagens , totalEmKm e totalConbustivel usado

    public List<RelatorioMotoristaDTO> relatorioPorMotorista() {
        return repositoriViagem.relatorioPorMotorista();
    }
     
    //Mostra o plca do carro , totalViagens , totalEmKm e totalConbustivel usado

    public List<RelatorioPorVeiculoDTO> gerarRelatorioPorVeiculo() {
        return repositoriViagem.relatorioPorVeiculo();  
    }
    
    public  Viagem  findByVeiculoId(long id) {
    	
    	Viagem viagem = this.repositoriViagem.findByVeiculoId(id).get(0);
    	return viagem;   
    }
    //Busca pelo o id da viagem  
    public Viagem findById(long id) {
    	return this.repositoriViagem.findById(id).get();
    }
    
	                   
	
}
