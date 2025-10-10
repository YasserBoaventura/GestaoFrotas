package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
@Repository
public interface RepositoryVeiculo extends JpaRepository<Veiculo, Long> {

}
