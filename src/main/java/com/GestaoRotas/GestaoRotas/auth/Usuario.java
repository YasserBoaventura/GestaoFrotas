

package com.GestaoRotas.GestaoRotas.auth;



import java.util.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name= "usuarios")
@AllArgsConstructor
@NoArgsConstructor   
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario implements UserDetails {
    
    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
         
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String role;
    
    // Campos de segurança para recuperação de senha
    @Column(name = "reset_token", length = 100)
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
    
    @Column(name = "token_utilizado", columnDefinition = "BOOLEAN")
    private Boolean tokenUtilizado = false;
    // Campos de verificação de segurança
    @Column(name = "pergunta_seguranca", length = 200)
    private String perguntaSeguranca;
    
    @Column(name = "resposta_seguranca", length = 100)
    private String respostaSeguranca;
    
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    @Column(name = "nuit", length = 14, unique = true)
    private String nuit;
    
    @Column(name = "data_nascimento")
    private LocalDateTime dataNascimento;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
      
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso; 
    
    @Column(name = "tentativas_login")
    private Integer tentativasLogin = 0;
    
    @Column(name = "conta_bloqueada")
    private Boolean contaBloqueada = false;
     
    // Métodos de UserDetails
   
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	List<GrantedAuthority> authorities = new ArrayList<>();
    	authorities.add(new SimpleGrantedAuthority(this.role));
    	return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return !contaBloqueada;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return ativo;
    }
       
    // Métodos auxiliares para segurança
    public void incrementarTentativasLogin() {
        this.tentativasLogin++;
        if (this.tentativasLogin >= 5) {
            this.contaBloqueada = true;
        }
    }  
    
    public void resetarTentativasLogin() {
        this.tentativasLogin = 0;
        this.contaBloqueada = false;
    }
    
    public boolean isTokenValido() {
        return resetToken != null && 
               resetTokenExpiry != null && 
               LocalDateTime.now().isBefore(resetTokenExpiry) &&
               !tokenUtilizado;
    }
    
    public void invalidarToken() {
        this.tokenUtilizado = true;
    } 
   //ainda por implementar
/**
public boolean isContaBloqueada() {
    if (contaBloqueada && dataBloqueio != null) {
        // Desbloqueia automaticamente após 30 minutos
	            LocalDateTime agora = LocalDateTime.now();
	            Duration duracao = Duration.between(dataBloqueio, agora);
	            if (duracao.toMinutes() >= 30) {
	                desbloquearConta();
	                return false;
	            }  
	        }
	        return contaBloqueada;
	    }   
   */
}

 


