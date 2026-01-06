package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  
public class ServiceVeiculo {

	private final RepositoryVeiculo repositoryVeiculo;
	private final RepositoryManutencao repositoryManutencao;
	private final RepositoryViagem repositoryViagem;
	

	public String salvar(Veiculo veiculo) {
		this.repositoryVeiculo.save(veiculo);
		 return "Veiculo Salvo com sucesso";
	}
	
	public String update(Veiculo veiculo, long id) {
		veiculo.setId(id);
		this.repositoryVeiculo.save(veiculo);
		return "veiculo actualizado com sucesso";
	}
	public String deletar(Long id) {
		this.repositoryVeiculo.deleteById(id);
		return "Veiculo deletado com sucess";    
	}
	
  public List<Veiculo> findAll() {
  return this.repositoryVeiculo.findAll();
	    }
	     
  @Transactional 
   public void atualizarStatusVeiculo(Long veiculoId) {
    Optional<Veiculo> veiculoOpt = repositoryVeiculo.findById(veiculoId);
    if (veiculoOpt.isEmpty()) return;
  Veiculo veiculo = veiculoOpt.get();
     String novoStatus = calcularStatusVeiculo(veiculo);
     
    // Atualiza o status somente se mudou
    if (!veiculo.getStatus().equals(novoStatus)) {
        veiculo.setStatus(novoStatus);
        veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
        repositoryVeiculo.save(veiculo);
    }  
}
	/**
     * Calcula o status do veículo baseado em múltiplos fatores
     * 	     */
 private String calcularStatusVeiculo(Veiculo veiculo) {
	        // 1. Verifica se está em viagem
	 if (estaEmViagem(veiculo.getId())) {
	            return "EM_VIAGEM";
	        }
	    
	        // 2. Verifica se está em manutenção
	        if (estaEmManutencaoAtiva(veiculo.getId())) {
	            return "EM_MANUTENCAO";
	        }

	        // 3. Verifica manutenções vencidas
	        if (temManutencaoVencida(veiculo.getId())) {
	            return "MANUTENCAO_VENCIDA";
	        }

	        // 4. Verifica manutenções próximas (próximos 7 dias)
	        if (temManutencaoProxima(veiculo.getId(), 7)) {
	            return "MANUTENCAO_PROXIMA";
	        }

	        // 5. Status normal
	        return "DISPONIVEL";
	    }

	    /**
	     * Verifica se o veículo está atualmente em viagem
	     */
	    private boolean estaEmViagem(Long veiculoId) {
	        List<Viagem> viagensAtivas = repositoryViagem.findByVeiculoIdAndStatus(veiculoId, "EM_ANDAMENTO");
	        return viagensAtivas != null && !viagensAtivas.isEmpty();
	    } 
  
	    
	    /**
	     * Verifica se o veículo está em manutenção ativa
	     */ 
	    private boolean estaEmManutencaoAtiva(Long veiculoId) {
	        List<Manutencao> manutencoes = repositoryManutencao.findByVeiculoId(veiculoId);
	        return manutencoes.stream()
	                .anyMatch(m -> m.getStatus() != null && 
	                              m.getStatus().equals("EM_ANDAMENTO"));
	    } 
 
	    /**
	     * Verifica se o veículo tem manutenções vencidas
	     */
private boolean temManutencaoVencida(Long veiculoId) {
    List<Manutencao> manutencoes = repositoryManutencao.findByVeiculoId(veiculoId);
    LocalDate hoje = LocalDate.now(); 
     
    return manutencoes.stream() 
            .anyMatch(m -> {
                // Verifica por data
        if (m.getProximaManutencaoData() != null && 
            m.getProximaManutencaoData().isBefore(hoje)) {
            return true;
        }
        
        // Verifica por quilometragem
        Veiculo v = m.getVeiculo();
        if (v != null && m.getProximaManutencaoKm() != null && 
            v.getKilometragemAtual() != null &&
            v.getKilometragemAtual() >= m.getProximaManutencaoKm()) {
            return true;
        }
        
        return false;
    });
}

	    /**
	     * Verifica se o veículo tem manutenções próximas
	     */
	    private boolean temManutencaoProxima(Long veiculoId, int diasAntecedencia) {
	        List<Manutencao> manutencoes = repositoryManutencao.findByVeiculoId(veiculoId);
	        LocalDate hoje = LocalDate.now();
	        LocalDate limite = hoje.plusDays(diasAntecedencia);
    
    return manutencoes.stream()   
        .anyMatch(m -> {
            // Verifica por data  
            if (m.getProximaManutencaoData() != null && 
                m.getProximaManutencaoData().isAfter(hoje) &&
                m.getProximaManutencaoData().isBefore(limite)) {
                return true;
            } 
            
            // Verifica por quilometragem
            Veiculo v = m.getVeiculo();
            if (v != null && m.getProximaManutencaoKm() != null && 
                v.getKilometragemAtual() != null) {
                double kmRestantes = m.getProximaManutencaoKm() - v.getKilometragemAtual();
                return kmRestantes > 0 && kmRestantes <= 1000; // Próximo se faltar 1000km ou menos
            }
            
            return false;
        });
	    }

	    /**
	     * Atualiza status de todos os veículos
	     * Pode ser chamado via API ou agendado
	     */
    @Transactional 
    public void atualizarStatusTodosVeiculos() {
        List<Veiculo> todosVeiculos =  repositoryVeiculo.findAll();
        
        for (Veiculo veiculo : todosVeiculos) {
            String novoStatus = calcularStatusVeiculo(veiculo); 
            
            if (!veiculo.getStatus().equals(novoStatus.toString())) {
                veiculo.setStatus(novoStatus.toString());
                veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
            }
        }
        
        repositoryVeiculo.saveAll(todosVeiculos);
    } 

	    /**
	     * Atualiza a quilometragem do veículo e verifica status
	     */
    @Transactional 
    public void atualizarKilometragem(Long veiculoId, Double novaKilometragem) {
        Optional<Veiculo> veiculoOpt = repositoryVeiculo.findById(veiculoId);
        if (veiculoOpt.isEmpty()) return;

        Veiculo veiculo = veiculoOpt.get();
        veiculo.setKilometragemAtual(novaKilometragem);
        
        // Atualiza status após mudança de quilometragem
	        String novoStatus = calcularStatusVeiculo(veiculo); 
	        veiculo.setStatus(novoStatus);
	        veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
	        
	        repositoryVeiculo.save(veiculo);
	    }
 
	    /**
     * Agenda verificação automática de status (executa a cada hora)
     */
    @Scheduled(cron = "0 0 * * * *") // A cada hora
    @Transactional  
    public void verificarStatusAgendado() {
        System.out.println("Verificando status dos veículos...");
        atualizarStatusTodosVeiculos(); 
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
	

  
	        
}
