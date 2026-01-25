package com.GestaoRotas.GestaoRotas.Repository;
import java.time.LocalDate;
import  jakarta.persistence.*;

import java.util.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
@Repository 
public interface RepositoryAbastecimentos extends JpaRepository<abastecimentos, Long>{

    
    @Query("SELECT a FROM abastecimentos a WHERE a.veiculo.id = :veiculoId")
    List<abastecimentos> findByVeiculoId(@Param("veiculoId") Long veiculoId);  

    @Query("SELECT a FROM abastecimentos a WHERE a.tipoCombustivel = :tipoCombustivel")
    List<abastecimentos> findByTipoCombustivel(@Param("tipoCombustivel") String tipoCombustivel);
      
    @Query("SELECT COUNT(a) FROM abastecimentos a WHERE a.statusAbastecimento = 'REALIZADA'")
    Long  contarAbastecimentosRealizados();
     
    @Query("SELECT COUNT(a) FROM abastecimentos a WHERE a.statusAbastecimento = 'CANCELADA'")
    Optional<Long>  contarAbastecimentosCancelados();  
    
    @Query("SELECT COUNT(a) FROM abastecimentos a WHERE a.statusAbastecimento = 'PLANEADA'")
    Optional<Long> contarAbastecimentosPlaneados();
   
    @Query("""    
            SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO(
                v.matricula,  
                SUM(a.quantidadeLitros), 
                SUM(a.quantidadeLitros * a.precoPorLitro), 
                AVG(a.precoPorLitro),
                AVG(a.quantidadeLitros),   
                a.statusAbastecimento   
            )
            FROM abastecimentos a 
            JOIN a.veiculo v
            WHERE a.dataAbastecimento BETWEEN :inicio AND :fim
                AND a.statusAbastecimento = 'REALIZADA'
            GROUP BY v.matricula, a.statusAbastecimento
        """)
        List<RelatorioCombustivelDTO> relatorioPorPeriodo(@Param("inicio") LocalDate inicio, 
                                                          @Param("fim") LocalDate fim);
        
        @Query("""      
            SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO(
                v.matricula,
                SUM(a.quantidadeLitros),
                SUM(a.quantidadeLitros * a.precoPorLitro),
                AVG(a.precoPorLitro),
                AVG(a.quantidadeLitros),   
                a.statusAbastecimento  
            ) 
            FROM abastecimentos a   
            JOIN a.veiculo v 
            WHERE a.statusAbastecimento = 'REALIZADA'
            GROUP BY v.matricula, a.statusAbastecimento 
        """)
        List<RelatorioCombustivelDTO> relatorioPorVeiculo(); 
     
    //----
  
      
     
} 
 



