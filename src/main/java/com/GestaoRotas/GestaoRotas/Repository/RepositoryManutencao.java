package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

@Repository
public interface RepositoryManutencao  extends JpaRepository<Manutencao, Long>{
 

      
    //Busca pelo tipo da manutencao
    List<Manutencao>  findBytipoManutencao (String tipoManutencao);
   
    //relatorio Media soma e o numero de manuntencoes feitas por cada veiculo
     @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO(" +
    	       "m.veiculo.matricula, COUNT(m), SUM(m.custo), AVG(m.custo)) " +
    	       "FROM  Manutencao  m WHERE m.veiculo IS NOT NULL GROUP BY m.veiculo.matricula")
     	List<RelatorioManutencaoDTO> relatorioPorVeiculo(); 
        
        
     @Query("""
    		    SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO(
    		        m.veiculo.matricula,
    		        COUNT(m.id),
    		        SUM(m.custo),
    		        AVG(m.custo)
    		    )
    		    FROM Manutencao m
    		    WHERE m.dataManutencao BETWEEN :inicio AND :fim
    		    GROUP BY m.veiculo.matricula
    		""")
    		List<RelatorioManutencaoDTO> relatorioPorPeriodo(
    		    @Param("inicio") LocalDate inicio,
    		    @Param("fim") LocalDate fim
    		); 
     // Busca pelo tipo da manutencao
     List<Manutencao> findByTipoManutencao(String tipoManutencao);
     
  // Manutenções vencidas  
     @Query("SELECT m FROM Manutencao m WHERE " +
            "(m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData < CURRENT_DATE) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual >= m.proximaManutencaoKm)")
     List<Manutencao> findManutencoesVencidas();

     // Próximas manutenções (30 dias) - CORRIGIDA
     @Query("SELECT m FROM Manutencao m WHERE " + 
            "(m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData >= CURRENT_DATE AND m.proximaManutencaoData <= :dataLimite30Dias) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual IS NOT NULL AND " +
            "m.veiculo.kilometragemAtual < m.proximaManutencaoKm AND " +
            "(m.proximaManutencaoKm - m.veiculo.kilometragemAtual) <= 1000)")
     List<Manutencao> findProximasManutencoes(@Param("dataLimite30Dias") LocalDate dataLimite30Dias);

     // Manutenções muito próximas (7 dias) - CORRIGIDA
     @Query("SELECT m FROM Manutencao m WHERE " +
            "(m.proximaManutencaoData IS NOT NULL AND m.proximaManutencaoData >= CURRENT_DATE AND m.proximaManutencaoData <= :dataLimite7Dias) OR " +
            "(m.proximaManutencaoKm IS NOT NULL AND m.veiculo.kilometragemAtual IS NOT NULL AND " +
            "m.veiculo.kilometragemAtual < m.proximaManutencaoKm AND " +
            "(m.proximaManutencaoKm - m.veiculo.kilometragemAtual) <= 200)")
     List<Manutencao> findManutencoesProximas7Dias(@Param("dataLimite7Dias") LocalDate dataLimite7Dias);
     // Manutenções por veículo  
     @Query("SELECT m FROM Manutencao m WHERE m.veiculo.id = :veiculoId ORDER BY m.dataManutencao DESC")
     List<Manutencao> findByVeiculoId(Long veiculoId);
     
 
     // Método alternativo usando Native Query  
     @Query(value = "SELECT * FROM manutencoes m WHERE " +
             "((m.proxima_manutencao_data IS NOT NULL AND m.proxima_manutencao_data BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)) OR " +
             "(m.proxima_manutencao_km IS NOT NULL AND m.proxima_manutencao_km - (SELECT kilometragem_atual FROM veiculo WHERE id = m.veiculo_id) <= 1000)) AND " +
             "NOT ((m.proxima_manutencao_data IS NOT NULL AND m.proxima_manutencao_data < CURDATE()) OR " +
             "(m.proxima_manutencao_km IS NOT NULL AND (SELECT kilometragem_atual FROM veiculo WHERE id = m.veiculo_id) >= m.proxima_manutencao_km))", 
             nativeQuery = true)
     List<Manutencao> findProximasManutencoesNative();
     
     //busca as manuten por estatus
     List<Manutencao> findByDataManutencaoAndStatusNotIn(
    	        LocalDate dataManutencao,
    	        List<statusManutencao> status 
    	    );
     //busca as manutencao passadas
    List<Manutencao> findByDataManutencaoBeforeAndStatusNotIn(
    		LocalDate dataManutencao,
    		List<statusManutencao> status
    		 );
    
    //busca as manutencao por veiculo dataManutencao e status
    List<Manutencao> findByVeiculoIdAndDataManutencaoAndStatusNotIn(
    		Long id,
    		LocalDate dataManutencao,
    		List<statusManutencao> status
    	
    		
    		);
    //por status e data
       List<Manutencao> findByDataManutencaoAndStatus(LocalDate amanha, statusManutencao status); 
    
     // conta por status
          Long countByStatus(String status); 
 }
 