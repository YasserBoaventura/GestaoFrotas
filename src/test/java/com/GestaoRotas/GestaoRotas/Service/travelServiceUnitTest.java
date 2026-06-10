package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.GestaoRotas.GestaoRotas.Custos.custoService;
import com.GestaoRotas.GestaoRotas.DTO.CancelarViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Model.TipoCarga;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

@ExtendWith(MockitoExtension.class)
public class travelServiceUnitTest {
	  
	    @Mock
	    private RepositoryViagem repositoryViagem;

	    @Mock
	    private RepositoryMotorista motoristaRepository;

	    @Mock
	    private RepositoryVeiculo veiculoRepository;

	    @Mock
	    private RepositoryRotas rotaRepository;

	    @Mock
	    private custoService custoService;
	    @InjectMocks
	    private ServiceViagem serviceViagem; 
	    
	     private Motorista motorista;  
	     private Veiculo veiculo;
	     private Rotas rota;
	     private Viagem viagem;
	     private ViagensDTO viagemDTO; 
	     private ConcluirViagemRequest  concluirRequest ; 
	     private CancelarViagemRequest cancelarRequest; 
	    
	    @BeforeEach  
	    void setUp() {
	        // Setup Motorista
    motorista = new Motorista();
    motorista.setId(1L);
    motorista.setNome("João Silva");
    motorista.setStatus(statusMotorista.DISPONIVEL);

    // Setup Veiculo
    veiculo = new Veiculo();
    veiculo.setId(1L);
    veiculo.setModelo("Fusion");
    veiculo.setMatricula("ABC-1234");
    veiculo.setStatus("DISPONIVEL");

    // Setup Rota
    rota = new Rotas();
    rota.setId(1L);
    rota.setOrigem("São Paulo");
    rota.setDestino("Rio de Janeiro");
    rota.setDistanciaKm(430.0);

    // Setup Viagem
    viagem = new Viagem();
    viagem.setId(1L);
    viagem.setMotorista(motorista);
    viagem.setVeiculo(veiculo);
    viagem.setRota(rota);
    viagem.setStatus("AGENDADA");
    viagem.setDataHoraPartida(LocalDateTime.now().plusDays(1));
    viagem.setKilometragemInicial(10000.0);
    viagem.setData(LocalDateTime.now());

    // Setup DTO
    viagemDTO = new ViagensDTO();
    viagemDTO.setMotoristaId(1L);
    viagemDTO.setVeiculoId(1L);
    viagemDTO.setRotaId(1L);
    viagemDTO.setStatus("AGENDADA");
    viagemDTO.setDataHoraPartida(LocalDateTime.now().plusDays(1));
    viagemDTO.setKilometragemInicial(10000.0);
    viagemDTO.setTipoCarga(TipoCarga.GERAL);

    // Setup Requests 
    concluirRequest = new ConcluirViagemRequest();
    concluirRequest.setKilometragemFinal(10430.0);
    concluirRequest.setDataHoraChegada(LocalDateTime.now().plusHours(6));
    concluirRequest.setObservacoes("Viagem concluída com sucesso");

    cancelarRequest = new CancelarViagemRequest();
    cancelarRequest.setMotivo("Problemas mecânicos");
}

@Test
void salvar_ComDadosValidos_DeveSalvarViagem() {
    // Arrange
    when(motoristaRepository.findById(1L)).thenReturn(Optional.of(motorista));
    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
    when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);

    // Act
    String resultado = serviceViagem.salvar(viagemDTO);

    // Assert
    assertEquals("viagem salva com sucesso", resultado);
    verify(repositoryViagem, times(1)).save(any(Viagem.class));
}

@Test
void salvar_ComMotoristaIndisponivel_DeveRetornarErro() {
    // Arrange
    motorista.setStatus(statusMotorista.FERIAS);
    when(motoristaRepository.findById(1L)).thenReturn(Optional.of(motorista));
    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
    when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));

    // Act
    String resultado = serviceViagem.salvar(viagemDTO);

    // Assert
    assertTrue(resultado.contains("Motorista não está disponível"));
    verify(repositoryViagem, never()).save(any());
}

