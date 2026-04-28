package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;
import java.time.*;

import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO;
import com.GestaoRotas.GestaoRotas.auth.Login;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.LoginService;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@Transactional 
@SpringBootTest 
public class LoginServiceIntegratioTest {
	

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Login login;

    @BeforeEach
    void setUp() {
        // Criar usuário real no banco
        usuario = new Usuario();
        usuario.setUsername("joaosilva");
        usuario.setEmail("joao@email.com");
        usuario.setNuit("1234567894");
        usuario.setPassword(passwordEncoder.encode("senha123"));
        usuario.setRole("USER");
        usuario.setAtivo(true);
        usuario.setContaBloqueada(false);
        usuario.setTentativasLogin(0);
        usuario.setPrimeiroLogin(false);
        usuario.setTelefone("11999999999");
        usuario.setPerguntaSeguranca("Qual sua cor favorita?");
        usuario.setRespostaSeguranca("Azul");
        usuario.setDataCriacao(LocalDateTime.now());
        usuario = loginRepository.save(usuario);

        // Setup Login
        login = new Login();
        login.setUsername("joaosilva");
        login.setPassword("senha123");
    }

    @Test
    void logar_ComCredenciaisValidas_DeveRetornarToken() {
        // Act
        String token = loginService.logar(login);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertTrue(usuarioAtualizado.isPresent());
        assertEquals(0, usuarioAtualizado.get().getTentativasLogin());
        assertNotNull(usuarioAtualizado.get().getUltimoAcesso());
    }

    @Test
    void logar_ComPrimeiroLogin_DeveLancarExcecao() {
        // Arrange
        usuario.setPrimeiroLogin(true);
        loginRepository.save(usuario);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        assertEquals("PRIMEIRO_LOGIN!", exception.getMessage());
    }

    @Test
    void logar_ComSenhaIncorreta_DeveIncrementarTentativas() {
        // Arrange
        login.setPassword("senhaErrada");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertTrue(usuarioAtualizado.isPresent());
        assertEquals(1, usuarioAtualizado.get().getTentativasLogin());
    }

    @Test
    void trocarSenha_ComDadosValidos_DeveAlterarSenha() {
        // Arrange
        trocarSenhaDTO dto = new trocarSenhaDTO();
        dto.setUsername("joaosilva");
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("novaSenha123");
        dto.setConfirmarSenha("novaSenha123");

        // Act
        String resultado = loginService.trocarSenha(dto);

        // Assert
        assertEquals("Senha alterada com sucesso", resultado);
        
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertTrue(passwordEncoder.matches("novaSenha123", usuarioAtualizado.get().getPassword()));
        assertFalse(usuarioAtualizado.get().getPrimeiroLogin());
    }

    @Test
    void trocarSenha_QuandoPrimeiroLogin_DeveAlterarEFlag() {
        // Arrange
        usuario.setPrimeiroLogin(true);
        loginRepository.save(usuario);
        
        trocarSenhaDTO dto = new trocarSenhaDTO();
        dto.setUsername("joaosilva");
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("novaSenha123");
        dto.setConfirmarSenha("novaSenha123");

        // Act
        String resultado = loginService.trocarSenha(dto);

        // Assert
        assertEquals("Senha alterada com sucesso", resultado);
        
        Optional<Usuario> usuarioAtualizado = loginRepository.findByUsername("joaosilva");
        assertFalse(usuarioAtualizado.get().getPrimeiroLogin());
    }

    @Test
    void registrar_DeveCriarUsuarioComSenhaTemporaria() {
        // Arrange
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername("maria123");
        novoUsuario.setEmail("maria@email.com");
        novoUsuario.setNuit("987654321");
        novoUsuario.setTelefone("11977777777");
        novoUsuario.setRole("USER");
        novoUsuario.setPerguntaSeguranca("Qual sua comida favorita?");
        novoUsuario.setRespostaSeguranca("Pizza");

        // Act
        String resultado = loginService.registar(novoUsuario);

        // Assert
        assertEquals("Usuário salvo com sucesso!", resultado);
        
        Optional<Usuario> usuarioSalvo = loginRepository.findByUsername("maria123");
        assertTrue(usuarioSalvo.isPresent());
        assertTrue(usuarioSalvo.get().getPrimeiroLogin());
        assertTrue(passwordEncoder.matches("0000", usuarioSalvo.get().getPassword()));
    }

