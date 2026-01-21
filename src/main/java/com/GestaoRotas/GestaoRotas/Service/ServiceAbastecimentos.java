package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  
public class ServiceAbastecimentos {
 
	
	private final RepositoryAbastecimentos repositoryAbastecimentos;
	private final RepositoryViagem  repositorioViagem;
	private final RepositoryVeiculo repositorioveiculos;
	
   public Map<String, String>  save(AbastecimentoDTO abstecimentos) {
	   Map<String, String> sucess= new HashMap<>();
    Veiculo veiculo = this.repositorioveiculos.findById(abstecimentos.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
 
	    abastecimentos abastecimento = new abastecimentos();
	    abastecimento.setVeiculo(veiculo);
  
	    // Se houver viagemId, busca; se não, mantém null
    if (abstecimentos.getViagemId() != null) {
        Viagem viagem = this.repositorioViagem.findById(abstecimentos.getViagemId())
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        abastecimento.setViagem(viagem);   
    } else {     
        abastecimento.setViagem(null);    
    }
    abastecimento.setDataAbastecimento(abstecimentos.getDataAbastecimento());
    abastecimento.setKilometragemVeiculo(abstecimentos.getKilometragemVeiculo());
    abastecimento.setQuantidadeLitros(abstecimentos.getQuantidadeLitros());
    abastecimento.setStatusAbastecimento(abstecimentos.getStatusAbastecimento());
	abastecimento.setTipoCombustivel(abstecimentos.getTipoCombustivel());
	abastecimento.setPrecoPorLitro(abstecimentos.getPrecoPorLitro());
	//saving    
this.repositoryAbastecimentos.save(abastecimento);
     sucess.put("sucesso", " abstecimento salvo com sucesso");
 return  sucess; 
	}
 //Busca tos os  abastecimentos feitos
    public List<abastecimentos> findAll(){
   return this.repositoryAbastecimentos.findAll();
            
    }  
    //Deletar por id
public String deletar(long id) {
	this.repositoryAbastecimentos.deleteById(id);
    return "abastecimento  deletado com sucesso";
	
} 
public abastecimentos findById(long id) {
  return this.repositoryAbastecimentos.findById(id).get();
}
    //Atualizacao de abastecimento de foreem mal escritos
public String update(AbastecimentoDTO abstecimentos, long id) {
   
    abastecimentos abastecimento = this.repositoryAbastecimentos.findById(id)
        .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado com ID: " + id));
    
    Veiculo veiculo = this.repositorioveiculos.findById(abstecimentos.getVeiculoId())
        .orElseThrow(() -> new RuntimeException("Veiculo não encontrado com ID: " + abstecimentos.getVeiculoId()));
     
    // Atualizar campos 
    abastecimento.setVeiculo(veiculo);
    abastecimento.setDataAbastecimento(abstecimentos.getDataAbastecimento());
    abastecimento.setKilometragemVeiculo(abstecimentos.getKilometragemVeiculo());
    abastecimento.setTipoCombustivel(abstecimentos.getTipoCombustivel());
    abastecimento.setPrecoPorLitro(abstecimentos.getPrecoPorLitro());
    abastecimento.setQuantidadeLitros(abstecimentos.getQuantidadeLitros()); // Falta esta linha!
    abastecimento.setStatusAbastecimento(abstecimentos.getStatusAbastecimento());
    
 
    // abastecimento.setStatusAbastecimento(abstecimentos.getStatusAbastecimento()); // REMOVER ESTA
    
    // Se houver viagemId, busca; se não, mantém null
    if (abstecimentos.getViagemId() ==0) { 
        Viagem viagem = this.repositorioViagem.findById(abstecimentos.getViagemId())
            .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + abstecimentos.getViagemId()));
        abastecimento.setViagem(viagem);
    } else {   
        abastecimento.setViagem(null);   
    }
    
    // Adicionar log para debug
    System.out.println("Abastecimento atualizado: " + abastecimento);
    
    repositoryAbastecimentos.save(abastecimento);
    return "sucesso ao actualizar abastecimento";
}   
// relario de de abastecimento por veiculo 
public List<RelatorioCombustivelDTO> relatorioPorVeiculo() {   
    return repositoryAbastecimentos.relatorioPorVeiculo();  
         
    }
// relatorios por periodo data fim e data inicio 
public List<RelatorioCombustivelDTO> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
    return repositoryAbastecimentos.relatorioPorPeriodo(inicio, fim); 
          
}
public Long numeroAbastecimento(){
	return repositoryAbastecimentos.contarAbastecimentosRealizados();  
}

    

}