@Test
void salvar_ComVeiculoIndisponivel_DeveRetornarErro() {
    // Arrange
    veiculo.setStatus("EM_MANUTENCAO");
    when(motoristaRepository.findById(1L)).thenReturn(Optional.of(motorista));
    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
    when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));

    // Act
    String resultado = serviceViagem.salvar(viagemDTO);

    // Assert
    assertTrue(resultado.contains("veiculo nao disponivel"));
    verify(repositoryViagem, never()).save(any());
}

@Test
void salvar_MotoristaNaoEncontrado_DeveLancarExcecao() {
    // Arrange
    when(motoristaRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceViagem.salvar(viagemDTO);
    });
    assertEquals("Motorista não encontrado", exception.getMessage());
}

@Test
void update_ComDadosValidos_DeveAtualizarViagem() {
    // Arrange
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(motoristaRepository.findById(1L)).thenReturn(Optional.of(motorista));
    when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
    when(rotaRepository.findById(1L)).thenReturn(Optional.of(rota));
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);

    // Act
    String resultado = serviceViagem.update(viagemDTO, 1L);

    // Assert
    assertEquals("viagem atualizada com sucesso!", resultado);
    verify(repositoryViagem, times(1)).save(any(Viagem.class));
}

@Test
void update_ViagemNaoEncontrada_DeveLancarExcecao() {
    // Arrange
    when(repositoryViagem.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceViagem.update(viagemDTO, 999L);
    });
    assertEquals("Viagem não encontrada", exception.getMessage());
}

@Test
void iniciarViagem_ComSucesso_DeveAtualizarStatus() {
    // Arrange
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(motoristaRepository.save(any(Motorista.class))).thenReturn(motorista);
    when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);

    // Act
    Map<String, String> response = serviceViagem.iniciarViagem(1L);

    // Assert
    assertEquals("viagem inicializada com sucesso", response.get("message"));
    assertEquals(statusMotorista.EM_VIAGEM, motorista.getStatus());
    assertEquals("EM_VIAGEM", veiculo.getStatus());
    verify(motoristaRepository, times(1)).save(motorista);
    verify(veiculoRepository, times(1)).save(veiculo);
}

@Test
void iniciarViagem_ViagemNaoEncontrada_DeveRetornarErro() {
    // Arrange
    when(repositoryViagem.findById(999L)).thenReturn(Optional.empty());

    // Act
    Map<String, String> response = serviceViagem.iniciarViagem(999L);

    // Assert
    assertTrue(response.containsKey("error"));
    assertTrue(response.get("error").contains("viagem nao existente"));
} 

@Test
void concluirViagem_ComSucesso_DeveAtualizarStatus() {
    // Arrange
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);
    when(motoristaRepository.save(any(Motorista.class))).thenReturn(motorista);
    when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

    // Act
    Map<String, String> response = serviceViagem.ConcluirViagem(concluirRequest, 1L);

    // Assert
    assertEquals("viagem concluida com sucesso", response.get("sucesso"));
    assertEquals(statusMotorista.DISPONIVEL, motorista.getStatus());
    assertEquals("DISPONIVEL", veiculo.getStatus());
    verify(repositoryViagem, times(1)).save(any(Viagem.class));
}

@Test
void cancelarViagem_ComSucesso_DeveCancelarViagem() {
    // Arrange
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);
    when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

    // Act
    Map<String, String> response = serviceViagem.cancelarViagem(cancelarRequest, 1L);

    // Assert
    assertEquals("canselada com sucesso", response.get("sucesso"));
    assertEquals("DISPONIVEL", veiculo.getStatus());
    assertTrue(viagem.getObservacoes().contains("Motivo: Problemas mecânicos"));
}

@Test
void cancelarViagem_SemMotivo_DeveCancelarSemAdicionarObservacao() {
    // Arrange 
    cancelarRequest.setMotivo(null); 
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryViagem.save(any(Viagem.class))).thenReturn(viagem);
    when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);
    
    //  
    Map<String, String> response = serviceViagem.cancelarViagem(cancelarRequest, 1L);

    // Assert     
    assertEquals("canselada com sucesso", response.get("sucesso"));
   assertTrue(viagem.getObservacoes() == null || !viagem.getObservacoes().contains("Motivo:"));
	    }  
  
	    @Test
	    void findAll_DeveRetornarListaDeViagens() {
	        // Arrange
    List<Viagem> viagens = Arrays.asList(viagem, new Viagem());
    when(repositoryViagem.findAll()).thenReturn(viagens);

    // Act
    List<Viagem> resultado = serviceViagem.findAll();

    // Assert
    assertEquals(2, resultado.size());
    verify(repositoryViagem, times(1)).findAll();
}