    @Test
    void autoCadastro_ComDadosValidos_DeveCriarUsuarioInativo() {
        // Arrange
        AutoCadastroDTO dto = new AutoCadastroDTO();
        dto.setUsername("pedro123");
        dto.setEmail("pedro@email.com");
        dto.setNuit("555555555");
        dto.setPassword("senhaPedro");
        dto.setPerguntaSeguranca("Qual seu animal favorito?");
        dto.setRespostaSeguranca("Cachorro");
        dto.setTelefone("11966666666");
        dto.setDataNascimento(LocalDate.of(1992, 3, 10));

        // Act
        ResponseEntity<?> response = loginService.autoCadastro(dto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        
        Optional<Usuario> usuarioSalvo = loginRepository.findByUsername("pedro123");
        assertTrue(usuarioSalvo.isPresent());
        assertFalse(usuarioSalvo.get().getAtivo()); // Conta inativa até admin ativar
        assertEquals("USER", usuarioSalvo.get().getRole());
    }

    @Test
    void atualizarUsuario_DeveAtualizarDados() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setUsername("joaosilva_atualizado");
        usuarioAtualizado.setEmail("joao_novo@email.com");
        usuarioAtualizado.setRole("ADMIN");
        usuarioAtualizado.setTelefone("11988888888");
        usuarioAtualizado.setNuit("111222333");
        usuarioAtualizado.setDataNascimento(LocalDate.of(1988, 5, 20));

        // Act
        Usuario resultado = loginService.atualizarUsuario(usuario.getId(), usuarioAtualizado);

        // Assert
        assertEquals("joaosilva_atualizado", resultado.getUsername());
        assertEquals("joao_novo@email.com", resultado.getEmail());
        assertEquals("ADMIN", resultado.getRole());
        assertEquals("11988888888", resultado.getTelefone());
        assertEquals("111222333", resultado.getNuit());
        
        // Verificar que senha não foi alterada
        assertTrue(passwordEncoder.matches("senha123", resultado.getPassword()));
    }

    @Test
    void bloquearConta_DeveAlternarEstado() {
        // Act - Bloquear
        Map<String, String> responseBloquear = loginService.bloquearConta(usuario.getId());

        // Assert
        assertEquals("conta bloqueada com suceso", responseBloquear.get("sucesso"));
        
        Optional<Usuario> usuarioBloqueado = loginRepository.findById(usuario.getId());
        assertTrue(usuarioBloqueado.get().getContaBloqueada());
        
        // Act - Desbloquear
        Map<String, String> responseDesbloquear = loginService.bloquearConta(usuario.getId());
        
        // Assert
        assertEquals("conta desbloqueada com sucesso", responseDesbloquear.get("sucesso"));
        
        Optional<Usuario> usuarioDesbloqueado = loginRepository.findById(usuario.getId());
        assertFalse(usuarioDesbloqueado.get().getContaBloqueada());
    }

    @Test
    void desativarConta_DeveAlternarEstado() {
        // Act - Desativar
        Map<String, String> responseDesativar = loginService.desativarConta(usuario.getId());

        // Assert
        assertEquals("conta desativada com sucesso", responseDesativar.get("sucesso"));
        
        Optional<Usuario> usuarioDesativado = loginRepository.findById(usuario.getId());
        assertFalse(usuarioDesativado.get().getAtivo());
        
        // Act - Ativar
        Map<String, String> responseAtivar = loginService.desativarConta(usuario.getId());
        
        // Assert
        assertEquals("conta ativada com sucesso", responseAtivar.get("sucesso"));
        
        Optional<Usuario> usuarioAtivado = loginRepository.findById(usuario.getId());
        assertTrue(usuarioAtivado.get().getAtivo());
    }

