package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import java.time.*;

import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
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

@Mock
private EmailService emailService;

@InjectMocks
private LoginService loginService;

private Usuario usuario;
private Login login;
private com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO trocarSenhaDTO;
private AutoCadastroDTO autoCadastroDTO;

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
    usuario.setPrimeiroLogin(false);
    usuario.setNuit("123456789");
    usuario.setTelefone("11999999999");
    usuario.setPerguntaSeguranca("Qual sua cor favorita?");
    usuario.setRespostaSeguranca("Azul");

    login = new Login();
    login.setUsername("joaosilva");
    login.setPassword("senha123");

    trocarSenhaDTO = new com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO();
    trocarSenhaDTO.setUsername("joaosilva");
    trocarSenhaDTO.setSenhaAtual("senha123");
    trocarSenhaDTO.setNovaSenha("novaSenha123");
    trocarSenhaDTO.setConfirmarSenha("novaSenha123");

    autoCadastroDTO = new AutoCadastroDTO();
    autoCadastroDTO.setUsername("maria123");
    autoCadastroDTO.setEmail("maria@email.com");
    autoCadastroDTO.setNuit("987654321");
    autoCadastroDTO.setPassword("senhaMaria");
    autoCadastroDTO.setPerguntaSeguranca("Qual sua comida favorita?");
    autoCadastroDTO.setRespostaSeguranca("Pizza");
    autoCadastroDTO.setTelefone("11888888888");
    autoCadastroDTO.setDataNascimento(LocalDate.of(1995, 5, 15));
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
void logar_ComPrimeiroLogin_DeveLancarExcecao() {
    // Arrange
    usuario.setPrimeiroLogin(true);
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        loginService.logar(login);
    });
    assertEquals("PRIMEIRO_LOGIN!", exception.getMessage());
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
}

@Test
void trocarSenha_ComDadosValidos_DeveAlterarSenha() {
    // Arrange
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("senha123", "senhaEncoded")).thenReturn(true);
    when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncoded");
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

    // Act
    String resultado = loginService.trocarSenha(trocarSenhaDTO);

    // Assert
    assertEquals("Senha alterada com sucesso", resultado);
    assertEquals("novaSenhaEncoded", usuario.getPassword());
    assertFalse(usuario.getPrimeiroLogin());
    verify(repository, times(1)).save(usuario);
} 

@Test
void trocarSenha_ComSenhaAtualInvalida_DeveLancarExcecao() {
    // Arrange
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("senha123", "senhaEncoded")).thenReturn(false);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        loginService.trocarSenha(trocarSenhaDTO);
    });
    assertEquals("Senha atual inválida", exception.getMessage());
    verify(repository, never()).save(any(Usuario.class));
}

@Test
void trocarSenha_ComSenhasNaoConincidentes_DeveLancarExcecao() {
    // Arrange
    trocarSenhaDTO.setConfirmarSenha("senhaDiferente");
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("senha123", "senhaEncoded")).thenReturn(true);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        loginService.trocarSenha(trocarSenhaDTO);
    });
    assertEquals("Senhas não coincidem", exception.getMessage());
}

@Test
void trocarSenha_ComNovaSenhaMuitoCurta_DeveLancarExcecao() {
    // Arrange
    trocarSenhaDTO.setNovaSenha("123");
    trocarSenhaDTO.setConfirmarSenha("123");
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("senha123", "senhaEncoded")).thenReturn(true);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        loginService.trocarSenha(trocarSenhaDTO);
    });
    assertEquals("Senha deve ter no mínimo 6 caracteres", exception.getMessage());
}

@Test
void registrar_ComDadosValidos_DeveSalvarUsuario() {
    // Arrange
    when(passwordEncoder.encode("0000")).thenReturn("senhaEncoded");
    when(repository.save(any(Usuario.class))).thenReturn(usuario);
    doNothing().when(emailService).enviarBoasVindasAoUsuario(anyString(), anyString());

    // Act
    String resultado = loginService.registar(usuario);

    // Assert         
    assertEquals("Usuário salvo com sucesso!", resultado);
    assertFalse(usuario.getPrimeiroLogin());
    assertEquals("senhaEncoded", usuario.getPassword());
    assertEquals(0, usuario.getTentativasLogin());
    assertFalse(usuario.getContaBloqueada());
    verify(emailService, times(1)).enviarBoasVindasAoUsuario(anyString(), anyString());
}

@Test
void autoCadastro_ComDadosValidos_DeveCriarUsuario() {
    // Arrange
    when(repository.existsByUsername("maria123")).thenReturn(false);
    when(repository.existsByEmail("maria@email.com")).thenReturn(false);
    when(repository.existsByNuit("987654321")).thenReturn(false);
    when(passwordEncoder.encode("senhaMaria")).thenReturn("senhaEncoded");
    when(repository.save(any(Usuario.class))).thenReturn(new Usuario());
    doNothing().when(emailService).enviarBoasVindasAoUsuario(anyString(), anyString());

    // Act
    ResponseEntity<?> response = loginService.autoCadastro(autoCadastroDTO);

    // Assert
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Cadastro realizado com sucesso. Aguarde ativação da conta por um administrador.", response.getBody());
    verify(repository, times(1)).save(any(Usuario.class));
}

