package com.GestaoRotas.GestaoRotas.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.auth.Usuario;


public interface LoginRepository extends JpaRepository<Usuario, Long>{

	public Optional<Usuario> findByUsername(String login);
	
}
