package com.GestaoRotas.GestaoRotas.Controllers;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.GestaoRotas.GestaoRotas.DTO.AutoCadastroDTO;
import com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO;
import com.GestaoRotas.GestaoRotas.auth.Login;
import com.GestaoRotas.GestaoRotas.auth.LoginController;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.LoginService;
import com.GestaoRotas.GestaoRotas.auth.Usuario;

@SpringBootTest 
public class LoginControllerTest {


	    @Autowired
	    private LoginController loginController;

	    @MockitoBean  
	    private LoginService loginService;

	    @MockitoBean
	    private PasswordEncoder passwordEncoder;

	    @MockitoBean
	    private LoginRepository loginRepository;

	    private Usuario usuario;
	    private Login login;
	    private AutoCadastroDTO autoCadastroDTO;
	    private com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO trocarSenhaDTO;
 
	    @BeforeEach
	    void setup() {
	        // Setup Usuario
	        usuario = new Usuario();
	        usuario.setId(1L);
	        usuario.setUsername("joao.silva");
	        usuario.setEmail("joao@email.com");
	        usuario.setPassword("senha123");
	        usuario.setAtivo(true);
	        usuario.setContaBloqueada(false);
	        usuario.setRole(Collections.singletonList("ADMIN").toString());
 
	        // Setup Login
	        login = new Login();
	        login.setUsername("joao.silva");
	        login.setPassword("senha123");

	        // Setup AutoCadastroDTO
	       

	      
	        // Setup trocarSenhaDTO
	        trocarSenhaDTO = new com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO();
	        trocarSenhaDTO.setUsername("joao.silva");
	        trocarSenhaDTO.setSenhaAtual("senha123");
	        trocarSenhaDTO.setNovaSenha("novaSenha123");
	        trocarSenhaDTO.setConfirmarSenha("novaSenha123");

	        // Mocks para LoginService
	        when(loginService.logar(any(Login.class))).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvLnNpbHZhIn0");
	        when(loginService.logar(any(Login.class))).thenThrow(new RuntimeException("Credenciais inválidas"));
	        
	        
	        when(loginService.trocarSenha(any(trocarSenhaDTO.class))).thenReturn("Senha alterada com sucesso");
	        when(loginService.trocarSenha(any(trocarSenhaDTO.class))).thenThrow(new RuntimeException("Senha atual incorreta"));
	        
	        when(loginService.registar(any(Usuario.class))).thenReturn("Usuário cadastrado com sucesso");
	        when(loginService.registar(any(Usuario.class))).thenThrow(new RuntimeException("Usuário já existe"));
	        
	        when(loginService.findAll()).thenReturn(Arrays.asList(usuario));
	       
	        Map<String, String> bloquearResponse = new HashMap<>();
	        bloquearResponse.put("message", "Conta bloqueada com sucesso");
	        when(loginService.bloquearConta(1L)).thenReturn(bloquearResponse);
	        when(loginService.bloquearConta(99L)).thenThrow(new ClassCastException("Erro ao bloquear"));
	        
	        Map<String, String> desativarResponse = new HashMap<>();
	        desativarResponse.put("message", "Conta desativada com sucesso");
	        when(loginService.desativarConta(1L)).thenReturn(desativarResponse);
	        when(loginService.desativarConta(99L)).thenThrow(new ClassCastException("Erro ao desativar"));
	         
	        when(loginService.atualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);
	        when(loginService.atualizarUsuario(eq(99L), any(Usuario.class))).thenThrow(new RuntimeException("Usuário não encontrado"));
	        
	        when(loginService.delete(1L)).thenReturn("Usuário deletado com sucesso");
	        when(loginService.delete(99L)).thenThrow(new RuntimeException("Erro ao deletar"));
	    }

	    // ==================== TESTES DE LOGIN ====================
	    
	    @Test
	    void testLogar_ComCredenciaisValidas_RetornaToken() {
	        when(loginService.logar(any(Login.class))).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvLnNpbHZhIn0");
	        
	        ResponseEntity<?> response = loginController.logar(login);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertTrue(response.getBody() instanceof String);
	        
	        verify(loginService, atLeastOnce()).logar(any(Login.class));
	    }
	    