@Test
void autoCadastro_ComUsernameDuplicado_DeveRetornarErro() {
    // Arrange
    when(repository.existsByUsername("maria123")).thenReturn(true);

    // Act
    ResponseEntity<?> response = loginService.autoCadastro(autoCadastroDTO);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Username já está em uso", response.getBody());
    verify(repository, never()).save(any(Usuario.class));
}

@Test
void autoCadastro_ComEmailDuplicado_DeveRetornarErro() {
    // Arrange
    when(repository.existsByUsername("maria123")).thenReturn(false);
    when(repository.existsByEmail("maria@email.com")).thenReturn(true);

    // Act
    ResponseEntity<?> response = loginService.autoCadastro(autoCadastroDTO);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Email já está em uso", response.getBody());
}

@Test
void autoCadastro_ComNuitDuplicado_DeveRetornarErro() {
    // Arrange
    when(repository.existsByUsername("maria123")).thenReturn(false);
    when(repository.existsByEmail("maria@email.com")).thenReturn(false);
    when(repository.existsByNuit("987654321")).thenReturn(true);

    // Act
    ResponseEntity<?> response = loginService.autoCadastro(autoCadastroDTO);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("NUIT já está em uso", response.getBody());
}

@Test
void atualizarUsuario_ComIdValido_DeveAtualizarCampos() {
    // Arrange
    Usuario usuarioAtualizado = new Usuario();
    usuarioAtualizado.setUsername("joaosilva_novo");
    usuarioAtualizado.setEmail("joao_novo@email.com");
    usuarioAtualizado.setRole("ADMIN");
    usuarioAtualizado.setTelefone("11988888888");
    usuarioAtualizado.setNuit("111222333");
    usuarioAtualizado.setAtivo(false);
    usuarioAtualizado.setDataNascimento(LocalDate.of(1990, 1, 1));
    usuarioAtualizado.setContaBloqueada(true);

    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

    // Act
    Usuario resultado = loginService.atualizarUsuario(1L, usuarioAtualizado);


    assertNotNull(resultado);
    assertEquals("joaosilva_novo", usuario.getUsername());
    assertEquals("joao_novo@email.com", usuario.getEmail());
    assertEquals("ADMIN", usuario.getRole());
    assertEquals("11988888888", usuario.getTelefone());
    assertEquals(0, usuario.getTentativasLogin());
    assertFalse(usuario.getContaBloqueada());
}

@Test
void bloquearConta_QuandoContaAtiva_DeveBloquear() {
    // Arrange
    usuario.setContaBloqueada(false);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

  
    Map<String, String> response = loginService.bloquearConta(1L);

   
    assertEquals("conta bloqueada com suceso", response.get("sucesso"));
    assertTrue(usuario.getContaBloqueada());
}

@Test
void bloquearConta_QuandoContaBloqueada_DeveDesbloquear() {
    // Arrange
    usuario.setContaBloqueada(true);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

    Map<String, String> response = loginService.bloquearConta(1L);

    assertEquals("conta desbloqueada com sucesso", response.get("sucesso"));
    assertFalse(usuario.getContaBloqueada());
}

@Test
void desativarConta_QuandoContaAtiva_DeveDesativar() {
    // Arrange
    usuario.setAtivo(true);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

    Map<String, String> response = loginService.desativarConta(1L);

    assertEquals("conta desativada com sucesso", response.get("sucesso"));
    assertFalse(usuario.getAtivo());
}

@Test
void desativarConta_QuandoContaDesativada_DeveAtivar() {
    usuario.setAtivo(false);
    when(repository.findById(1L)).thenReturn(Optional.of(usuario));
    when(repository.save(any(Usuario.class))).thenReturn(usuario);

    Map<String, String> response = loginService.desativarConta(1L);

    // Assert
    assertEquals("conta ativada com sucesso", response.get("sucesso"));
    assertTrue(usuario.getAtivo());
}

@Test
void findAll_DeveRetornarListaDeUsuarios() {
    // Arrange
    List<Usuario> usuarios = Arrays.asList(usuario, new Usuario());
    when(repository.findAll()).thenReturn(usuarios);

    List<Usuario> resultado = loginService.findAll();


    assertEquals(2, resultado.size());
}

@Test
void delete_DeveDeletarUsuario() {

    doNothing().when(repository).deleteById(1L);

    String resultado = loginService.delete(1L);

 
    assertEquals("Usuario deletado com sucesso", resultado);
    verify(repository, times(1)).deleteById(1L);
}

@Test
void gerarToken_ComCredenciaisValidas_DeveRetornarToken() {

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(mock(Authentication.class));
    when(repository.findByUsername("joaosilva")).thenReturn(Optional.of(usuario));
    when(jwtService.generateToken(usuario)).thenReturn("jwt-token-123");
  
    String token = loginService.gerarToken(login);

    assertEquals("jwt-token-123", token);
}
} 