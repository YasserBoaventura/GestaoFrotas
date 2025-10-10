package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;

import org.springframework.data.jpa.repository.Query;

public interface RepositoryViagem extends JpaRepository<Viagem, Long> {
 
  public List<Viagem> findByMotorista_Id(Long id);

  
    

	//Mostra o motorista totalViagens , totalEmKm e totalCombustivel usado
    @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO(" +
    	       "v.motorista.nome, COUNT(v), SUM(v.quilometragem), SUM(v.combustivelUsado)) " +
    	       "FROM Viagem v GROUP BY v.motorista.nome")
    	   List<RelatorioMotoristaDTO> relatorioPorMotorista();
 
	//Mostra o a plca do veiculo , totalViagens , totalEmKm e totalConbustivel usado
  @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO(" +
	       "v.veiculo.tipo, COUNT(v), SUM(v.quilometragem), SUM(v.combustivelUsado)) " +
	       "FROM Viagem v GROUP BY v.veiculo.tipo")
	List<RelatorioPorVeiculoDTO> relatorioPorVeiculo(); 
}
   