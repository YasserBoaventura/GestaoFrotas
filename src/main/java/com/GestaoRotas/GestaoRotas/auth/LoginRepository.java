package com.GestaoRotas.GestaoRotas.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.auth.Usuario;


public interface LoginRepository extends JpaRepository<Usuario, Long>{

	Usuario findByUsername(String username);
	//public Optional<Usuario> findByUsername(String login);
//para recuperar a senha
	Usuario findByResetToken(String restToken); 
	
	//Verificao dos dados para evitar cadastros de usuarios com nomes e passwords iguais
	
    boolean existsByUsername(String username);
    boolean existsByPassword(String password);
}
