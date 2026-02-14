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

    // Busca por status e veículo
    List<Viagem> findByVeiculoIdAndStatus(Long veiculoId, String status);

    // Relatório por motorista
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
           "v.motorista.nome, " +
           "v.motorista.telefone, " +
           "v.status, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "CASE WHEN SUM(a.quantidadeLitros) > 0 THEN SUM(v.kilometragemFinal - v.kilometragemInicial) / SUM(a.quantidadeLitros) ELSE 0 END" +
           ") " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "GROUP BY v.motorista.id, v.motorista.nome, v.motorista.telefone, v.status")
    List<RelatorioMotoristaDTO> relatorioPorMotorista();

    // Relatório por veículo
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
           "v.veiculo.matricula, " +
           "v.veiculo.modelo, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "CASE WHEN SUM(a.quantidadeLitros) > 0 THEN SUM(v.kilometragemFinal - v.kilometragemInicial) / SUM(a.quantidadeLitros) ELSE 0 END" +
           ") " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "GROUP BY v.veiculo.id, v.veiculo.matricula, v.veiculo.modelo")
    List<RelatorioPorVeiculoDTO> relatorioPorVeiculo();

    // Consultas adicionais
    List<Viagem> findByStatus(String status);
    List<Viagem> findByVeiculoId(Long veiculoId);
    List<Viagem> findByDataHoraPartidaBetween(LocalDateTime start, LocalDateTime end);

    // Relatório por motorista com filtro de data
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
           "v.motorista.nome, " +
           "v.motorista.telefone, " +
           "v.status, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "CASE WHEN SUM(a.quantidadeLitros) > 0 THEN SUM(v.kilometragemFinal - v.kilometragemInicial) / SUM(a.quantidadeLitros) ELSE 0 END" +
           ") " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.motorista.id, v.motorista.nome, v.motorista.telefone, v.status")
    List<RelatorioMotoristaDTO> relatorioPorMotoristaPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Relatório por veículo com filtro de data
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
           "v.veiculo.matricula, " +
           "v.veiculo.modelo, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "CASE WHEN SUM(a.quantidadeLitros) > 0 THEN SUM(v.kilometragemFinal - v.kilometragemInicial) / SUM(a.quantidadeLitros) ELSE 0 END" +
           ") " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.data BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.veiculo.id, v.veiculo.matricula, v.veiculo.modelo")
    List<RelatorioPorVeiculoDTO> relatorioPorVeiculoPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Relatório diário de viagens (agrupado por dia)
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
           GROUP BY FUNCTION('DATE', v.dataHoraPartida)
           ORDER BY FUNCTION('DATE', v.dataHoraPartida) DESC
           """)
    List<RelatorioDiarioDTO> relatorioDiarioPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Relatório mensal de viagens
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMensalDTO(" +
           "YEAR(v.dataHoraPartida), " +
           "MONTH(v.dataHoraPartida), " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.dataHoraPartida BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY YEAR(v.dataHoraPartida), MONTH(v.dataHoraPartida) " +
           "ORDER BY YEAR(v.dataHoraPartida) DESC, MONTH(v.dataHoraPartida) DESC")
    List<RelatorioMensalDTO> relatorioMensalPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Relatório geral (resumo) por período
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioGeralDTO(" +
           "COUNT(v), " +
           "COUNT(DISTINCT v.motorista.id), " +
           "COUNT(DISTINCT v.veiculo.id), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0), " +
           "CASE WHEN COUNT(v) > 0 THEN COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0) / COUNT(v) ELSE 0 END" +
           ") " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.dataHoraPartida BETWEEN :dataInicio AND :dataFim")
    RelatorioGeralDTO relatorioGeralPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Top 5 motoristas com mais viagens no período
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioTopMotoristasDTO(" +
           "v.motorista.nome, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0)) " +
           "FROM Viagem v " +
           "WHERE v.status = 'CONCLUIDA' " +
           "AND v.dataHoraPartida BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY v.motorista.id, v.motorista.nome " +
           "ORDER BY COUNT(v) DESC")
    List<RelatorioTopMotoristasDTO> findTopMotoristasPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    // Contagem de viagens por período
    Long countByDataHoraPartidaBetween(LocalDateTime inicio, LocalDateTime fim);

    // Contagem de viagens por status
    Long countByStatus(String status);

}