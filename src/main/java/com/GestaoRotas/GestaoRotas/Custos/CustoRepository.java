package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoListDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.VeiculoCustoDTO;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import jakarta.*;
import java.util.*;
import java.awt.print.Pageable;
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
                      
    //metodo calcular o numero de custos  apenas por periodo 
    @Query("SELECT COUNT(c) FROM Custo c WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO'")
    Integer numeroTotalCustoPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE c.tipo = :tipo AND c.status = 'PAGO'")
    Double calcularTotalPorTipo(@Param("tipo") TipoCusto tipo);
      
    //conta todos 
    @Query("SELECT COUNT(c) FROM Custo c") 
    Long countAll();
    
    
    Integer countByStatus(StatusCusto status); 
    
     Integer countByTipo(TipoCusto tipo); 
     
    // Agrupamentos 
    @Query("SELECT c.tipo, SUM(c.valor) FROM Custo c " +
           "WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes AND c.status = 'PAGO' " +
           "GROUP BY c.tipo")
    List<Object[]> calcularTotalPorTipoAgrupado(@Param("ano") Integer ano, @Param("mes") Integer mes);
      
    @Query("SELECT c.tipo, SUM(c.valor) FROM Custo c " +
           "WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO' " +
           "GROUP BY c.tipo")
    List<Object[]> calcularTotalPorTipoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    
    // querys usando dto
    
    // Query para findAll seguro usando DTO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.CustoDTO.CustoListDTO(" +
           "c.id, c.data, c.descricao, c.valor, c.tipo, c.status, " +
           "v.id, v.matricula, " +
           "CASE " + 
           "  WHEN c.abastecimento IS NOT NULL THEN 'ABASTECIMENTO' " +
           "  WHEN c.manutencao IS NOT NULL THEN 'MANUTENCAO' " +
           "  WHEN c.viagem IS NOT NULL THEN 'VIAGEM' " +
           "  ELSE 'MANUAL' " +
           "END) " +
           "FROM Custo c " +
           "LEFT JOIN c.veiculo v " +
           "ORDER BY c.data DESC")
    List<CustoListDTO> findAllAsDTO();
    
    // Query nativa segura
    @Query(value = """
        SELECT 
            c.id,
            c.data,
            c.descricao,
            c.valor,
            c.tipo,
            c.status,
            COALESCE(v.id, 0) as veiculo_id,
            COALESCE(v.matricula, 'N/A') as veiculo_matricula,
            CASE 
                WHEN c.abastecimento_id IS NOT NULL THEN 'ABASTECIMENTO'
                WHEN c.manutencao_id IS NOT NULL THEN 'MANUTENCAO'
                WHEN c.viagem_id IS NOT NULL THEN 'VIAGEM'
                ELSE 'MANUAL'
            END as origem 
        FROM Custo c  
        LEFT JOIN Veiculo v ON c.veiculo_id = v.id
        ORDER BY c.data DESC
        """, nativeQuery = true)
    List<Object[]> findAllNativeSafe();
    
    // Query nativa para relatório
    @Query(value = """
        SELECT     
            c.id,
            c.data,
            c.descricao,
            c.valor,
            c.tipo,
            c.status,
            v.id as veiculo_id,
            v.matricula as veiculo_matricula,
            v.modelo as veiculo_modelo
        FROM Custo c
        LEFT JOIN Veiculo v ON c.veiculo_id = v.id
        WHERE 
            (:inicio IS NULL OR c.data >= :inicio) AND
            (:fim IS NULL OR c.data <= :fim) AND
            (:veiculoId IS NULL OR c.veiculo_id = :veiculoId)
        ORDER BY c.data DESC
        """, nativeQuery = true)
    List<Object[]> findForReport(@Param("inicio") LocalDate inicio,
                                 @Param("fim") LocalDate fim,
                                 @Param("veiculoId") Long veiculoId);
    
    // Contagem simples
    
    @Query("SELECT new com.GestaoRotas.GestaoRotas.CustoDTO.VeiculoCustoDTO(" +
    	       "v.matricula, v.modelo, SUM(c.valor)) " +
    	       "FROM Custo c " +
    	       "JOIN c.veiculo v " +
    	       "WHERE FUNCTION('YEAR', c.data) = :ano " +
    	       "AND FUNCTION('MONTH', c.data) = :mes " +
    	       "AND c.status = 'PAGO' " +
    	       "GROUP BY v.id, v.matricula, v.modelo " +
    	       "ORDER BY SUM(c.valor) DESC")
    	List<VeiculoCustoDTO> findTop5VeiculosMaisCaros(@Param("ano") Integer ano, @Param("mes") Integer mes);
     
    @Query("SELECT new com.GestaoRotas.GestaoRotas.CustoDTO.VeiculoCustoDTO(" +
 	       "v.matricula, v.modelo, SUM(c.valor)) " +
 	       "FROM Custo c " +
 	       "JOIN c.veiculo v " + 
 	       "WHERE c.data BETWEEN :dataInicio AND :dataFim " +
 	       "AND c.status = 'PAGO' " +
 	       "GROUP BY v.id, v.matricula, v.modelo " +
 	       "ORDER BY SUM(c.valor) DESC")
 	List<VeiculoCustoDTO> findTop5VeiculosMaisCarosPorPeriodo(
 	    @Param("dataInicio") LocalDate dataInicio,
 	    @Param("dataFim") LocalDate dataFim);
    
 
	//esse retorna o objecto ?  
	 @Query("SELECT v.matricula, v.modelo, SUM(c.valor) FROM Custo c " +
		       "JOIN c.veiculo v " +
		       "WHERE YEAR(c.data) = :ano AND MONTH(c.data) = :mes AND c.status = 'PAGO' " +
		       "GROUP BY v.id, v.matricula, v.modelo " + 
		       "ORDER BY SUM(c.valor) DESC " +
		       "LIMIT 5")  
		List<Object[]> findTop5VeiculosMaisCaross(@Param("ano") Integer ano, @Param("mes") Integer mes);
		
		// top 5 os custos mais altos
		@Query("SELECT new com.GestaoRotas.GestaoRotas.CustoDTO.CustoDetalhadoDTO(" +
			       "c.id, c.descricao, c.valor, c.data, c.tipo, c.status, " +
			       "v.matricula, v.modelo) " + 
			       "FROM Custo c " +
			       "JOIN c.veiculo v " +
			       "WHERE c.status = 'PAGO' " +
			       "ORDER BY c.valor DESC "+
			       "LIMIT 5")
			List<CustoDetalhadoDTO> findTop5CustosMaisAltos(org.springframework.data.domain.Pageable pageable);
		
    @Query("SELECT v.matricula, SUM(c.valor) FROM Custo c " +
           "JOIN c.veiculo v " +
           "WHERE c.data BETWEEN :inicio AND :fim AND c.status = 'PAGO' " +
           "GROUP BY v.id, v.matricula")
    List<Object[]> calcularTotalPorVeiculoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    // Consulta com filtros multiplos
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
    // Consulta principal com todos os filtros
    @Query("SELECT c FROM Custo c WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " + 
           "(:veiculoId IS NULL OR c.veiculo.id = :veiculoId) AND " +
           "(:tipoCusto IS NULL OR c.tipo = :tipoCusto) AND " +
           "(:statusCusto IS NULL OR c.status = :statusCusto) " +
           "ORDER BY c.data DESC") 
    List<Custo> findByFiltros(@Param("inicio") LocalDate inicio, 
                              @Param("fim") LocalDate fim, 
                              @Param("veiculoId") Long veiculoId,
                              @Param("tipoCusto") TipoCusto tipoCusto,
                              @Param("statusCusto") StatusCusto statusCusto);
    
    // Total por período com filtros
    @Query("SELECT SUM(c.valor) FROM Custo c WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " +
           "(:veiculoId IS NULL OR c.veiculo.id = :veiculoId) AND " +
           "(:tipoCusto IS NULL OR c.tipo = :tipoCusto) AND " +
           "(:statusCusto IS NULL OR c.status = :statusCusto)")
    Double calcularTotalComFiltros(@Param("inicio") LocalDate inicio,
                                   @Param("fim") LocalDate fim,
                                   @Param("veiculoId") Long veiculoId,
                                   @Param("tipoCusto") TipoCusto tipoCusto,
                                   @Param("statusCusto") StatusCusto statusCusto);
    
    // Total por veículo com filtros
    @Query("SELECT v.matricula, SUM(c.valor) FROM Custo c " +
           "JOIN c.veiculo v WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " +
           "(:tipoCusto IS NULL OR c.tipo = :tipoCusto) AND " +
           "(:statusCusto IS NULL OR c.status = :statusCusto) " +
           "GROUP BY v.id, v.matricula")
    List<Object[]> calcularTotalPorVeiculoComFiltros(@Param("inicio") LocalDate inicio,
                                                     @Param("fim") LocalDate fim,
                                                     @Param("tipoCusto") TipoCusto tipoCusto,
                                                     @Param("statusCusto") StatusCusto statusCusto);
    
    // Total por tipo com filtros
    @Query("SELECT c.tipo, SUM(c.valor) FROM Custo c WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " +
           "(:veiculoId IS NULL OR c.veiculo.id = :veiculoId) AND " +
           "(:statusCusto IS NULL OR c.status = :statusCusto) " +
           "GROUP BY c.tipo")
    List<Object[]> calcularTotalPorTipoComFiltros(@Param("inicio") LocalDate inicio,
                                                  @Param("fim") LocalDate fim,
                                                  @Param("veiculoId") Long veiculoId,
                                                  @Param("statusCusto") StatusCusto statusCusto);
       
    // Quantidade de custos 
    @Query("SELECT COUNT(c) FROM Custo c WHERE " +
           "(:inicio IS NULL OR c.data >= :inicio) AND " +
           "(:fim IS NULL OR c.data <= :fim) AND " +
           "(:veiculoId IS NULL OR c.veiculo.id = :veiculoId) AND " +
           "(:tipoCusto IS NULL OR c.tipo = :tipoCusto) AND " +
           "(:statusCusto IS NULL OR c.status = :statusCusto)")
 Integer contarCustosComFiltros(@Param("inicio") LocalDate inicio,
                                @Param("fim") LocalDate fim,
                                @Param("veiculoId") Long veiculoId,
                                @Param("tipoCusto") TipoCusto tipoCusto,
                                @Param("statusCusto")  StatusCusto statusCusto);

    
}
 