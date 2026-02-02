package com.GestaoRotas.GestaoRotas.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GestaoRotas.GestaoRotas.auth.Usuario;


public interface LoginRepository extends JpaRepository<Usuario, Long>{


	//Verificao dos dados para evitar cadastros de usuarios com nomes e passwords iguais
	
    boolean existsByUsername(String username);
    boolean existsByPassword(String password);
    boolean existsByEmail(String nome);
    boolean existsByNuit(String nome);
    
    
    //consulta pra recuperacao de senha
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsernameAndEmail(String username, String email);
    Optional<Usuario> findByResetToken(String resetToken);
    Optional<Usuario> findByNuit(String nuit);
    //o optional duplo pra usar na validacao  
    Optional<Usuario> findByUsernameAndResetToken(String username, String resetToken);

          
    
}
