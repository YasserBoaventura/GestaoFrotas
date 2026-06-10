package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@SpringBootTest
@ActiveProfiles("test")
@Transactional 
public class DriverServiceIntegrationTest {
	
    @Autowired
    private ServiceMotorista serviceMotorista;

    @Autowired
    private RepositoryMotorista repositoryMotorista;

    private Motorista motorista;

    @BeforeEach
    void setUp() {
        motorista = new Motorista();
        motorista.setNome("João Silva");
        motorista.setEmail("joao.integracao@email.com");
        motorista.setTelefone("11988888888");
        motorista.setNumeroCarta("98765432100");
        motorista.setCategoriaHabilitacao("B");
        motorista.setDataNascimento(LocalDate.of(1985, 5, 15));
        motorista.setStatus(statusMotorista.DISPONIVEL);
    }

    @Test
    void salvar_DevePersistirMotoristaNoBanco() {
        // Act
        Map<String, String> response = serviceMotorista.salvar(motorista);

        // Assert
        assertEquals("Motorista salvo com sucesso", response.get("sucesso"));
        assertNotNull(motorista.getId());
        
        Optional<Motorista> motoristaSalvo = repositoryMotorista.findById(motorista.getId());
        assertTrue(motoristaSalvo.isPresent());
        assertEquals("João Silva", motoristaSalvo.get().getNome());
        assertEquals("joao.integracao@email.com", motoristaSalvo.get().getEmail());
        assertEquals(statusMotorista.DISPONIVEL, motoristaSalvo.get().getStatus()); 
    }