@Test
void findById_DeveRetornarViagem() {
    // Arrange
    when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));

    // Act
    Viagem resultado = serviceViagem.findById(1L);

    // Assert
    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
}

@Test
void findById_NaoEncontrada_DeveLancarExcecao() {
    // Arrange
    when(repositoryViagem.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceViagem.findById(999L);
    });
    assertEquals("Viagem nao encontrada", exception.getMessage());
}

@Test
void delete_DeveDeletarViagem() {
    // Arrange
    doNothing().when(repositoryViagem).deleteById(1L);

    // Act
    String resultado = serviceViagem.delete(1L);

    // Assert
    assertEquals("deletado com sucesso", resultado);
    verify(repositoryViagem, times(1)).deleteById(1L);
}

@Test
void getContByStatus_DeveRetornarQuantidade() {
    // Arrange
    when(repositoryViagem.countByStatus("AGENDADA")).thenReturn(5L);

    // Act
    Long resultado = serviceViagem.getContByStatus("AGENDADA");

    // Assert
    assertEquals(5L, resultado);
}

@Test
void findByIdMotorista_DeveRetornarViagensDoMotorista() {
    // Arrange
    List<Viagem> viagens = Arrays.asList(viagem);
    when(repositoryViagem.findByMotoristaId(1L)).thenReturn(viagens);

    // Act
    List<Viagem> resultado = serviceViagem.findByIdMotorista(1L);

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void findByVeiculoId_DeveRetornarViagensDoVeiculo() {
    // Arrange
    List<Viagem> viagens = Arrays.asList(viagem);
    when(repositoryViagem.findByVeiculoId(1L)).thenReturn(viagens);

    // Act
    List<Viagem> resultado = serviceViagem.findByVeiculoId(1L);

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void relatorioPorMotorista_DeveRetornarRelatorio() {
    // Arrange
    List<RelatorioMotoristaDTO> relatorios = Arrays.asList(new RelatorioMotoristaDTO());
    when(repositoryViagem.relatorioPorMotorista()).thenReturn(relatorios);

    // Act
    List<RelatorioMotoristaDTO> resultado = serviceViagem.relatorioPorMotorista();

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void relatorioPorVeiculo_DeveRetornarRelatorio() {
    // Arrange
    List<RelatorioPorVeiculoDTO> relatorios = Arrays.asList(new RelatorioPorVeiculoDTO());
    when(repositoryViagem.relatorioPorVeiculo()).thenReturn(relatorios);

    // Act
    List<RelatorioPorVeiculoDTO> resultado = serviceViagem.gerarRelatorioPorVeiculo();

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void validarMotorista_ComStatusBloqueado_DeveRetornarFalse() {
    // Teste de método privado via reflexão
    motorista.setStatus(statusMotorista.FERIAS);
    boolean resultado = invokePrivateValidarMotorista(motorista);
    assertFalse(resultado);
}

@Test
void validarVeiculo_ComStatusIndisponivel_DeveRetornarTrue() {
    // Teste de método privado via reflexão
    veiculo.setStatus("EM_MANUTENCAO");
    boolean resultado = invokePrivateValidarVeiculo(veiculo);
    assertTrue(resultado);
}

@Test
void validarVeiculo_ComStatusDisponivel_DeveRetornarFalse() {
    // Teste de método privado via reflexão
    veiculo.setStatus("DISPONIVEL");
    boolean resultado = invokePrivateValidarVeiculo(veiculo);
    assertFalse(resultado);
}

// Métodos auxiliares para testar métodos privados
private boolean invokePrivateValidarMotorista(Motorista motorista) {
    try {
        java.lang.reflect.Method method = ServiceViagem.class.getDeclaredMethod("validarMotorista", Motorista.class);
        method.setAccessible(true);
        return (boolean) method.invoke(serviceViagem, motorista);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

private boolean invokePrivateValidarVeiculo(Veiculo veiculo) {
    try {
        java.lang.reflect.Method method = ServiceViagem.class.getDeclaredMethod("validarVeiculo", Veiculo.class);
            method.setAccessible(true);
            return (boolean) method.invoke(serviceViagem, veiculo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
    
    
    


