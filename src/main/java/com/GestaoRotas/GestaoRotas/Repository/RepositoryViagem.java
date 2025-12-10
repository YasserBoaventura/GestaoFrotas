package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.GestaoRotas.GestaoRotas.DTO.*;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;

@Repository
public interface RepositoryViagem extends JpaRepository<Viagem, Long> {
 
    // Lista viagens por id do motorista  
    List<Viagem> findByMotoristaId(Long motoristaId);

    // Relatório por motorista
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
           "v.motorista.nome, " +
            "v.motorista.telefone," +
            "v.status ,"+
           "COUNT(v), " +   
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +    
           "GROUP BY v.motorista.id, v.motorista.nome")
    List<RelatorioMotoristaDTO> relatorioPorMotorista();
        
    // Relatório por veículo
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
           "v.veiculo.matricula, " +
            "v.veiculo.modelo, "+ 
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN  v.abastecimentos a " + 
           "WHERE v.status = 'CONCLUIDA' " +
           "GROUP BY v.veiculo.id, v.veiculo.matricula")
    List<RelatorioPorVeiculoDTO> relatorioPorVeiculo(); 
   
    // Consultas adicionais úteis 
    List<Viagem> findByStatus(String status);
    List<Viagem> findByVeiculoId(Long veiculoId);
    List<Viagem> findByDataHoraPartidaBetween(LocalDateTime start, LocalDateTime end);
    
    // NOVAS CONSULTAS CORRIGIDAS
    
    // 1. Relatório por motorista com filtro de data - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
           "v.motorista.nome, " +
            "v.motorista.telefone," +
            "v.status," +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.motorista.id, v.motorista.nome")
    List<RelatorioMotoristaDTO> relatorioPorMotoristaPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 2. Relatório por veículo com filtro de data - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
           "v.veiculo.matricula, " +
            "v.veiculo.modelo, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.veiculo.id, v.veiculo.matricula")
    List<RelatorioPorVeiculoDTO> relatorioPorVeiculoPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 3. Relatório diário de viagens (agrupado por dia) - CORRIGIDO (usando CAST)
    @Query("""
    	    SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioDiarioDTO(
    	        FUNCTION('DATE', v.dataHoraPartida),
    	        COUNT(v),
    	        COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0),
    	        COALESCE(SUM(a.quantidadeLitros), 0)
    	    )
    	    FROM Viagem v
    	    LEFT JOIN v.abastecimentos a 
    	    WHERE v.status = 'CONCLUIDA'
    	      AND v.dataHoraPartida BETWEEN :dataInicio AND :dataFim
    	    GROUP BY FUNCTION('DATE', v.data)
    	    ORDER BY FUNCTION('DATE', v.data) DESC
    	""")
    	List<RelatorioDiarioDTO> relatorioDiarioPorPeriodo(
    	        LocalDateTime dataInicio,
    	        LocalDateTime dataFim);


    
    // 4. Relatório mensal de viagens (agrupado por mês) - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMensalDTO(" +
           "YEAR(v.data), " +
           "MONTH(v.data), " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.dataHoraPartida BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY YEAR(v.data), MONTH(v.data) " +
           "ORDER BY YEAR(v.data) DESC, MONTH(v.data) DESC")
    List<RelatorioMensalDTO> relatorioMensalPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 5. Relatório geral (resumo) por período - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioGeralDTO(" +
           "COUNT(v), " +
           "COUNT(DISTINCT v.motorista.id), " +
           "COUNT(DISTINCT v.veiculo.id), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "COALESCE(AVG(v.kilometragemFinal - v.kilometragemInicial), 0)) " +
           "FROM Viagem v " +     
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim")
    RelatorioGeralDTO relatorioGeralPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 6. Relatório por semana - CORRIGIDO (usando função do MySQL WEEK)
    @Query(value = "SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioSemanalDTO(" +
           "CONCAT('Semana ', WEEK(v.data_hora_partida, 1)), " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragem_final - v.kilometragem_inicial), 0), " +
           "COALESCE(SUM(a.quantidade_litros), 0)) " +
           "FROM viagem v " +
           "LEFT JOIN abastecimento a ON v.id = a.viagem_id " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY WEEK(v.data, 1) " +
           "ORDER BY WEEK(v.data, 1) DESC", nativeQuery = true)
    List<RelatorioSemanalDTO> relatorioSemanalPorPeriodo( 
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 7. Viagens por período e status - NOVO método
    @Query("SELECT v FROM Viagem v WHERE v.data BETWEEN :dataInicio AND :dataFim AND v.status = :status")
    List<Viagem> findByDataHoraPartidaBetweenAndStatus(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("status") String status);
    
    // 8. Top 5 motoristas com mais viagens no período - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioTopMotoristasDTO(" +
           "v.motorista.nome, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0)) " +
           "FROM Viagem v " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.motorista.id, v.motorista.nome " +
           "ORDER BY COUNT(v) DESC")
    List<RelatorioTopMotoristasDTO> findTopMotoristasPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
    
    // 9. Contagem de viagens por período
    Long countByDataHoraPartidaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    // 10. Contagem de viagens por status
    Long countByStatus(String status);
}