package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import java.time.*;

import com.GestaoRotas.GestaoRotas.DTO.UserSaveDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.auth.Login;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.LoginService;
import com.GestaoRotas.GestaoRotas.auth.Usuario;
import com.GestaoRotas.GestaoRotas.config.JwtServiceGenerator;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoginServiceUnitTest {

    @Mock
    private LoginRepository repository;

    @Mock
    private JwtServiceGenerator jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private Usuario usuario;
    private Login login;
    @Mock
    private EmailService emailService; 

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joaosilva");
        usuario.setPassword("senhaEncoded");
        usuario.setEmail("joao@email.com");
        usuario.setRole("USER"); 
        usuario.setAtivo(true);
        usuario.setContaBloqueada(false);
        usuario.setTentativasLogin(0);
        usuario.setUltimoAcesso(null);

        login = new Login();
        login.setUsername("joaosilva");
        login.setPassword("senha123");
    }

    @Test
    void logar_ComCredenciaisValidas_DeveRetornarToken() {
        // Arrange
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("jwt-token-123");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        String token = loginService.logar(login);

        // Assert
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        assertEquals(0, usuario.getTentativasLogin());
        assertNotNull(usuario.getUltimoAcesso());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void logar_ComUsuarioNaoEncontrado_DeveLancarExcecao() {
        // Arrange
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void logar_ComUsuarioInativo_DeveLancarExcecao() {
        // Arrange
        usuario.setAtivo(false);
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("conta desativada solicite um administrador", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void logar_ComContaBloqueada_DeveLancarExcecao() {
        // Arrange
        usuario.setContaBloqueada(true);
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("Conta bloqueada", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void logar_ComCincoTentativas_DeveLancarExcecao() {
        // Arrange
        usuario.setTentativasLogin(5);
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("Conta bloqueada devido a múltiplas tentativas de login", exception.getMessage());
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    void logar_ComCredenciaisInvalidas_DeveIncrementarTentativas() {
        // Arrange
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Credenciais inválidas"));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("Credenciais inválidas", exception.getMessage());
        assertEquals(1, usuario.getTentativasLogin());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void registrar_ComDadosValidos_DeveSalvarUsuario() {
    	when(passwordEncoder.encode("0000")).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        String resultado = loginService.registar(usuario);

        // Assert 
        assertEquals("Usuário salvo com sucesso!", resultado);
        assertEquals("senhaEncoded", usuario.getPassword());
        verify(repository).save(any(Usuario.class)); 
    }

    @Test
    void atualizarUsuario_ComIdValido_DeveAtualizarCamposPermitidos() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("joaosilva_atualizado");
        usuarioAtualizado.setEmail("joao_novo@email.com");
        usuarioAtualizado.setRole("ADMIN");
        usuarioAtualizado.setTelefone("11988888888");
        usuarioAtualizado.setNuit("987654321");
        usuarioAtualizado.setAtivo(false);
        usuarioAtualizado.setContaBloqueada(true);

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Usuario resultado = loginService.atualizarUsuario(1L, usuarioAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("joaosilva_atualizado", usuario.getUsername());
        assertEquals("joao_novo@email.com", usuario.getEmail());
        assertEquals("ADMIN", usuario.getRole());
        assertEquals("11988888888", usuario.getTelefone());
        assertEquals("987654321", usuario.getNuit());
        assertFalse(usuario.getAtivo());
        assertTrue(usuario.getContaBloqueada());
        
        // Verificar que campos sensíveis NÃO foram atualizados
        assertNotEquals("senha123", usuario.getPassword());
    }

    @Test
    void atualizarUsuario_ComIdInvalido_DeveLancarExcecao() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            loginService.atualizarUsuario(999L, new Usuario());
        });
        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    @Test
    void bloquearConta_QuandoContaAtiva_DeveBloquear() {
        // Arrange
        usuario.setContaBloqueada(false);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Map<String, String> response = loginService.bloquearConta(1L);

        // Assert
        assertNotNull(response);
        assertEquals("conta bloqueada com suceso", response.get("sucesso"));
        assertTrue(usuario.getContaBloqueada());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void bloquearConta_QuandoContaBloqueada_DeveDesbloquear() {
        // Arrange
        usuario.setContaBloqueada(true);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Map<String, String> response = loginService.bloquearConta(1L);

        // Assert
        assertNotNull(response);
        assertEquals("conta desbloqueada com sucesso", response.get("sucesso"));
        assertFalse(usuario.getContaBloqueada());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void bloquearConta_ComIdInvalido_DeveLancarExcecao() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.bloquearConta(999L);
        });
        assertEquals(" usuario nao econtrado", exception.getMessage());
    }

    @Test
    void desativarConta_QuandoContaAtiva_DeveDesativar() {
        // Arrange
        usuario.setAtivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Map<String, String> response = loginService.desativarConta(1L);

        // Assert
        assertNotNull(response);
        assertEquals("conta desativada com sucesso", response.get("sucesso"));
        assertFalse(usuario.getAtivo());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void desativarConta_QuandoContaDesativada_DeveAtivar() {
        // Arrange
        usuario.setAtivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        Map<String, String> response = loginService.desativarConta(1L);

        // Assert
        assertNotNull(response);
        assertEquals("conta ativada com sucesso", response.get("sucesso"));
        assertTrue(usuario.getAtivo());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    void findAll_DeveRetornarListaDeUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario, new Usuario());
        when(repository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = loginService.findAll();

        // Assert
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void delete_DeveDeletarUsuario() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        String resultado = loginService.delete(1L);

        // Assert
        assertEquals("Usuario deletado com sucesso", resultado);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void gerarToken_ComCredenciaisValidas_DeveRetornarToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mock(Authentication.class));
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token-123");

        // Act
        String token = loginService.gerarToken(login);

        // Assert
        assertNotNull(token);
        assertEquals("jwt-token-123", token);
    }

    @Test
    void gerarToken_ComUsuarioInativo_DeveLancarDisabledException() {
        // Arrange
        usuario.setAtivo(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mock(Authentication.class));
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(DisabledException.class, () -> {
            loginService.gerarToken(login);
        });
    }

    @Test
    void gerarToken_ComContaBloqueada_DeveLancarDisabledException() {
        // Arrange
        usuario.setContaBloqueada(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mock(Authentication.class));
        when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(DisabledException.class, () -> {
            loginService.gerarToken(login);
        });
    }
}
