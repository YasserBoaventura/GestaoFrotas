package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.Collections;
import java.util.*; 

import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;

@ExtendWith(MockitoExtension.class)
public class DriverServiceUnitTest {
	  
	   @Mock
	   private RepositoryMotorista repositoryMotorista;
	
	    @InjectMocks
	    private ServiceMotorista serviceMotorista;
	    @Autowired 
	    private Motorista motorista;
	
	    @BeforeEach 
	    void setUp() {
        motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("João Silva");
	    motorista.setEmail("joao@email.com");
	    motorista.setTelefone("11999999999");
	    motorista.setNumeroCarta("12345678900");
	    motorista.setCategoriaHabilitacao("B");
	    motorista.setDataNascimento(LocalDate.of(1990, 1, 1));
	    motorista.setStatus(statusMotorista.DISPONIVEL);
	}
	
	@Test
	void salvar_ComDadosValidos_DeveSalvarMotorista() {
	    // Arrange
	    when(repositoryMotorista.save(any(Motorista.class))).thenReturn(motorista);
	
	    // Act
	    Map<String, String> response = serviceMotorista.salvar(motorista);
	
	    // Assert
	    assertNotNull(response);
	    assertEquals("Motorista salvo com sucesso", response.get("sucesso"));
	    verify(repositoryMotorista, times(1)).save(motorista);
	}
	
	@Test
	void salvar_ComMotoristaNull_DeveLancarExcecao() {
	    // Act & Assert
	    assertThrows(Exception.class, () -> {
	        serviceMotorista.salvar(null);
	    });
	    verify(repositoryMotorista, never()).save(any());
	}
	 
