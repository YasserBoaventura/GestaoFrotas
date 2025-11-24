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
		return "Veiculo deletado com sucess";
	}
	
	  public List<Veiculo> findAll() {
	        List<Veiculo> veiculos = repositoryVeiculo.findAll();
	         return veiculos;
	    }
	    
private VeiculoDTO convertToDTO(Veiculo veiculo) {
    VeiculoDTO dto = new VeiculoDTO();
    dto.setId(veiculo.getId());
    dto.setModelo(veiculo.getModelo());
    dto.setMatricula(veiculo.getMatricula());
    dto.setAnoFabricacao(veiculo.getAnoFabricacao());
    dto.setCapacidadeTanque(veiculo.getCapacidadeTanque());
    dto.setKilometragemAtual(veiculo.getKilometragemAtual());
    
    // Informações da marca
    if (veiculo.getMarca() != null) {
        dto.setMarcaNome(veiculo.getMarca().getNome());
        dto.setMarcaId(veiculo.getMarca().getId());
    }
    
    // Métodos calculados
    dto.setMediaConsumo(veiculo.getMediaConsumo());
    dto.setTotalViagensConcluidas(veiculo.getViagens().stream()
        .filter(v -> "CONCLUIDA".equals(v.getStatus()))
        .collect(Collectors.toList())
        .size());
        
    return dto;
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
