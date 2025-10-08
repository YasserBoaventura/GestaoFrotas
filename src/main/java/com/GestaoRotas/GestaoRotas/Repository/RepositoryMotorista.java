package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;

@Repository
public interface RepositoryMotorista  extends JpaRepository<Motorista, Long>{
 

    List<Motorista> findByNomeContainingIgnoreCase(String nome);
	
}