    @Test
    void findAll_DeveRetornarTodosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setUsername("maria123");
        usuario2.setEmail("maria@email.com");
        usuario2.setNuit("987654321");
        usuario2.setPassword(passwordEncoder.encode("senhaMaria"));
        usuario2.setRole("USER");
        usuario2.setAtivo(true);
        loginRepository.save(usuario2);

        // Act
        List<Usuario> usuarios = loginService.findAll();

        // Assert
        assertTrue(usuarios.size() >= 2);
        assertTrue(usuarios.stream().anyMatch(u -> u.getUsername().equals("joaosilva")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getUsername().equals("maria123")));
    }

    @Test
    void delete_DeveRemoverUsuario() {
        // Assert
        assertTrue(loginRepository.findById(usuario.getId()).isPresent());

        // Act
        String resultado = loginService.delete(usuario.getId());

        // Assert
        assertEquals("Usuario deletado com sucesso", resultado);
        assertFalse(loginRepository.findById(usuario.getId()).isPresent());
    }

    @Test
    void logar_AposDesbloquear_DeveFuncionar() {
        // Arrange
        loginService.bloquearConta(usuario.getId());
        
        // Tentar logar - deve falhar
        assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        
        // Desbloquear
        loginService.bloquearConta(usuario.getId());

        // Act
        String token = loginService.logar(login);

        // Assert
        assertNotNull(token);
    }

    @Test
    void logar_AposAtivar_DeveFuncionar() {
        // Arrange
        loginService.desativarConta(usuario.getId());
        
        // Tentar logar - deve falhar
        assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        
        // Ativar
        loginService.desativarConta(usuario.getId());

        // Act
        String token = loginService.logar(login);

        // Assert
        assertNotNull(token);
    }

    @Test
    void autoCadastro_ComUsernameExistente_DeveRetornarErro() {
        
        Usuario existente = new Usuario();
        existente.setUsername("pedro123");
        existente.setEmail("pedro@email.com");
        existente.setNuit("555555555");
        existente.setPassword(passwordEncoder.encode("123"));
        existente.setRole("USER");
        existente.setAtivo(true);
           
        loginRepository.save(existente);
        AutoCadastroDTO dto = new AutoCadastroDTO();
        dto.setUsername("pedro123"); // mesmo username
        dto.setEmail("outro@email.com");
        dto.setNuit("999999999");
        dto.setPassword("senhaPedro");
        dto.setPerguntaSeguranca("Qual seu animal favorito?");
        dto.setRespostaSeguranca("Cachorro");
        dto.setTelefone("11966666666");
        dto.setDataNascimento(LocalDate.of(1992, 3, 10));

        ResponseEntity<?> response = loginService.autoCadastro(dto);

        
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Username já está em uso"));
    }

    @Test 
    void logar_AposMultiplasTentativas_DeveBloquearConta() {
        // Arrange
        login.setPassword("senhaErrada");
        
        // 5 tentativas falhas
        for (int i = 0; i < 5; i++) {
            try {
                loginService.logar(login);
            } catch (RuntimeException e) {
                // Esperado
            }
        }
        
        // Tentativa 6 - deve dizer que conta está bloqueada por tentativas
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.logar(login);
        });
        
        assertTrue(exception.getMessage().contains("Conta bloqueada devido a múltiplas tentativas"));
    }

    @Test
    void trocarSenha_ComUsuarioNaoEncontrado_DeveLancarExcecao() {
        // Arrange
        trocarSenhaDTO dto = new trocarSenhaDTO();
        dto.setUsername("usuarioInexistente");
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("novaSenha123");
        dto.setConfirmarSenha("novaSenha123");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loginService.trocarSenha(dto);
        });
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

}