	    @Test
	    void testLogar_ComCredenciaisInvalidas_RetornaBadRequest() {
	        when(loginService.logar(any(Login.class)))
	            .thenThrow(new RuntimeException("Credenciais inválidas"));
	        
	        ResponseEntity<?> response = loginController.logar(login);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertTrue(response.getBody() instanceof Map);
	        
	        @SuppressWarnings("unchecked")
	        Map<String, String> error = (Map<String, String>) response.getBody();
	        assertTrue(error.containsKey("error"));
	        
	        verify(loginService, atLeastOnce()).logar(any(Login.class));
	    }
	    
	    @Test
	    void testLogar_ComUsuarioBloqueado_RetornaBadRequest() {
	        when(loginService.logar(any(Login.class)))
	            .thenThrow(new RuntimeException("Usuário bloqueado"));
	        
	        ResponseEntity<?> response = loginController.logar(login);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).logar(any(Login.class));
	    }
	    
	    @Test
	    void testLogar_ComUsuarioInativo_RetornaBadRequest() {
	        when(loginService.logar(any(Login.class)))
	            .thenThrow(new RuntimeException("Usuário inativo"));
	        
	        ResponseEntity<?> response = loginController.logar(login);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).logar(any(Login.class));
	    }
 
	  
	    // ==================== TESTES DE TROCAR SENHA ====================
	    
	    @Test
	    void testAlterSenhaNoPrimeiroLogin_ComSucesso_RetornaOk() {
	        when(loginService.trocarSenha(any(trocarSenhaDTO.class))).thenReturn("Senha alterada com sucesso");
	        
	        ResponseEntity<String> response = loginController.alterSenhaNoPrimeiroLogin(trocarSenhaDTO);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Senha alterada com sucesso", response.getBody());
	        
	        verify(loginService, atLeastOnce()).trocarSenha(any(trocarSenhaDTO.class));
	    }
	    
	    @Test
	    void testAlterSenhaNoPrimeiroLogin_ComSenhaAtualIncorreta_RetornaInternalServerError() {
	        when(loginService.trocarSenha(any(trocarSenhaDTO.class)))
	            .thenThrow(new RuntimeException("Senha atual incorreta"));
	        
	        assertThrows(RuntimeException.class, () -> { 
	            loginController.alterSenhaNoPrimeiroLogin(trocarSenhaDTO);
	        });
	    }
	    
	    @Test
	    void testAlterSenhaNoPrimeiroLogin_ComNovasSenhasDiferentes_RetornaInternalServerError() {
	        com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO dtoInvalido = new trocarSenhaDTO();
	        dtoInvalido.setNovaSenha("nova123");
	        dtoInvalido.setConfirmarSenha("nova456");
	        
	        when(loginService.trocarSenha(any(com.GestaoRotas.GestaoRotas.DTO.trocarSenhaDTO.class)))
	            .thenThrow(new RuntimeException("As novas senhas não conferem"));
	         
	        assertThrows(RuntimeException.class, () -> {
	            loginController.alterSenhaNoPrimeiroLogin(dtoInvalido);
	        });
	    }

	    // ==================== TESTES DE CADASTRO DE USUÁRIO ====================
	    
	    @Test
	    void testSave_ComSucesso_RetornaOk() {
	        when(loginService.registar(any(Usuario.class))).thenReturn("Usuário cadastrado com sucesso");
	        
	        ResponseEntity<?> response = loginController.save(usuario);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Usuário cadastrado com sucesso", response.getBody());
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }
	    
	    @Test
	    void testSave_QuandoServiceLancaExcecao_RetornaBadRequest() {
	        when(loginService.registar(any(Usuario.class)))
	            .thenThrow(new RuntimeException("Usuário já existe"));
	        
	        ResponseEntity<?> response = loginController.save(usuario);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertTrue(response.getBody() instanceof Map);
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testSave_ComUsernameDuplicado_RetornaBadRequest() {
	        when(loginService.registar(any(Usuario.class)))
	            .thenThrow(new RuntimeException("Username já existe"));
	        
	        ResponseEntity<?> response = loginController.save(usuario);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }

	    // ==================== TESTES DE FIND ALL ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testFindAll_ComListaNaoVazia_RetornaLista() {
	        when(loginService.findAll()).thenReturn(Arrays.asList(usuario));
	        
	        ResponseEntity<List<Usuario>> response = loginController.findAll();
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals(1, response.getBody().size());
	        assertEquals(1L, response.getBody().get(0).getId());
	        assertEquals("joao.silva", response.getBody().get(0).getUsername());
	        
	        verify(loginService, atLeastOnce()).findAll();
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testFindAll_QuandoServiceLancaExcecao_RetornaBadRequest() {
	        when(loginService.findAll()).thenThrow(new RuntimeException("Erro ao buscar usuários"));
	        
	        ResponseEntity<List<Usuario>> response = loginController.findAll();
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertNull(response.getBody());
	        
	        verify(loginService, atLeastOnce()).findAll();
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testFindAll_ComListaVazia_RetornaListaVazia() {
	        when(loginService.findAll()).thenReturn(Collections.emptyList());
	        
	        ResponseEntity<List<Usuario>> response = loginController.findAll();
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals(0, response.getBody().size());
	        
	        verify(loginService, atLeastOnce()).findAll();
	    }

	    // ==================== TESTES DE BLOQUEIO ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testBloquearConta_ComSucesso_RetornaOk() {
	        Map<String, String> bloquearResponse = new HashMap<>();
	        bloquearResponse.put("message", "Conta bloqueada com sucesso");
	        when(loginService.bloquearConta(1L)).thenReturn(bloquearResponse);
	        
	        ResponseEntity<Map<String, String>> response = loginController.bloquearConta(1L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals("Conta bloqueada com sucesso", response.getBody().get("message"));
	        
	        verify(loginService, atLeastOnce()).bloquearConta(1L);
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})  
	    void testBloquearConta_QuandoServiceLancaClassCastException_RetornaBadRequest() {

	        
	        ResponseEntity<Map<String, String>> response = loginController.bloquearConta(99L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).bloquearConta(99L);
	    }
	    
	    @Test
	    void testBloquearConta_ComIdInexistente_RetornaBadRequest() {
	        when(loginService.bloquearConta(999L)).thenThrow(new RuntimeException("Usuário não encontrado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.bloquearConta(999L);
	        });
	    }

	    // ==================== TESTES DE ATIVAR/DESATIVAR ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})  
	    void testDesativarConta_ComSucesso_RetornaOk() {
	        Map<String, String> desativarResponse = new HashMap<>();
	        desativarResponse.put("message", "Conta desativada com sucesso");
	        when(loginService.desativarConta(1L)).thenReturn(desativarResponse);
	        
	        ResponseEntity<Map<String, String>> response = loginController.desativarConta(1L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals("Conta desativada com sucesso", response.getBody().get("message"));
	        
	        verify(loginService, atLeastOnce()).desativarConta(1L);
	    }
	    
	    @Test 
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testDesativarConta_QuandoServiceLancaClassCastException_RetornaBadRequest() {
	         
	        ResponseEntity<Map<String, String>> response = loginController.desativarConta(99L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals("erro ao tentar fazer altercoes", response.getBody().get("erro"));
	        
	        verify(loginService, atLeastOnce()).desativarConta(99L);
	    }
	    
	    @Test
	    void testDesativarConta_ComIdInexistente_RetornaBadRequest() {
	        when(loginService.desativarConta(999L)).thenThrow(new RuntimeException("Usuário não encontrado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.desativarConta(999L);
	        });
	    }

	    // ==================== TESTES DE ATUALIZAR USUÁRIO ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testAtualizarUsuario_ComSucesso_RetornaUsuarioAtualizado() {
	        when(loginService.atualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);
	        
	        ResponseEntity<Usuario> response = loginController.atualizarUsuario(1L, usuario);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertNotNull(response.getBody());
	        assertEquals(1L, response.getBody().getId());
	        assertEquals("joao.silva", response.getBody().getUsername());
	        
	        verify(loginService, atLeastOnce()).atualizarUsuario(eq(1L), any(Usuario.class));
	    }
	    
	    @Test
	    void testAtualizarUsuario_ComIdInexistente_RetornaInternalServerError() {
	        when(loginService.atualizarUsuario(eq(99L), any(Usuario.class)))
	            .thenThrow(new RuntimeException("Usuário não encontrado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.atualizarUsuario(99L, usuario);
	        });
	    }
	    
	    @Test
	    void testAtualizarUsuario_ComDadosInvalidos_RetornaInternalServerError() {
	        Usuario usuarioInvalido = new Usuario();
	        usuarioInvalido.setEmail("email-invalido");
	        
	        when(loginService.atualizarUsuario(eq(1L), any(Usuario.class)))
	            .thenThrow(new RuntimeException("Dados inválidos"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.atualizarUsuario(1L, usuarioInvalido);
	        });
	    }

	    // ==================== TESTES DE DELETE ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testDelete_ComSucesso_RetornaOk() {
	        when(loginService.delete(1L)).thenReturn("Usuário deletado com sucesso");
	        
	        ResponseEntity<String> response = loginController.delete(1L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals("Usuário deletado com sucesso", response.getBody());
	        
	        verify(loginService, atLeastOnce()).delete(1L);
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testDelete_QuandoServiceLancaExcecao_RetornaBadRequest() {
	        
	        ResponseEntity<String> response = loginController.delete(99L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertEquals("erro ao deletar usuario", response.getBody());
	        
	        verify(loginService, atLeastOnce()).delete(99L);
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testDelete_ComIdInexistente_RetornaBadRequest() {
	        when(loginService.delete(999L)).thenThrow(new RuntimeException("Usuário não encontrado"));
	        
	        ResponseEntity<String> response = loginController.delete(999L);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        assertEquals("erro ao deletar usuario", response.getBody());
	        
	        verify(loginService, atLeastOnce()).delete(999L);
	    }

	    // ==================== TESTES DE VALIDAÇÃO ====================
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testSave_ComUsuarioSemUsername_RetornaBadRequest() {
	        Usuario usuarioInvalido = new Usuario();
	        usuarioInvalido.setEmail("teste@email.com");
	        usuarioInvalido.setPassword("senha123");
	        
	        when(loginService.registar(any(Usuario.class)))
	            .thenThrow(new RuntimeException("Username é obrigatório"));
	        
	        ResponseEntity<?> response = loginController.save(usuarioInvalido);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testSave_ComUsuarioSemEmail_RetornaBadRequest() {
	        Usuario usuarioInvalido = new Usuario();
	        usuarioInvalido.setUsername("joao.silva");
	        usuarioInvalido.setPassword("senha123");
	        
	        when(loginService.registar(any(Usuario.class)))
	            .thenThrow(new RuntimeException("Email é obrigatório"));
	        
	        ResponseEntity<?> response = loginController.save(usuarioInvalido);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"ADMIN"})
	    void testSave_ComUsuarioSemSenha_RetornaBadRequest() {
	        Usuario usuarioInvalido = new Usuario();
	        usuarioInvalido.setUsername("joao.silva");
	        usuarioInvalido.setEmail("joao@email.com");
	        
	        when(loginService.registar(any(Usuario.class)))
	            .thenThrow(new RuntimeException("Senha é obrigatória"));
	        
	        ResponseEntity<?> response = loginController.save(usuarioInvalido);
	        
	        assertNotNull(response);
	        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	        
	        verify(loginService, atLeastOnce()).registar(any(Usuario.class));
	    }

	    // ==================== TESTES DE AUTORIZAÇÃO ====================
	    
	    @Test
	    void testFindAll_QuandoUsuarioNaoTemPermissao_RetornaErro() {
	        when(loginService.findAll()).thenThrow(new RuntimeException("Acesso negado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.findAll();
	        });
	    }
	     
	    @Test
	    @WithMockUser(authorities = {"ADMIN"}) 
	    void testBloquearConta_QuandoUsuarioNaoTemPermissao_RetornaErro() {
	        when(loginService.bloquearConta(1L)).thenThrow(new RuntimeException("Acesso negado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.bloquearConta(1L);
	        });
	    }
	    
	    @Test
	    @WithMockUser(authorities = {"USER"}) 
	    void testDelete_QuandoUsuarioNaoTemPermissao_RetornaErro() {
	        when(loginService.delete(1L)).thenThrow(new RuntimeException("Acesso negado"));
	        
	        assertThrows(RuntimeException.class, () -> {
	            loginController.delete(1L);
	        });
	    }
	
}
