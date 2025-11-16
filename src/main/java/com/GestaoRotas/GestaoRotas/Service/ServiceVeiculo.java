package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
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
	
	 
	public String update(Veiculo veiculo, long id) {
		
		 veiculo.setId(id);
		this.repositoryVeiculo.save(veiculo);
		return "veiculo actualizado com sucesso";
	}
	public String deletar(long id) {
		this.repositoryVeiculo.deleteById(id);
		return "Veiculo deletado com sucesso";
	}
	  
	  public List<VeiculoDTO> findAll() {
	        List<Veiculo> veiculos = repositoryVeiculo.findAll();
	        return veiculos.stream()
	                      .map(this::convertToDTO)
	                      .collect(Collectors.toList());
	    }
	  //Depois da DTO ser convertidade
	    
	  private VeiculoDTO convertToDTO(Veiculo veiculo) {
		    return new VeiculoDTO(
		        veiculo.getId(),
		        veiculo.getMatricula(),      // placa
		        veiculo.getModelo(),         // modelo 
		        veiculo.getAnoFabricacao(),  // ano
		        determinarTipoVeiculo(veiculo.getModelo()), // tipo
		        veiculo.getMarca(),
		        veiculo.getMotoristas()      // Set<Motorista>
		    );
		}

		// Método auxiliar para determinar tipo
		private String determinarTipoVeiculo(String modelo) {
		    if (modelo == null) return "OUTRO";
		    modelo = modelo.toUpperCase();
		    if (modelo.contains("CAMINHAO")) return "CAMINHAO";
		    if (modelo.contains("VAN")) return "VAN";
		    if (modelo.contains("CARRO")) return "CARRO";
		    if (modelo.contains("MOTO")) return "MOTO";
		    return "OUTRO";
		}	
	        
}
