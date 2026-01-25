package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import jakarta.*;
import java.util.*;
import java.time.*; 
@Repository 
public interface CustoRepository extends JpaRepository<Custo, Long> {
	 // Consultas básicas
    List<Custo> findByVeiculoIdOrderByDataDesc(Long veiculoId);
    List<Custo> findByVeiculoIdAndDataBetweenOrderByDataDesc(Long veiculoId, LocalDate inicio, LocalDate fim);
    List<Custo> findByAbastecimentoId(Long abastecimentoId);
    List<Custo> findByManutencaoId(Long manutencaoId);
    List<Custo> findByViagemId(Long viagemId);
    List<Custo> findTop10ByOrderByDataDesc();
    
    // Verificação de existência
    boolean existsByAbastecimentoId(Long abastecimentoId);
    boolean existsByManutencaoId(Long manutencaoId);
    boolean existsByViagemId(Long viagemId);
     
    // Agregações
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE c.veiculo.id = :veiculoId AND c.status = 'PAGO'")
    Double calcularTotalPorVeiculo(@Param("veiculoId") Long veiculoId);
    
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes AND c.status = 'PAGO'")
    Double calcularTotalPorPeriodo(@Param("ano") Integer ano, @Param("mes") Integer mes);
    
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO'")
    Double calcularTotalPorPeriodoCompleto(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE c.tipo = :tipo AND c.status = 'PAGO'")
    Double calcularTotalPorTipo(@Param("tipo") TipoCusto tipo);
    
    // Agrupamentos
    @Query("SELECT c.tipo, SUM(c.valor) FROM Custo c " +
           "WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes AND c.status = 'PAGO' " +
           "GROUP BY c.tipo")
    List<Object[]> calcularTotalPorTipoAgrupado(@Param("ano") Integer ano, @Param("mes") Integer mes);
    
    @Query("SELECT c.tipo, SUM(c.valor) FROM Custo c " +
           "WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO' " +
           "GROUP BY c.tipo")
    List<Object[]> calcularTotalPorTipoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    @Query("SELECT v.matricula, SUM(c.valor) FROM Custo c " +
           "JOIN c.veiculo v " +
           "WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes AND c.status = 'PAGO' " +
           "GROUP BY v.id, v.matricula " +
           "ORDER BY SUM(c.valor) DESC")
    List<Object[]> findTop5VeiculosMaisCaros(@Param("ano") Integer ano, @Param("mes") Integer mes);
    
    @Query("SELECT v.matricula, SUM(c.valor) FROM Custo c " +
           "JOIN c.veiculo v " +
           "WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO' " +
           "GROUP BY v.id, v.matricula")
    List<Object[]> calcularTotalPorVeiculoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    // Consulta com filtros múltiplos
    @Query("SELECT c FROM Custo c WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " +
           "(:veiculoId IS NULL OR c.veiculo.id = :veiculoId) " +
           "ORDER BY c.data DESC")
    List<Custo> findByPeriodo(@Param("inicio") LocalDate inicio, 
                              @Param("fim") LocalDate fim, 
                              @Param("veiculoId") Long veiculoId);
    
    // Totais detalhados por veículo
    @Query("SELECT " +
           "SUM(CASE WHEN c.status = 'PAGO' THEN c.valor ELSE 0 END) as total, " +
           "SUM(CASE WHEN c.tipo = 'COMBUSTIVEL' AND c.status = 'PAGO' THEN c.valor ELSE 0 END) as combustivel, " +
           "SUM(CASE WHEN c.tipo IN ('MANUTENCAO_PREVENTIVA', 'MANUTENCAO_CORRETIVA') AND c.status = 'PAGO' THEN c.valor ELSE 0 END) as manutencao " +
           "FROM Custo c WHERE c.veiculo.id = :veiculoId")
    Map<String, Object> calcularTotaisPorVeiculo(@Param("veiculoId") Long veiculoId);
    
    // Média geral
    @Query("SELECT AVG(v.custoTotal) FROM Veiculo v WHERE v.custoTotal IS NOT NULL")
    Double calcularMediaCustoPorVeiculo();
    
}
 