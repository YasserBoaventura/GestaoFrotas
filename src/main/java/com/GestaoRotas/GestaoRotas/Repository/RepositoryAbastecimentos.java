package com.GestaoRotas.GestaoRotas.Repository;
import java.time.LocalDate;
import  jakarta.persistence.*;

import java.util.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;

public interface RepositoryAbastecimentos extends JpaRepository<abastecimentos, Long>{

 
    @Query("SELECT a FROM abastecimentos a WHERE a.veiculo.id = :veiculoId")
    List<abastecimentos> findByVeiculoId(@Param("veiculoId") Long veiculoId);

    @Query("SELECT a FROM abastecimentos a WHERE a.tipoCombustivel = :tipoCombustivel")
    List<abastecimentos> findByTipoCombustivel(@Param("tipoCombustivel") String tipoCombustivel);
   
    //  RELATÓRIO SIMPLES - retorna lista de objetos 
      @Query("SELECT a.veiculo.matricula, SUM(a.quantidadeLitros), SUM(a.quantidadeLitros * a.precoPorLitro), AVG(a.precoPorLitro) " +
           "FROM abastecimentos a " +  
            "GROUP BY a.veiculo.matricula")
      List<Object[]> relatorioPorVeiculo(); 
       
    
    @Query("SELECT a.veiculo.matricula, SUM(a.quantidadeLitros), SUM(a.quantidadeLitros * a.precoPorLitro), AVG(a.precoPorLitro) " +
            "FROM  abastecimentos a " + 
            "WHERE  DATE(a.dataAbastecimento) BETWEEN :inicio AND :fim " +
           "GROUP BY a.veiculo.matricula")  
    List<Object[]> relatorioPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
     
} 