	@Test
	void salvar_ComEmailDuplicado_DeveLancarExcecao() {
	    // Arrange
	    when(repositoryMotorista.save(any(Motorista.class)))
	        .thenThrow(new RuntimeException("Email já cadastrado"));
	
	    // Act & Assert
	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        serviceMotorista.salvar(motorista);
	    });
	    
	    assertEquals("Email já cadastrado", exception.getMessage());
	    verify(repositoryMotorista, times(1)).save(any(Motorista.class));
	}
	@Test
	void deleteById_DeveDeletarMotorista() {
	 
	    when(repositoryMotorista.existsById(1L)).thenReturn(true);
	    doNothing().when(repositoryMotorista).deleteById(1L);
	
	   
	    String resultado = serviceMotorista.deleteById(1L);
	
	 
	    assertEquals("deletado com sucesso", resultado);
	    verify(repositoryMotorista, times(1)).deleteById(1L);
	}
		
	@Test
	void deleteById_ComIdInexistente_DeveLancarExcecao() {
	    // Arrange
	    when(repositoryMotorista.existsById(999L)).thenReturn(false);

	    // Act & Assert
	    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
	        serviceMotorista.deleteById(999L);
	    });
	    assertEquals("Motorista não encontrado", exception.getMessage());
	}
	
	@Test
	void findAll_DeveRetornarListaDeMotoristas() {
	    // Arrange
	    List<Motorista> motoristas = Arrays.asList(motorista, new Motorista());
	    when(repositoryMotorista.findAll()).thenReturn(motoristas);
	
	    // Act
	    List<Motorista> resultado = serviceMotorista.findAll();
	
	    // Assert
	    assertEquals(2, resultado.size());
	    verify(repositoryMotorista, times(1)).findAll();
	}
	
	@Test
	void findAll_QuandoNaoHaMotoristas_DeveRetornarListaVazia() {
	    // Arrange
	    when(repositoryMotorista.findAll()).thenReturn(Collections.emptyList());
	
	    // Act
	    List<Motorista> resultado = serviceMotorista.findAll();
	
	    // Assert
	    assertTrue(resultado.isEmpty());
	    verify(repositoryMotorista, times(1)).findAll();
	}
	
	@Test
	void update_ComDadosValidos_DeveAtualizarMotorista() {
	    // Arrange
	    when(repositoryMotorista.save(any(Motorista.class))).thenReturn(motorista);
	
	    // Act
	    String resultado = serviceMotorista.update(motorista, 1L);
	
	    // Assert
	    assertEquals("Motorista actualizado com sucesso", resultado);
	    assertEquals(1L, motorista.getId());
	    verify(repositoryMotorista, times(1)).save(motorista);
	}
	
	@Test
	void update_ComIdDiferente_DeveAtualizarIdCorreto() {
	    // Arrange
	    Motorista motoristaAtualizado = new Motorista();
	    motoristaAtualizado.setNome("João Silva Atualizado");
	    motoristaAtualizado.setEmail("joao.atualizado@email.com");
	    
	    when(repositoryMotorista.save(any(Motorista.class))).thenReturn(motoristaAtualizado);
	
	    // Act
	    String resultado = serviceMotorista.update(motoristaAtualizado, 5L);
	
	    // Assert
	    assertEquals("Motorista actualizado com sucesso", resultado);
	    assertEquals(5L, motoristaAtualizado.getId());
	    verify(repositoryMotorista, times(1)).save(motoristaAtualizado);
	}
	
	@Test
	void update_ComMotoristaNull_DeveLancarExcecao() {
	    // Act & Assert
	    assertThrows(Exception.class, () -> {
	        serviceMotorista.update(null, 1L);
	    });
	    verify(repositoryMotorista, never()).save(any());
	}
	
	@Test
	void findById_QuandoMotoristaExiste_DeveRetornarMotorista() {
	    // Arrange
	    when(repositoryMotorista.findById(1L)).thenReturn(Optional.of(motorista));
	
	    // Act
	    Motorista resultado = serviceMotorista.findById(1L);
	
	    // Assert
	    assertNotNull(resultado);
	    assertEquals(1L, resultado.getId());
	    assertEquals("João Silva", resultado.getNome());
	    verify(repositoryMotorista, times(1)).findById(1L);
	}
	
	@Test
	void findById_QuandoMotoristaNaoExiste_DeveLancarNoSuchElementException() {
	    // Arrange
	    when(repositoryMotorista.findById(999L)).thenReturn(Optional.empty());
	
	    // Act & Assert
	    assertThrows(NoSuchElementException.class, () -> {
	        serviceMotorista.findById(999L);
	    });
	    verify(repositoryMotorista, times(1)).findById(999L);
	}
	
	@Test
	void findByNome_ComNomeExistente_DeveRetornarListaDeMotoristas() {
	    // Arrange
	    List<Motorista> motoristas = Arrays.asList(motorista);
	    when(repositoryMotorista.findByNomeContainingIgnoreCase("João")).thenReturn(motoristas);
	
	    // Act
	    List<Motorista> resultado = serviceMotorista.findByNome("João");
	
	    // Assert
	    assertEquals(1, resultado.size());
	    assertEquals("João Silva", resultado.get(0).getNome());
	    verify(repositoryMotorista, times(1)).findByNomeContainingIgnoreCase("João");
	}
	
	@Test
	void findByNome_ComNomeParcial_DeveRetornarMotoristasQueContem() {
	    // Arrange
	    Motorista motorista2 = new Motorista();
	    motorista2.setNome("João Santos");
	    
	    List<Motorista> motoristas = Arrays.asList(motorista, motorista2);
	    when(repositoryMotorista.findByNomeContainingIgnoreCase("João")).thenReturn(motoristas);
	
	    // Act
	    List<Motorista> resultado = serviceMotorista.findByNome("João");
	
	    // Assert
	    assertEquals(2, resultado.size());
	    verify(repositoryMotorista, times(1)).findByNomeContainingIgnoreCase("João");
	}
	
	@Test
	void findByNome_ComNomeInexistente_DeveRetornarListaVazia() {
	
	    when(repositoryMotorista.findByNomeContainingIgnoreCase("Inexistente"))
	        .thenReturn(Collections.emptyList());
	
	
	    List<Motorista> resultado = serviceMotorista.findByNome("Inexistente");

	    assertTrue(resultado.isEmpty());
	    verify(repositoryMotorista, times(1)).findByNomeContainingIgnoreCase("Inexistente");
	}
	
	@Test
	void findByNome_ComNomeNull_DeveLancarExcecao() {

	    assertThrows(Exception.class, () -> {
	        serviceMotorista.findByNome(null);
	    });
	    verify(repositoryMotorista, never()).findByNomeContainingIgnoreCase(any());
	}
	
	@Test
	void findByNome_ComNomeVazio_DeveRetornarTodosMotoristas() {
	
	    List<Motorista> motoristas = Arrays.asList(motorista);
	    when(repositoryMotorista.findByNomeContainingIgnoreCase("")).thenReturn(motoristas);
	
	    List<Motorista> resultado = serviceMotorista.findByNome("");
	
	    // Assert
	    assertEquals(1, resultado.size());
	    verify(repositoryMotorista, times(1)).findByNomeContainingIgnoreCase("");
	} 
	
	@Test
	void salvar_DeveRetornarMapComSucesso() {
     
		when(repositoryMotorista.save(any(Motorista.class))).thenReturn(motorista);

	    Map<String, String> response = serviceMotorista.salvar(motorista);

	    assertTrue(response.containsKey("sucesso"));
	    assertEquals("Motorista salvo com sucesso", response.get("sucesso"));
	    assertTrue(response.size() == 1);
	}
	   
	@Test
	void update_DeveManterTotalViagens() {
	    // Arrange
	    when(repositoryMotorista.save(any(Motorista.class))).thenReturn(motorista);
	    String resultado = serviceMotorista.update(motorista, 1L);
	    assertEquals("Motorista actualizado com sucesso", resultado);
	  
	        verify(repositoryMotorista, times(1)).save(motorista);
	}
	}
	
	