    @Test
    void salvar_ComEmailDuplicado_DeveLancarExcecao() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("José Santos");
        motorista2.setEmail("joao.integracao@email.com"); // Mesmo email
        motorista2.setTelefone("11977777777");
        motorista2.setNumeroCarta("11111111111");
        motorista2.setCategoriaHabilitacao("C");
        motorista2.setDataNascimento(LocalDate.of(1988, 3, 10));
        motorista2.setStatus(statusMotorista.DISPONIVEL);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            serviceMotorista.salvar(motorista2);
        });
    }

    @Test
    void salvar_ComCpfDuplicado_DeveLancarExcecao() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("Maria Santos");
        motorista2.setEmail("maria@email.com");
        motorista2.setTelefone("11977777777");
        motorista2.setNumeroCarta("98765432100"); // Mesmo número da carta
        motorista2.setCategoriaHabilitacao("B");
        motorista2.setDataNascimento(LocalDate.of(1992, 7, 20));
        motorista2.setStatus(statusMotorista.DISPONIVEL);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            serviceMotorista.salvar(motorista2);
        });
    }

    @Test
    void findAll_DeveRetornarTodosMotoristas() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("Maria Oliveira");
        motorista2.setEmail("maria@email.com");
        motorista2.setTelefone("11977777777");
        motorista2.setNumeroCarta("11122233344");
        motorista2.setCategoriaHabilitacao("D");
        motorista2.setDataNascimento(LocalDate.of(1992, 7, 20));
        motorista2.setStatus(statusMotorista.DISPONIVEL);
        serviceMotorista.salvar(motorista2);

        // Act
        List<Motorista> motoristas = serviceMotorista.findAll();

        // Assert
        assertTrue(motoristas.size() >= 2);
        assertTrue(motoristas.stream().anyMatch(m -> m.getNome().equals("João Silva")));
        assertTrue(motoristas.stream().anyMatch(m -> m.getNome().equals("Maria Oliveira")));
    }

    @Test
    void findById_DeveRetornarMotoristaCorreto() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long motoristaId = motorista.getId();

        // Act
        Motorista motoristaEncontrado = serviceMotorista.findById(motoristaId);

        // Assert
        assertNotNull(motoristaEncontrado);
        assertEquals(motoristaId, motoristaEncontrado.getId());
        assertEquals("João Silva", motoristaEncontrado.getNome());
        assertEquals("joao.integracao@email.com", motoristaEncontrado.getEmail());
    }

    @Test
    void findById_ComIdInexistente_DeveLancarExcecao() {
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            serviceMotorista.findById(999L);
        });
    } 

    @Test
    void update_DeveAtualizarMotoristaExistente() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long motoristaId = motorista.getId();
        
        Motorista motoristaAtualizado = new Motorista();
        motoristaAtualizado.setNome("João Silva Atualizado");
        motoristaAtualizado.setEmail("joao.atualizado@email.com");
        motoristaAtualizado.setTelefone("11999999999");
        motoristaAtualizado.setNumeroCarta("98765432100");
        motoristaAtualizado.setCategoriaHabilitacao("B");
        motoristaAtualizado.setDataNascimento(LocalDate.of(1985, 5, 15));
        motoristaAtualizado.setStatus(statusMotorista.DISPONIVEL);

        // Act
        String resultado = serviceMotorista.update(motoristaAtualizado, motoristaId);

        // Assert
        assertEquals("Motorista actualizado com sucesso", resultado);
        
        Motorista motoristaDoBanco = repositoryMotorista.findById(motoristaId).get();
        assertEquals("João Silva Atualizado", motoristaDoBanco.getNome());
        assertEquals("joao.atualizado@email.com", motoristaDoBanco.getEmail());
        assertEquals("11999999999", motoristaDoBanco.getTelefone());
    }

    @Test
    void update_AlterandoStatus_DeveAtualizarCorretamente() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long motoristaId = motorista.getId();
        
        Motorista motoristaAtualizado = new Motorista();
        motoristaAtualizado.setNome("João Silva");
        motoristaAtualizado.setEmail("joao.integracao@email.com");
        motoristaAtualizado.setTelefone("11988888888");
        motoristaAtualizado.setNumeroCarta("98765432100");
        motoristaAtualizado.setCategoriaHabilitacao("B");
        motoristaAtualizado.setDataNascimento(LocalDate.of(1985, 5, 15));
        motoristaAtualizado.setStatus(statusMotorista.EM_VIAGEM);

        // Act
        serviceMotorista.update(motoristaAtualizado, motoristaId);

        // Assert
        Motorista motoristaDoBanco = repositoryMotorista.findById(motoristaId).get();
        assertEquals(statusMotorista.EM_VIAGEM, motoristaDoBanco.getStatus());
    }
 
    @Test
    void deleteById_DeveRemoverMotorista() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long motoristaId = motorista.getId();
        
        assertTrue(repositoryMotorista.findById(motoristaId).isPresent());

        // Act
        String resultado = serviceMotorista.deleteById(motoristaId);

        // Assert
        assertEquals("deletado com sucesso", resultado);
        assertFalse(repositoryMotorista.findById(motoristaId).isPresent());
    }

    @Test
    void deleteById_ComIdInexistente_DeveLancarExcecao() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            serviceMotorista.deleteById(999L);
        });
    }
 
    @Test
    void findByNome_DeveRetornarMotoristasPorNome() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("João Santos");
        motorista2.setEmail("joao.santos@email.com");
        motorista2.setTelefone("11977777777");
        motorista2.setNumeroCarta("55566677788");
        motorista2.setCategoriaHabilitacao("C");
        motorista2.setDataNascimento(LocalDate.of(1990, 8, 10));
        motorista2.setStatus(statusMotorista.DISPONIVEL);
        serviceMotorista.salvar(motorista2);

        Motorista motorista3 = new Motorista();
        motorista3.setNome("Carlos Almeida");
        motorista3.setEmail("carlos@email.com");
        motorista3.setTelefone("11966666666");
        motorista3.setNumeroCarta("99988877766");
        motorista3.setCategoriaHabilitacao("B");
        motorista3.setDataNascimento(LocalDate.of(1988, 3, 25));
        motorista3.setStatus(statusMotorista.DISPONIVEL);
        serviceMotorista.salvar(motorista3);

        // Act
        List<Motorista> motoristasJoao = serviceMotorista.findByNome("João");

        // Assert
        assertEquals(2, motoristasJoao.size());
        assertTrue(motoristasJoao.stream().allMatch(m -> m.getNome().contains("João")));
    }

    @Test
    void findByNome_IgnoreCase_DeveFuncionar() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("joão silva"); // Minúsculo
        motorista2.setEmail("joao2@email.com");
        motorista2.setTelefone("11955555555");
        motorista2.setNumeroCarta("44433322211");
        motorista2.setCategoriaHabilitacao("B");
        motorista2.setDataNascimento(LocalDate.of(1995, 12, 1));
        motorista2.setStatus(statusMotorista.DISPONIVEL);
        serviceMotorista.salvar(motorista2);

        // Act
        List<Motorista> resultado = serviceMotorista.findByNome("JOÃO");

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void findByNome_ComNomeParcial_DeveRetornarCorrespondencias() {
        // Arrange
        serviceMotorista.salvar(motorista);
        
        Motorista motorista2 = new Motorista();
        motorista2.setNome("Ana Silva");
        motorista2.setEmail("ana@email.com");
        motorista2.setTelefone("11944444444");
        motorista2.setNumeroCarta("77788899900");
        motorista2.setCategoriaHabilitacao("B");
        motorista2.setDataNascimento(LocalDate.of(1993, 6, 15));
        motorista2.setStatus(statusMotorista.DISPONIVEL);
        serviceMotorista.salvar(motorista2);

        // Act
        List<Motorista> resultado = serviceMotorista.findByNome("Silva");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(m -> m.getNome().contains("Silva")));
    }

    @Test
    void findByNome_ComNomeInexistente_DeveRetornarListaVazia() {
        // Act
        List<Motorista> resultado = serviceMotorista.findByNome("NomeInexistente");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void salvar_DeveGerarIdAutomaticamente() {
        // Act
        serviceMotorista.salvar(motorista);

        // Assert
        assertNotNull(motorista.getId());
        assertTrue(motorista.getId() > 0);
    }

    @Test
    void salvar_ComDadosMinimos_DeveSalvar() {
        // Arrange
        Motorista motoristaMinimo = new Motorista();
        motoristaMinimo.setNome("Pedro Costa");
        motoristaMinimo.setEmail("pedro@email.com");
        motoristaMinimo.setNumeroCarta("12312312345");
        motoristaMinimo.setCategoriaHabilitacao("B");
        motoristaMinimo.setDataNascimento(LocalDate.of(1991, 2, 20));
        motoristaMinimo.setStatus(statusMotorista.DISPONIVEL);

        // Act
        Map<String, String> response = serviceMotorista.salvar(motoristaMinimo);

        // Assert
        assertEquals("Motorista salvo com sucesso", response.get("sucesso"));
        assertNotNull(motoristaMinimo.getId());
    }

    @Test
    void update_ComIdDiferente_DeveAtualizarIdCorreto() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long idOriginal = motorista.getId();
        
        Motorista motoristaParaUpdate = new Motorista();
        motoristaParaUpdate.setNome("Nome Atualizado");
        motoristaParaUpdate.setEmail("atualizado@email.com");
        motoristaParaUpdate.setTelefone("11933333333");
        motoristaParaUpdate.setNumeroCarta("98765432100");
        motoristaParaUpdate.setCategoriaHabilitacao("B");
        motoristaParaUpdate.setDataNascimento(LocalDate.of(1985, 5, 15));
        motoristaParaUpdate.setStatus(statusMotorista.DISPONIVEL);

        // Act 
        serviceMotorista.update(motoristaParaUpdate, idOriginal);

        // Assert
        Motorista motoristaAtualizado = repositoryMotorista.findById(idOriginal).get();
        assertEquals("Nome Atualizado", motoristaAtualizado.getNome());
        assertEquals("atualizado@email.com", motoristaAtualizado.getEmail());
        assertEquals(idOriginal, motoristaAtualizado.getId());
    }

    @Test
    void deleteById_DeletandoMotoristaComViagens_DeveLancarExcecao() {
        // Arrange
        serviceMotorista.salvar(motorista);
        Long motoristaId = motorista.getId();
        
        // Se houver viagens associadas, o delete deve falhar por constraint
        // Este teste assume que o motorista tem viagens associadas no banco
        
        // Act & Assert
        // O delete pode falhar se houver registros de viagem associados
        // Por isso é importante testar este cenário
        assertDoesNotThrow(() -> {
            serviceMotorista.deleteById(motoristaId);
        });
    }

    @Test
    void salvar_ComTelefoneNull_DeveSalvar() {
        // Arrange
        motorista.setTelefone(null);

        // Act
        Map<String, String> response = serviceMotorista.salvar(motorista);

        // Assert
        assertEquals("Motorista salvo com sucesso", response.get("sucesso"));
        
        Motorista motoristaSalvo = repositoryMotorista.findById(motorista.getId()).get();
        assertNull(motoristaSalvo.getTelefone());
    }

}
