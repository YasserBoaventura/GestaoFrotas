package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.Custos.custoService;
import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  
public class ServiceAbastecimentos {
 
	
	private final RepositoryAbastecimentos repositoryAbastecimentos;
	private final RepositoryViagem  repositorioViagem;
	private final RepositoryVeiculo repositorioveiculos;
	private final custoService custoService;
	////////////
	 @Transactional
	    public Map<String, String> save(AbastecimentoDTO dto) {
	        Map<String, String> response = new HashMap<>();
	        Veiculo veiculo = repositorioveiculos.findById(dto.getVeiculoId())
	            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
	      abastecimentos abastecimento = new abastecimentos();
	        abastecimento.setVeiculo(veiculo);
	       if (dto.getViagemId() != null) {
	        	Viagem viagem = repositorioViagem.findById(dto.getViagemId())
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + dto.getViagemId()));
                abastecimento.setViagem(viagem);
        } else {
            abastecimento.setViagem(null);
        }
 
    // Preencher outros campos...
    abastecimento.setDataAbastecimento(dto.getDataAbastecimento());
    abastecimento.setKilometragemVeiculo(dto.getKilometragemVeiculo());
    abastecimento.setQuantidadeLitros(dto.getQuantidadeLitros());
    abastecimento.setPrecoPorLitro(dto.getPrecoPorLitro());
    abastecimento.setTipoCombustivel(dto.getTipoCombustivel());
    abastecimento.setStatusAbastecimento(dto.getStatusAbastecimento());
    
    abastecimentos savedAbastecimento = repositoryAbastecimentos.save(abastecimento);
  
        abastecimentos abastecimentoComViagem = repositoryAbastecimentos
            .findByIdWithViagem(savedAbastecimento.getId())
            .orElse(savedAbastecimento);
        //criando custo pra o abastecimento
      Custo custo = custoService.criarCustoParaAbastecimento(abastecimentoComViagem);
	        response.put("sucesso", "Abastecimento salvo");
	        response.put("abastecimentoId", String.valueOf(savedAbastecimento.getId()));
	        response.put("custoId", String.valueOf(custo.getId()));
	        response.put("viagemAssociada", 
	            savedAbastecimento.getViagem() != null ? 
	                String.valueOf(savedAbastecimento.getViagem().getId()) : "null");
	        
	        return response;
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

    // Se houver viagemId, busca; se não, mantém null
    if (abstecimentos.getViagemId() != null) { 
        Viagem viagem = this.repositorioViagem.findById(abstecimentos.getViagemId())
            .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + abstecimentos.getViagemId()));
        abastecimento.setViagem(viagem); 
    } else {   
        abastecimento.setViagem(null);   
    }
   abastecimentos abastecimentoActualizado =  repositoryAbastecimentos.save(abastecimento);
   //actualizando os custo pra o abastecimento actualizao
   custoService.actualizarCustoParaAbastecimento(abastecimentoActualizado); 
    return "sucesso ao actualizar abastecimento";
}   
// relario de de abastecimento por veiculo 
public List<RelatorioCombustivelDTO> relatorioPorVeiculo() {   
    return repositoryAbastecimentos.relatorioPorVeiculo();  
         
    }
public List<RelatorioCombustivelDTO> relatorioPorPeriodo(LocalDate inicio, LocalDate fim) {
    return repositoryAbastecimentos.relatorioPorPeriodo(inicio, fim); 
   }
public Long numeroAbastecimentoRealizados(){
	return repositoryAbastecimentos.contarAbastecimentosRealizados();  
} 
public Optional<Long> numeroAbastecimentoCancelados(){ 
	return repositoryAbastecimentos.contarAbastecimentosCancelados(); 
}
public Optional<Long>  numeroAbastecimentoPlaneado(){
	return repositoryAbastecimentos.contarAbastecimentosPlaneados();
}
    

}


