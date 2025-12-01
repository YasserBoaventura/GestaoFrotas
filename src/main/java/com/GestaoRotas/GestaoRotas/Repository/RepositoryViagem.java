package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryViagem extends JpaRepository<Viagem, Long> {
 
	
    // Lista viagens por id do motorista  
    List<Viagem> findByMotoristaId(Long motoristaId);

    // Relatório por motorista - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
           "v.motorista.nome, " +
           "COUNT(v), " + 
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "GROUP BY v.motorista.id, v.motorista.nome")
    List<RelatorioMotoristaDTO> relatorioPorMotorista();

    // Relatório por veículo - CORRIGIDO
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
           "v.veiculo.matricula, " +
           "COUNT(v), " +
           "COALESCE(SUM(v.kilometragemFinal - v.kilometragemInicial), 0), " +
           "COALESCE(SUM(a.quantidadeLitros), 0)) " +
           "FROM Viagem v " +
           "LEFT JOIN v.abastecimentos a " +
           "WHERE v.status = 'CONCLUIDA' " +
           "GROUP BY v.veiculo.id, v.veiculo.matricula")
    List<RelatorioPorVeiculoDTO> relatorioPorVeiculo();
   
    // Consultas adicionais úteis
    List<Viagem> findByStatus(String status);
    List<Viagem> findByVeiculoId(Long veiculoId);
    List<Viagem> findByDataHoraPartidaBetween(LocalDateTime start, LocalDateTime end);

}
   