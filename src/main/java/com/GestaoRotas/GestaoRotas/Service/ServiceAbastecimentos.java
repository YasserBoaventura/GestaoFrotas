package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;

@Service
public class ServiceAbastecimentos {

	
	private final RepositoryAbastecimentos repositoryAbastecimentos;
	
	@Autowired
	public ServiceAbastecimentos(RepositoryAbastecimentos repositoryAbastecimentos) {
		this.repositoryAbastecimentos=repositoryAbastecimentos;
	}
	

    public String save(abastecimentos abastecimento) {
        // Calcular preço por litro se não for informado
        if (abastecimento.getPrecoPorLitro() == null && abastecimento.getQuantidade() != null && abastecimento.getValorTotal() != null) {
            abastecimento.setPrecoPorLitro(abastecimento.getValorTotal() / abastecimento.getQuantidade());
        }
        this.repositoryAbastecimentos.save(abastecimento);
        return "abastecimento concluido com sucesso";
    }
   // relario de de abastecimento por veiculo
    public List<RelatorioCombustivelDTO> relatorioPorVeiculo() {
        return repositoryAbastecimentos.relatorioPorVeiculo().stream()
                .map(obj -> new RelatorioCombustivelDTO(
                        (String) obj[0],
                        (Double) obj[1],
                        (Double) obj[2],
                        (Double) obj[3]  
                )) 
                .collect(Collectors.toList());
    }

    public List<RelatorioCombustivelDTO> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
        return repositoryAbastecimentos.relatorioPorPeriodo(inicio, fim).stream()
                .map(obj -> new RelatorioCombustivelDTO(
                        (String) obj[0],
                        (Double) obj[1],
                        (Double) obj[2],
                        (Double) obj[3]
                ))
                .collect(Collectors.toList());
    }
	//Busca tos os  abastecimentos feitos
    public List<abastecimentos> findAll(){
    List<abastecimentos> lista=this.repositoryAbastecimentos.findAll();
    return lista;
    }
	
}
