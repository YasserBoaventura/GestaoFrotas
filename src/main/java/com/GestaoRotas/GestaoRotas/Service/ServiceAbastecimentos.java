package com.GestaoRotas.GestaoRotas.Service;

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
	
	public ServiceAbastecimentos(RepositoryAbastecimentos repositoryAbastecimentos) {
		this.repositoryAbastecimentos=repositoryAbastecimentos;
	}
	  

    public String save(abastecimentos abastecimento) {
        // Calcular preço por litro se não for informado
        if (abastecimento.getPrecoPorLitro() == null && abastecimento.getQuantidadeLitros() != null && abastecimento.getValorTotal() != null) { }
        this.repositoryAbastecimentos.save(abastecimento);
        return "Abastecimeto Salvo com sucesso";
     }

  

	//Busca tos os  abastecimentos feitos
    public List<abastecimentos> findAll(){
    List<abastecimentos> lista=this.repositoryAbastecimentos.findAll();
              return lista;
    }  

    //Deletar por id
    public String deletar(long id) {
    	if(this.repositoryAbastecimentos.existsById(id)) {
    	this.repositoryAbastecimentos.deleteById(id);
    	return "abastecimento deletado com sucesso";
    	}
    	else if(!this.repositoryAbastecimentos.existsById(id)) {
    		return  "Nao existe um abastecimento com esse id";
    	}
    	return "...";
    	
	} 
    public abastecimentos findById(long id) {
      return this.repositoryAbastecimentos.findById(id).get();
    }
    //Atualizacao de abastecimento de foreem mal escritos
    public String update(abastecimentos abastecimento, long id) {
    	abastecimento.setId(id);
     if(this.repositoryAbastecimentos.existsById(id)) {
    	this.repositoryAbastecimentos.save(abastecimento);
    	return "abastecimento atuaizado com sucesso";
       }
       else {
    	return "Nao existe um abastecimento com esse id";
    }
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
// relatorios por periodo data fim e data inicio 
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
    

}


