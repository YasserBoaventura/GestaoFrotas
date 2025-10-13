package com.GestaoRotas.GestaoRotas.Repository;
import java.time.LocalDate;
import java.util.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;

public interface RepositoryAbastecimentos extends JpaRepository<abastecimentos, Long>{

	 // Relatório geral por veículo
    @Query("SELECT a.veiculo.placa, SUM(a.quantidade), SUM(a.valorTotal), AVG(a.precoPorLitro) " +
           "FROM abastecimentos a GROUP BY a.veiculo.placa")
   public List<Object[]> relatorioPorVeiculo();

    // Relatório por período
    @Query("SELECT a.veiculo.placa, SUM(a.quantidade), SUM(a.valorTotal), AVG(a.precoPorLitro) " +
           "FROM abastecimentos a WHERE a.data BETWEEN :inicio AND :fim GROUP BY a.veiculo.placa")
   public List<Object[]> relatorioPorPeriodo(LocalDate inicio, LocalDate fim);

}
