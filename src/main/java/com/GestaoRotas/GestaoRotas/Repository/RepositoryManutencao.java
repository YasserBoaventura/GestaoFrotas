package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;

@Repository
public interface RepositoryManutencao  extends JpaRepository<Manutencao, Long>{


     
    //Busca pelo tipo da manutencao
    List<Manutencao>  findBytipoManutencao (String tipoManutencao);
   
    //relatorio Media soma e o numero de manuntencoes feitas por cada veiculo
     @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO(" +
    	       "m.veiculo.matricula, COUNT(m), SUM(m.custo), AVG(m.custo)) " +
    	       "FROM  Manutencao  m WHERE m.veiculo IS NOT NULL GROUP BY m.veiculo.matricula")
    	List<RelatorioManutencaoDTO> relatorioPorVeiculo();
             
    
     // Manutenções vencidas
     @Query("SELECT m FROM Manutencao m WHERE " +
            "(m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData < CURRENT_DATE) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual >= m.proximaManutencaoKm)")
     List<Manutencao> findManutencoesVencidas();
     
     // Próximas manutenções (30 dias ou 1000km)
     @Query("SELECT m FROM Manutencao m WHERE " +
            "((m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData BETWEEN CURRENT_DATE AND FUNCTION('DATE_ADD', CURRENT_DATE, 30, 'DAY')) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual IS NOT NULL AND " +
            "m.proximaManutencaoKm - m.veiculo.kilometragemAtual <= 1000)) AND " +
            "NOT ((m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData < CURRENT_DATE) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual >= m.proximaManutencaoKm))")
     List<Manutencao> findProximasManutencoes();
     
     // Manutenções muito próximas (7 dias ou 200km)
     @Query("SELECT m FROM Manutencao m WHERE " +
            "((m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData BETWEEN CURRENT_DATE AND FUNCTION('DATE_ADD', CURRENT_DATE, 7, 'DAY')) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual IS NOT NULL AND " +
            "m.proximaManutencaoKm - m.veiculo.kilometragemAtual <= 200)) AND " +
            "NOT ((m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData < CURRENT_DATE) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual >= m.proximaManutencaoKm))")
     List<Manutencao> findManutencoesProximas7Dias();
 

     // Manutenções por veículo
     @Query("SELECT m FROM Manutencao m WHERE m.veiculo.id = :veiculoId ORDER BY m.dataManutencao DESC")
     List<Manutencao> findByVeiculoId(Long veiculoId);
 }
 