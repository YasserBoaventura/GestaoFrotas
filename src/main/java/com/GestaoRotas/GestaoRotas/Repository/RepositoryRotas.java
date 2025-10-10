package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GestaoRotas.GestaoRotas.Entity.Rotas;

@Repository
public interface RepositoryRotas extends JpaRepository<Rotas, Long> {


}
