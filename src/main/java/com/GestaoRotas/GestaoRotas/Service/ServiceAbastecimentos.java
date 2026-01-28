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
	        
	        System.out.println("=== DEBUG: Iniciando save abastecimento ===");
	        System.out.println("ViagemId recebido no DTO: " + dto.getViagemId());
	        
	        // 1. Buscar veículo (já feito)
	        Veiculo veiculo = repositorioveiculos.findById(dto.getVeiculoId())
	            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
	        
	        // 2. Criar abastecimento
	        abastecimentos abastecimento = new abastecimentos();
	        abastecimento.setVeiculo(veiculo);
	        
	        // 3. **CRÍTICO: Buscar e associar a viagem ANTES de salvar**
	        if (dto.getViagemId() != null) {
	            System.out.println("Buscando viagem ID: " + dto.getViagemId());
	            
	            // Buscar viagem do banco
	            Viagem viagem = repositorioViagem.findById(dto.getViagemId())
	                .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + dto.getViagemId()));
	            
	            System.out.println("Viagem encontrada: ID " + viagem.getId());
	            
	            // Associar viagem ao abastecimento
	            abastecimento.setViagem(viagem);
	        } else {
	            System.out.println("DTO não tem viagemId, abastecimento ficará sem viagem");
	            abastecimento.setViagem(null);
	        }
	        
	        // Preencher outros campos...
	        abastecimento.setDataAbastecimento(dto.getDataAbastecimento());
	        abastecimento.setKilometragemVeiculo(dto.getKilometragemVeiculo());
	        abastecimento.setQuantidadeLitros(dto.getQuantidadeLitros());
	        abastecimento.setPrecoPorLitro(dto.getPrecoPorLitro());
	        abastecimento.setTipoCombustivel(dto.getTipoCombustivel());
	        abastecimento.setStatusAbastecimento(dto.getStatusAbastecimento());
	        
	        // 4. Salvar abastecimento
	        System.out.println("Salvando abastecimento...");
	        abastecimentos savedAbastecimento = repositoryAbastecimentos.save(abastecimento);
	        
	        // 5. **VERIFICAR se a viagem foi salva**
	        System.out.println("Abastecimento salvo com ID: " + savedAbastecimento.getId());
	        System.out.println("Viagem no abastecimento salvo: " + 
	            (savedAbastecimento.getViagem() != null ? 
	                "ID " + savedAbastecimento.getViagem().getId() : "NULL"));
	        
	        // 6. Recarregar abastecimento com viagem (para garantir)
	        abastecimentos abastecimentoComViagem = repositoryAbastecimentos
	            .findByIdWithViagem(savedAbastecimento.getId())
	            .orElse(savedAbastecimento);
	        
	        System.out.println("Após recarregar - Viagem: " + 
	            (abastecimentoComViagem.getViagem() != null ? 
	                "ID " + abastecimentoComViagem.getViagem().getId() : "NULL"));
	        
	        // 7. Criar custo
	        System.out.println("Chamando criarCustoParaAbastecimento...");
	        Custo custo = custoService.criarCustoParaAbastecimento(abastecimentoComViagem);
	        
	        System.out.println("Custo criado com ID: " + custo.getId());
	        System.out.println("Viagem no custo: " + 
	            (custo.getViagem() != null ? "ID " + custo.getViagem().getId() : "NULL"));
	        
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
//numero de abastecimento d realizados
public Long numeroAbastecimentoRealizados(){
	return repositoryAbastecimentos.contarAbastecimentosRealizados();  
} 
//numero de abastecimentos cancelados
public Optional<Long> numeroAbastecimentoCancelados(){ 
	return repositoryAbastecimentos.contarAbastecimentosCancelados(); 
}
//numero de abastecimentos planeada
public Optional<Long>  numeroAbastecimentoPlaneado(){
	return repositoryAbastecimentos.contarAbastecimentosPlaneados();
	}
    

}


