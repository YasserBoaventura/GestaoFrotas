package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;
import com.GestaoRotas.GestaoRotas.RecuperacaoSenha.RecuperacaoSenhaService;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.*;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RecoveyPasswordServiceIntegrationTest {

	 @Autowired
	    private RecuperacaoSenhaService recuperacaoSenhaService;

	    @Autowired
	    private LoginRepository loginRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    private Usuario usuario;
	    private recuperacaoSenhaDTO dto;

	     @BeforeEach
	     void setUp() {
	        // Criar usuário real no banco
	        usuario = new Usuario();
	        usuario.setUsername("joaosilva");
	        usuario.setEmail("joao@email.com");
	        usuario.setNuit("123456788");
	        usuario.setPassword(passwordEncoder.encode("senhaOriginal"));
	        usuario.setAtivo(true);
	        usuario.setRole("ADMIN");
	        usuario.setPerguntaSeguranca("Qual é o nome da sua mãe?");
	        usuario.setRespostaSeguranca("Maria");
	        usuario.setCodigoVerificado(false);
	        usuario.setTokenUtilizado(false);
	        usuario = loginRepository.save(usuario);

	        // Setup DTO
	        dto = new recuperacaoSenhaDTO();
	        dto.setUsername("joaosilva");
	        dto.setEmail("joao@email.com");
	        dto.setNuit("123456788");
	        dto.setRespostaSeguranca("Maria");
	        dto.setNovaSenha("novaSenha123");
	    }

    @Test
    void solicitarRecuperacaoSenha_DeveGerarCodigoEToken() {
        // Act
        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

        // Assert
        assertNotNull(response);
        assertEquals("sucesso", response.get("status"));
        assertNotNull(response.get("token"));
        assertNotNull(response.get("perguntaSeguranca"));
        
        // Verificar se o token foi salvo no banco
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertTrue(usuarioAtualizado.isPresent());
        assertNotNull(usuarioAtualizado.get().getResetToken());
        assertNotNull(usuarioAtualizado.get().getCodigoVerificacao());
        assertEquals(6, usuarioAtualizado.get().getCodigoVerificacao().length());
    }

	    @Test
	    void verificarRespostaSeguranca_ComRespostaCorreta_DeveRetornarTrue() {
	        // Act
	        boolean resultado = recuperacaoSenhaService.verificarRespostaSeguranca("joaosilva", "Maria");

	        // Assert
	        assertTrue(resultado);
	    }

	    @Test
	    void verificarRespostaSeguranca_ComRespostaIncorreta_DeveRetornarFalse() {
	        // Act
	        boolean resultado = recuperacaoSenhaService.verificarRespostaSeguranca("joaosilva", "RespostaErrada");

	        // Assert
	        assertFalse(resultado);
	    }

	    @Test
	    void redefinirSenhaComToken_ComTokenValido_DeveRedefinirSenha() {
	        // Arrange
	        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        String token = response.get("token");
	        
	        // Verificar senha original
	        Optional<Usuario> usuarioOriginal = loginRepository.findByUsername("joaosilva");
	        assertTrue(passwordEncoder.matches("senhaOriginal", usuarioOriginal.get().getPassword()));

	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComToken(token, "novaSenha123");

	        // Assert
	        assertTrue(resultado);
	        
	        // Verificar se a senha foi alterada
	        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
	        assertTrue(passwordEncoder.matches("novaSenha123", usuarioAtualizado.get().getPassword()));
	    }

	    @Test
	    void redefinirSenhaComToken_ComTokenInvalido_DeveFalhar() {
	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComToken("token-invalido", "novaSenha123");

	        // Assert
	        assertFalse(resultado);
	        
	        // Verificar que a senha não foi alterada
	        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
	        assertTrue(passwordEncoder.matches("senhaOriginal", usuarioAtualizado.get().getPassword()));
	    }

    @Test
    void redefinirSenhaComVerificacao_ComDadosValidos_DeveRedefinirSenha() {
        // Arrange - Primeiro solicitar recuperação para gerar código
        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
        
        // Buscar o código gerado
        Optional<Usuario> usuarioComCodigo = loginRepository.findByUsername("joaosilva");
        String codigo = usuarioComCodigo.get().getCodigoVerificacao();
        String token = usuarioComCodigo.get().getResetToken();
        
        dto.setCodigoVerificacao(codigo);
        dto.setNovaSenha("novaSenha456");

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertTrue(resultado);
        
        // Verificar se a senha foi alterada
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertTrue(passwordEncoder.matches("novaSenha456", usuarioAtualizado.get().getPassword()));
    }

	    @Test
	    void redefinirSenhaComVerificacao_ComCodigoErrado_DeveFalhar() {
	        // Arrange
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        dto.setCodigoVerificacao("999999");

	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

	        // Assert
	        assertFalse(resultado);
	        
	        // Verificar que a senha não foi alterada
	        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
	        assertTrue(passwordEncoder.matches("senhaOriginal", usuarioAtualizado.get().getPassword()));
	    }

	    @Test
	    void redefinirSenhaComVerificacao_ComEmailErrado_DeveFalhar() {
	        // Arrange
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        dto.setEmail("emailerrado@email.com");

	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

	        // Assert
	        assertFalse(resultado);
	    }

	    @Test
	    void redefinirSenhaComVerificacao_ComNuitErrado_DeveFalhar() {
	        // Arrange
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        dto.setNuit("999999999");

	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

	        // Assert
	        assertFalse(resultado);
	    }

	    @Test
	    void redefinirSenhaComVerificacao_ComRespostaErrada_DeveFalhar() {
	        // Arrange
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        dto.setRespostaSeguranca("RespostaErrada");

	        // Act
	        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

	        // Assert
	        assertFalse(resultado);
	    }

	    @Test
	    void solicitacaoMultipla_DeveGerarNovoCodigo() {
	        // Act
	        Map<String, String> primeiraSolicitacao = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        String primeiroToken = primeiraSolicitacao.get("token");
	        
	        Optional<Usuario> usuarioAposPrimeira = loginRepository.findByUsername("joaosilva");
	        String primeiroCodigo = usuarioAposPrimeira.get().getCodigoVerificacao();
	        
	        // Segunda solicitação
	        Map<String, String> segundaSolicitacao = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        String segundoToken = segundaSolicitacao.get("token");
	        
	        Optional<Usuario> usuarioAposSegunda = loginRepository.findByUsername("joaosilva");
	        String segundoCodigo = usuarioAposSegunda.get().getCodigoVerificacao();

	        // Assert
	        assertNotEquals(primeiroToken, segundoToken);
	        assertNotEquals(primeiroCodigo, segundoCodigo);
	    }

	    @Test
	    void solicitarRecuperacaoSenha_UsuarioInativo_DeveBloquear() {
	        // Arrange
	        usuario.setAtivo(false);
	        loginRepository.save(usuario);

	        // Act
	        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

	        // Assert
	        assertNotNull(response);
	        assertEquals("Usuario nao pode fazer altercoes. sua conta esta inativa", response.get("status"));
	    }

	    @Test
	    void redefinirSenhaComToken_TokenUtilizado_DeveFalhar() {
	        // Arrange
	        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        String token = response.get("token");
	        
	        // Primeira redefinição - deve funcionar
	        boolean primeiraRedefinicao = recuperacaoSenhaService.redefinirSenhaComToken(token, "primeiraSenha");
	        assertTrue(primeiraRedefinicao);
	        
	        // Segunda redefinição com mesmo token - deve falhar
	        boolean segundaRedefinicao = recuperacaoSenhaService.redefinirSenhaComToken(token, "segundaSenha");
	        assertFalse(segundaRedefinicao);
	    }

	    @Test
	    void codigoVerificacao_DeveExpiracaoCorreta() {
	        // Act
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        
	        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
	        
	        // Assert
	        assertNotNull(usuarioAtualizado.get().getCodigoVerificacaoExpiry());
	        assertTrue(usuarioAtualizado.get().getCodigoVerificacaoExpiry().isAfter(LocalDateTime.now()));
	        // A expiração deve ser em até 10 minutos
	        assertTrue(usuarioAtualizado.get().getCodigoVerificacaoExpiry().isBefore(LocalDateTime.now().plusMinutes(11)));
	    }

	    @Test
	    void tokenRecuperacao_DeveExpiracaoCorreta() {
	        // Act
	        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");
	        
	        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
	        
	        // Assert
	        assertNotNull(usuarioAtualizado.get().getResetTokenExpiry());
	        assertTrue(usuarioAtualizado.get().getResetTokenExpiry().isAfter(LocalDateTime.now()));
	        // A expiração deve ser em até 2 horas
	        assertTrue(usuarioAtualizado.get().getResetTokenExpiry().isBefore(LocalDateTime.now().plusHours(2).plusMinutes(1)));
	    }
	}

