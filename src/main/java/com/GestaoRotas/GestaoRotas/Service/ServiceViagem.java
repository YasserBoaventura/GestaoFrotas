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
	    private final RepositoryMotorista motoristaRepository;
	    private final RepositoryVeiculo veiculoRepository;
	    private final RepositoryRotas rotaRepository;
	
     public ServiceViagem(RepositoryViagem repositoryViagem, RepositoryVeiculo veiculoRepository,  RepositoryMotorista motoristaRepository, RepositoryRotas rotaRepository) {
		this.repositoriViagem=repositoryViagem;
		this.motoristaRepository=motoristaRepository;
		this.veiculoRepository=veiculoRepository;
		this.rotaRepository=rotaRepository;
		
		
	}

public String update(ViagensDTO viagemDTO, long id) {

    Viagem viagem = repositoriViagem.findById(id)
            .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));

    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
            .orElseThrow(() ->  new RuntimeException("Rota não encontrada"));
   
    validarMotorista(motorista);
   
    viagem.setMotorista(motorista);
    viagem.setVeiculo(veiculo);  
    viagem.setRota(rota);
    viagem.setDataHoraPartida(viagemDTO.getDataHoraPartida());
    viagem.setDataHoraChegada(viagemDTO.getDataHoraChegada());
    viagem.setStatus(viagemDTO.getStatus());
    viagem.setKilometragemInicial(viagemDTO.getKilometragemInicial());
    viagem.setKilometragemFinal(viagemDTO.getKilometragemFinal());
    viagem.setObservacoes(viagemDTO.getObservacoes());
    repositoriViagem.save(viagem);
    return  "viagem atualizada com sucesso!";
}
 
	public String salvar(ViagensDTO viagemDTO) {
	    Viagem viagem = new Viagem();
	    // Buscar motorista, veiculo e rota pelos IDs
	    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
	        .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
	    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
	        .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
	    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
	        .orElseThrow(() -> new RuntimeException("Rota não encontrada"));
	    //validar o motorista
	    validarMotorista(motorista);
	    
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
	    repositoriViagem.save(viagem);
	    return "viagem salva com sucesso";
	}
	// campos para validar o estato do motorista antes de ser
	//Associando a uma viagem
	private String validarMotorista(Motorista motorista) {
	    if (motorista.getStatus() == motorista.getStatus().FERIAS) {
	    	
	    	  System.out.println("nao foi possivel"); 	
	    	return "Motorista está inativo e não pode realizar viagens";
	      //new RuntimeException("Motorista está inativo e não pode realizar viagens");
	     
	    } 
	    	return "";
	    

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
    
    public  List<Viagem>  findByVeiculoId(long id) {
    	
     return this.repositoriViagem.findByVeiculoId(id);
       
    }
    //Busca pelo o id da viagem  
    public Viagem findById(long id) {
    	return this.repositoriViagem.findById(id).get();
    }
    
	                   
	
}
