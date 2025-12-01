package com.GestaoRotas.GestaoRotas.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;


@Configuration
public class SecurityManager {
	
	@Autowired
	private LoginRepository loginRepository;


    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


    @Bean
    AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}


    @Bean
    UserDetailsService userDetailsService() {
    return username -> {
        Optional<Usuario> usuarioOpt = loginRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }
    
    Usuario usuario = usuarioOpt.get();
    
    // Verificar se o usuário está ativo
    if (!usuario.getAtivo()) {
        throw new DisabledException("Usuário desativado: " + username);
    }
    
    // Verificar se a conta não está bloqueada
    if (!usuario.isAccountNonLocked()) {
        throw new LockedException("Conta bloqueada: " + username);
    }
    
    return usuario;
};
	

}
	}
