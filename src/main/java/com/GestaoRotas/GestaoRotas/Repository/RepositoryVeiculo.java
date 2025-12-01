package com.GestaoRotas.GestaoRotas.Repository;
import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.VeiculoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
@Repository
public interface RepositoryVeiculo extends JpaRepository<Veiculo, Long> {

	 
    
}
