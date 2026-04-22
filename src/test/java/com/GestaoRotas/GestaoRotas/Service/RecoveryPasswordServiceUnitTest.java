package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*; 
import java.time.*;
import com.GestaoRotas.GestaoRotas.DTO.recuperacaoSenhaDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.RecuperacaoSenha.RecuperacaoSenhaService;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RecoveryPasswordServiceUnitTest {


    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RecuperacaoSenhaService recuperacaoSenhaService;

    private Usuario usuario;
    private recuperacaoSenhaDTO dto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joaosilva");
        usuario.setEmail("joao@email.com");
        usuario.setNuit("123456789");
        usuario.setPassword("senhaAntiga");
        usuario.setAtivo(true);
        usuario.setPerguntaSeguranca("Qual é o nome da sua mãe?");
        usuario.setRespostaSeguranca("Maria");
        usuario.setCodigoVerificacao("123456");
        usuario.setCodigoVerificacaoExpiry(LocalDateTime.now().plusMinutes(10));
        usuario.setResetToken("token-123");
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(2));
        usuario.setTokenUtilizado(false);
        usuario.setCodigoVerificado(false);

        dto = new recuperacaoSenhaDTO();
        dto.setUsername("joaosilva");
        dto.setEmail("joao@email.com");
        dto.setNuit("123456789");
        dto.setRespostaSeguranca("Maria");
        dto.setCodigoVerificacao("123456");
        dto.setNovaSenha("novaSenha123");
    }
 
    @Test
