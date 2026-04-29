package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.GestaoRotas.GestaoRotas.auth.LoginRepository;
import com.GestaoRotas.GestaoRotas.auth.Usuario;
import com.GestaoRotas.GestaoRotas.config.JwtServiceGenerator;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import java.util.*;
import java.time.*;
import io.jsonwebtoken.Claims;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class jwtServiceGenerateIntegrationTest {
	 

    @Autowired
    private JwtServiceGenerator jwtServiceGenerator;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Criar usuário real no banco
        usuario = new Usuario();
        usuario.setUsername("joaosilva");
        usuario.setEmail("joao@email.com");
        usuario.setNuit("123456701");
        usuario.setPassword(passwordEncoder.encode("senha123"));
        usuario.setRole("ADMIN");
        usuario.setAtivo(true);
        usuario.setContaBloqueada(false);
        usuario = loginRepository.save(usuario);

        userDetails = User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities("ROLE_" + usuario.getRole())
                .build();
    }

    @Test
    void generateToken_DeveGerarTokenViavel() {
        // Act
        String token = jwtServiceGenerator.generateToken(usuario);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Verificar se o token é válido
        String extractedUsername = jwtServiceGenerator.extractUsername(token);
        assertEquals(usuario.getUsername(), extractedUsername);
        
        // Verificar se o token não expirou
        boolean isValid = jwtServiceGenerator.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void generateToken_ComUsuarioDoBanco_DeveConterDadosCorretos() {
        // Act
        String token = jwtServiceGenerator.generateToken(usuario);
        String username = jwtServiceGenerator.extractUsername(token);

        // Assert
        assertEquals(usuario.getUsername(), username);
    }

    @Test
    void isTokenValid_ComTokenGerado_DeveSerValido() {
        // Arrange
        String token = jwtServiceGenerator.generateToken(usuario);

        // Act
        boolean isValid = jwtServiceGenerator.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ComTokenDeOutroUsuario_DeveSerInvalido() {
        // Arrange
        String token = jwtServiceGenerator.generateToken(usuario);
        
        UserDetails outroUser = User.builder()
                .username("outrousuario")
                .password("senha")
                .authorities("ROLE_USER")
                .build();

        // Act
        boolean isValid = jwtServiceGenerator.isTokenValid(token, outroUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractUsername_DeveExtrairUsernameCorreto() {
        // Arrange
        String token = jwtServiceGenerator.generateToken(usuario);

        // Act
        String username = jwtServiceGenerator.extractUsername(token);

        // Assert
        assertEquals(usuario.getUsername(), username);
    }

    @Test
    void extractClaim_DeveExtrairClaimsEspecificos() {
        // Arrange
        String token = jwtServiceGenerator.generateToken(usuario);

        // Act
        String subject = jwtServiceGenerator.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtServiceGenerator.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtServiceGenerator.extractClaim(token, Claims::getExpiration);

        // Assert
        assertEquals(usuario.getUsername(), subject);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
    }

    @Test
    void generateToken_MultiplasVezes_DeveGerarTokensDiferentes() {
        // Act
        String token1 = jwtServiceGenerator.generateToken(usuario);
        String token2 = jwtServiceGenerator.generateToken(usuario);

        // Assert
        assertNotEquals(token1, token2);
        
        // Ambos devem ser válidos
        assertTrue(jwtServiceGenerator.isTokenValid(token1, userDetails));
        assertTrue(jwtServiceGenerator.isTokenValid(token2, userDetails));
    }

    @Test
    void extractExpiration_DeveCalcularExpiracaoCorreta() {
        // Arrange
        Date antes = new Date();
        
        // Act
        String token = jwtServiceGenerator.generateToken(usuario);
        Date expiration = invokePrivateExtractExpiration(token);
        Date depois = new Date();

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(antes));
        
        // Deve expirar em aproximadamente 2 horas
        long diffEmHoras = (expiration.getTime() - antes.getTime()) / (1000 * 60 * 60);
        assertEquals(1, diffEmHoras);
    } 

    @Test
    void gerarPayload_DeveConterTodosOsDadosDoUsuario() {
        // Arrange
        usuario.setRole("USER");
        usuario = loginRepository.save(usuario);

        // Act
        String token = jwtServiceGenerator.generateToken(usuario);
        Claims claims = invokePrivateExtractAllClaims(token);

        // Assert
        assertEquals(usuario.getUsername(), claims.get("username"));
        assertEquals(usuario.getId().toString(), claims.get("id"));
        assertEquals(usuario.getRole(), claims.get("role"));
        assertEquals("teste", claims.get("outracoisa"));
    }

    @Test
    void generateToken_ParaDiferentesUsuarios_DeveConterDadosEspecificos() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setUsername("maria123");
        usuario2.setEmail("maria@email.com");
        usuario2.setNuit("987654321");
        usuario2.setPassword(passwordEncoder.encode("senhaMaria"));
        usuario2.setRole("USER");
        usuario2.setAtivo(true);
        usuario2 = loginRepository.save(usuario2);

        // Act
        String token1 = jwtServiceGenerator.generateToken(usuario);
        String token2 = jwtServiceGenerator.generateToken(usuario2);

        // Assert
        String username1 = jwtServiceGenerator.extractUsername(token1);
        String username2 = jwtServiceGenerator.extractUsername(token2);
        
        assertEquals(usuario.getUsername(), username1);
        assertEquals(usuario2.getUsername(), username2);
    }

    @Test
    void isTokenValid_ComTokenAlterado_DeveSerInvalido() {
        // Arrange
        String tokenOriginal = jwtServiceGenerator.generateToken(usuario);
        String tokenAlterado = tokenOriginal.substring(0, tokenOriginal.length() - 5) + "xxxxx";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtServiceGenerator.isTokenValid(tokenAlterado, userDetails);
        });
    }

    @Test
    void generateToken_DeveTerIssuedAtCorreto() {
        // Arrange
        Date antes = new Date();
        
        // Act
        String token = jwtServiceGenerator.generateToken(usuario);
        Date issuedAt = jwtServiceGenerator.extractClaim(token, Claims::getIssuedAt);

        // Assert
        assertNotNull(issuedAt);
        assertFalse(issuedAt.after(antes) || issuedAt.equals(antes));
        assertTrue(issuedAt.before(new Date(antes.getTime() + 5000))); // dentro de 5 segundos
    }

    @Test
    void extractAllClaims_ComTokenValido_DeveRetornarClaimsCompletos() {
        // Act
        String token = jwtServiceGenerator.generateToken(usuario);
        Claims claims = invokePrivateExtractAllClaims(token);

        // Assert
        assertNotNull(claims);
        assertNotNull(claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.get("username"));
        assertNotNull(claims.get("id"));
        assertNotNull(claims.get("role"));
    }

    @Test
    void getSigningKey_DeveRetornarChaveValida() {
        // Arrange
        String token = jwtServiceGenerator.generateToken(usuario);

        // Act - Se conseguir extrair o token com sucesso, a chave está correta
        String username = jwtServiceGenerator.extractUsername(token);

        // Assert
        assertEquals(usuario.getUsername(), username);
    }

    // Métodos auxiliares para testar métodos privados
    private Date invokePrivateExtractExpiration(String token) {
        try {
            java.lang.reflect.Method method = JwtServiceGenerator.class.getDeclaredMethod("extractExpiration", String.class);
            method.setAccessible(true);
            return (Date) method.invoke(jwtServiceGenerator, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private Claims invokePrivateExtractAllClaims(String token) {
        try {
            java.lang.reflect.Method method = JwtServiceGenerator.class.getDeclaredMethod("extractAllClaims", String.class);
            method.setAccessible(true);
            return (Claims) method.invoke(jwtServiceGenerator, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	    }
}