void solicitarRecuperacaoSenha_ComUsuarioValido_DeveEnviarCodigo() {
    // Arrange
    when(loginRepository.findByUsernameAndEmail("joaosilva", "joao@email.com"))
        .thenReturn(Optional.of(usuario));
    when(loginRepository.save(any(Usuario.class))).thenReturn(usuario);
    doNothing().when(emailService).enviarCodigoVerificacao(anyString(), anyString(), anyString());

    // Act
    Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

    // Assert
    assertNotNull(response);
    assertEquals("sucesso", response.get("status"));
    assertEquals("Código de verificação enviado para seu email", response.get("mensagem"));
    assertEquals("joaosilva", response.get("username"));
    assertEquals("joao@email.com", response.get("email"));
    assertEquals("10 minutos", response.get("expiraEm"));
    assertNotNull(response.get("token"));
    assertNotNull(response.get("perguntaSeguranca"));
    
    verify(emailService, times(1)).enviarCodigoVerificacao(anyString(), anyString(), anyString());
    verify(loginRepository, times(1)).save(any(Usuario.class));
}

    @Test
    void solicitarRecuperacaoSenha_ComUsuarioInativo_DeveRetornarErro() {
        // Arrange
        usuario.setAtivo(false); 
        when(loginRepository.findByUsernameAndEmail("joaosilva", "joao@email.com"))
            .thenReturn(Optional.of(usuario));

        // Act
        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

        // Assert
        assertNotNull(response);
        assertEquals("Usuario nao pode fazer altercoes. sua conta esta inativa", response.get("status"));
        verify(emailService, never()).enviarCodigoVerificacao(anyString(), anyString(), anyString());
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void solicitarRecuperacaoSenha_ComUsuarioNaoEncontrado_DeveRetornarErro() {
        // Arrange
        when(loginRepository.findByUsernameAndEmail("joaosilva", "joao@email.com"))
            .thenReturn(Optional.empty());

        // Act
        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

        // Assert
        assertNotNull(response);
        assertEquals("erro", response.get("status"));
        assertEquals("Usuário não encontrado", response.get("mensagem"));
        verify(emailService, never()).enviarCodigoVerificacao(anyString(), anyString(), anyString());
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void solicitarRecuperacaoSenha_DeveGerarTokenEValidade() {
        // Arrange
        when(loginRepository.findByUsernameAndEmail("joaosilva", "joao@email.com"))
            .thenReturn(Optional.of(usuario));
        when(loginRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(emailService).enviarCodigoVerificacao(anyString(), anyString(), anyString());

        // Act
        Map<String, String> response = recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

        // Assert
        assertNotNull(response.get("token"));
        assertNotNull(usuario.getResetToken());
        assertNotNull(usuario.getResetTokenExpiry());
        assertNotNull(usuario.getCodigoVerificacao());
        assertNotNull(usuario.getCodigoVerificacaoExpiry());
        assertFalse(usuario.getTokenUtilizado());
        assertFalse(usuario.isCodigoVerificado());
    }
 
    @Test
    void verificarRespostaSeguranca_ComRespostaCorreta_DeveRetornarTrue() {
        // Arrange
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.verificarRespostaSeguranca("joaosilva", "Maria");

        // Assert
        assertTrue(resultado);
    }

    @Test
    void verificarRespostaSeguranca_ComRespostaIncorreta_DeveRetornarFalse() {
        // Arrange
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.verificarRespostaSeguranca("joaosilva", "Ana");
 
        // Assert
        assertFalse(resultado);
    }   

    @Test 
    void verificarRespostaSeguranca_UsuarioNaoEncontrado_DeveRetornarFalse() {
        // Arrange
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.empty());

        // Act
        boolean resultado = recuperacaoSenhaService.verificarRespostaSeguranca("joaosilva", "Maria");

        // Assert
        assertFalse(resultado);
    }

    @Test
    void redefinirSenhaComToken_ComTokenValido_DeveRedefinirSenha() {
        // Arrange
        when(loginRepository.findByResetToken("token-123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaEncoded");
        when(loginRepository.save(any(Usuario.class))).thenReturn(usuario);

    
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComToken("token-123", "novaSenha123");

        assertTrue(resultado);
        assertTrue(usuario.getTokenUtilizado());
        assertEquals("senhaEncoded", usuario.getPassword());
        verify(loginRepository, times(1)).save(usuario);
    }

    @Test
    void redefinirSenhaComToken_ComTokenInvalido_DeveRetornarFalse() {
        // Arrange
        usuario.setResetTokenExpiry(LocalDateTime.now().minusHours(1));
        when(loginRepository.findByResetToken("token-123")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComToken("token-123", "novaSenha123");

        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComToken_TokenNaoEncontrado_DeveRetornarFalse() {
        // Arrange
        when(loginRepository.findByResetToken("token-invalido")).thenReturn(Optional.empty());

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComToken("token-invalido", "novaSenha123");

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComVerificacao_ComDadosValidos_DeveRedefinirSenha() {
        // Arrange
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaEncoded");
        when(loginRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertTrue(resultado);
        assertEquals("senhaEncoded", usuario.getPassword());
        verify(loginRepository, times(1)).save(usuario);
    }

    @Test
    void redefinirSenhaComVerificacao_ComEmailInvalido_DeveRetornarFalse() {
        // Arrange
        dto.setEmail("emailerrado@email.com");
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComVerificacao_ComNuitInvalido_DeveRetornarFalse() {
        // Arrange
        dto.setNuit("999999999");
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComVerificacao_ComRespostaInvalida_DeveRetornarFalse() {
        // Arrange
        dto.setRespostaSeguranca("RespostaErrada");
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComVerificacao_ComCodigoInvalido_DeveRetornarFalse() {
        // Arrange
        dto.setCodigoVerificacao("999999");
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }
    
    @Test
    void redefinirSenhaComVerificacao_UsuarioNaoEncontrado_DeveRetornarFalse() {
        // Arrange
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.empty());

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void redefinirSenhaComVerificacao_ComTokenExpirado_DeveRetornarFalse() {
        // Arrange
        usuario.setResetTokenExpiry(LocalDateTime.now().minusHours(1));
        when(loginRepository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = recuperacaoSenhaService.redefinirSenhaComVerificacao(dto);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, never()).save(any(Usuario.class));
    }

    @Test
    void solicitarRecuperacaoSenha_DeveSalvarCodigoComExpiracao() {
        // Arrange
        when(loginRepository.findByUsernameAndEmail("joaosilva", "joao@email.com"))
            .thenReturn(Optional.of(usuario));
        when(loginRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(emailService).enviarCodigoVerificacao(anyString(), anyString(), anyString());

        // Act
        recuperacaoSenhaService.solicitarRecuperacaoSenha("joaosilva", "joao@email.com");

        // Assert
        assertNotNull(usuario.getCodigoVerificacao());
        assertEquals(6, usuario.getCodigoVerificacao().length());
        assertNotNull(usuario.getCodigoVerificacaoExpiry());
        assertTrue(usuario.getCodigoVerificacaoExpiry().isAfter(LocalDateTime.now()));
    }
}
