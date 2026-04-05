package com.GestaoRotas.GestaoRotas.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.TipoCarga;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test") 
@Transactional  
public class supplyServiceIntegrationTest {
	 
	    @Autowired
	    private ServiceAbastecimentos serviceAbastecimentos;
  
	    @Autowired
	    private RepositoryAbastecimentos repositoryAbastecimentos;

	    @Autowired
	    private RepositoryVeiculo repositoryVeiculo;

	    @Autowired
	    private RepositoryViagem repositoryViagem;

	    @Autowired
	    private RepositoryMotorista repositoryMotorista;
	    
	    @Autowired
	    private RepositoryRotas repositoryRota;

	    private Veiculo veiculo;
	    private Viagem viagem;
	    private Motorista motorista;
	    private Rotas rota;
	    private AbastecimentoDTO dto;

	    @BeforeEach
	    void setUp() {
    motorista = new Motorista();
    motorista.setNome("João Silva");
    motorista.setEmail("joao@teste.com");
    motorista.setTelefone("11999999999");
    motorista.setNumeroCarta("12345678900");
    motorista.setCategoriaHabilitacao("B");
    motorista.setDataNascimento(LocalDate.of(1990, 1, 1));
    motorista.setStatus(statusMotorista.ATIVO);
    motorista = repositoryMotorista.save(motorista);                  
  
    // Criar Rota
    rota = new Rotas();
    rota.setOrigem("São Paulo");
    rota.setDestino("Rio de Janeiro");                                                                   
    rota.setDistanciaKm(430.0);
    rota.setTempoEstimadoHoras(6.0);
   
    rota = repositoryRota.save(rota);

    // Criar Veículo
    veiculo = new Veiculo();
    veiculo.setModelo("Fusion");
    veiculo.setMatricula("XYZ-9876");
    veiculo.setAnoFabricacao(2021);
    veiculo.setCapacidadeTanque(60.0);
    veiculo.setKilometragemAtual(15000.0);
    veiculo.setStatus("DISPONIVEL");
    veiculo.setEmailResponsavel("teste@teste.com");
    veiculo = repositoryVeiculo.save(veiculo);

    // Criar Viagem
    viagem = new Viagem();
    viagem.setVeiculo(veiculo);
    viagem.setMotorista(motorista);
    viagem.setRota(rota);
    viagem.setStatus("EM_ANDAMENTO"); 
    viagem.setDataHoraPartida(LocalDate.now().atStartOfDay());
    viagem.setData(LocalDateTime.now());
    viagem.setKilometragemInicial(veiculo.getKilometragemAtual());
    viagem.setTipoCarga(TipoCarga.GERAL);
    viagem = repositoryViagem.save(viagem);

    // Configurar DTO
    dto = new AbastecimentoDTO();
    dto.setVeiculoId(veiculo.getId());
    dto.setViagemId(viagem.getId());
    dto.setDataAbastecimento(LocalDate.now());
    dto.setKilometragemVeiculo(15200.0);
    dto.setQuantidadeLitros(45.5);
    dto.setPrecoPorLitro(5.79);
    dto.setTipoCombustivel("GASOLINA");
    dto.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
}

@Test 
void save_DevePersistirAbastecimentoNoBanco() {
    // Act
    Map<String, String> response = serviceAbastecimentos.save(dto);
    assertNotNull(response);
    assertEquals("Abastecimento salvo", response.get("sucesso"));
     
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    Optional<abastecimentos> abastecimentoSalvo = repositoryAbastecimentos.findById(abastecimentoId);
    assertFalse(!abastecimentoSalvo.isPresent());  
    assertTrue(abastecimentoSalvo.isPresent());
    assertEquals(45.5, abastecimentoSalvo.get().getQuantidadeLitros());
    assertEquals(5.79, abastecimentoSalvo.get().getPrecoPorLitro());
    assertEquals("GASOLINA", abastecimentoSalvo.get().getTipoCombustivel());
    assertEquals(veiculo.getId(), abastecimentoSalvo.get().getVeiculo().getId());
    assertEquals(viagem.getId(), abastecimentoSalvo.get().getViagem().getId());
}
 
@Test
void save_SemViagem_DevePersistirAbastecimentoSemViagem() {
    // Arrange
    dto.setViagemId(null);

    // Act
    Map<String, String> response = serviceAbastecimentos.save(dto);

    // Assert
    assertNotNull(response);
    assertEquals("Abastecimento salvo", response.get("sucesso"));
    assertEquals("null", response.get("viagemAssociada"));
    
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    Optional<abastecimentos> abastecimentoSalvo = repositoryAbastecimentos.findById(abastecimentoId);
    
    assertTrue(abastecimentoSalvo.isPresent());
    assertNull(abastecimentoSalvo.get().getViagem());
}

@Test
void findAll_DeveRetornarTodosAbastecimentos() {
    // Arrange
    serviceAbastecimentos.save(dto);
    
    AbastecimentoDTO dto2 = new AbastecimentoDTO();
    dto2.setVeiculoId(veiculo.getId());
    dto2.setDataAbastecimento(LocalDate.now());
    dto2.setKilometragemVeiculo(15500.0);
    dto2.setQuantidadeLitros(30.0);
    dto2.setPrecoPorLitro(5.79);
    dto2.setTipoCombustivel("GASOLINA");
    dto2.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
    serviceAbastecimentos.save(dto2); 

    // Act
    List<abastecimentos> abastecimentos = serviceAbastecimentos.findAll();

    // Assert
    assertTrue(abastecimentos.size() >= 2);
}

@Test
void findById_DeveRetornarAbastecimentoCorreto() {
    // Arrange
    Map<String, String> response = serviceAbastecimentos.save(dto);
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));

    // Act
    abastecimentos abastecimentoEncontrado = serviceAbastecimentos.findById(abastecimentoId);

    // Assert
    assertNotNull(abastecimentoEncontrado);
    assertEquals(abastecimentoId, abastecimentoEncontrado.getId());
    assertEquals(45.5, abastecimentoEncontrado.getQuantidadeLitros());
}

@Test
void update_DeveAtualizarAbastecimento() {
    // Arrange
    Map<String, String> response = serviceAbastecimentos.save(dto);
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    
    AbastecimentoDTO updateDto = new AbastecimentoDTO();
    updateDto.setVeiculoId(veiculo.getId());
    updateDto.setViagemId(viagem.getId());
    updateDto.setDataAbastecimento(LocalDate.now());
    updateDto.setKilometragemVeiculo(16000.0);
    updateDto.setQuantidadeLitros(55.0);
    updateDto.setPrecoPorLitro(6.19);
    updateDto.setTipoCombustivel("ETANOL");
    updateDto.setStatusAbastecimento(statusAbastecimentos.REALIZADA);

    // Act
    String resultado = serviceAbastecimentos.update(updateDto, abastecimentoId);

    // Assert
    assertEquals("sucesso ao actualizar abastecimento", resultado);
    
    abastecimentos abastecimentoAtualizado = repositoryAbastecimentos.findById(abastecimentoId).get();
    assertNotNull(abastecimentoAtualizado); 
    assertEquals(55.0, abastecimentoAtualizado.getQuantidadeLitros());
    assertEquals(6.19, abastecimentoAtualizado.getPrecoPorLitro());
    assertEquals("ETANOL", abastecimentoAtualizado.getTipoCombustivel());
    assertEquals(16000.0, abastecimentoAtualizado.getKilometragemVeiculo());
}

@Test
void update_RemovendoViagem_DeveAtualizarParaViagemNull() {
    // Arrange
    Map<String, String> response = serviceAbastecimentos.save(dto);
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    
    AbastecimentoDTO updateDto = new AbastecimentoDTO();
    updateDto.setVeiculoId(veiculo.getId());
    updateDto.setViagemId(null);  // Remover viagem
    updateDto.setDataAbastecimento(LocalDate.now());
    updateDto.setKilometragemVeiculo(16000.0);
    updateDto.setQuantidadeLitros(55.0);
    updateDto.setPrecoPorLitro(6.19);
    updateDto.setTipoCombustivel("ETANOL");
    updateDto.setStatusAbastecimento(statusAbastecimentos.REALIZADA);

    // Act
    String resultado = serviceAbastecimentos.update(updateDto, abastecimentoId);

    // Assert
    assertEquals("sucesso ao actualizar abastecimento", resultado);
    
    abastecimentos abastecimentoAtualizado = repositoryAbastecimentos.findById(abastecimentoId).get();
    assertNull(abastecimentoAtualizado.getViagem());
}

@Test
void deletar_DeveRemoverAbastecimento() {
    // Arrange
    Map<String, String> response = serviceAbastecimentos.save(dto);
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    
    assertTrue(repositoryAbastecimentos.findById(abastecimentoId).isPresent());

    // Act
    String resultado = serviceAbastecimentos.deletar(abastecimentoId);

    // Assert
    assertEquals("abastecimento  deletado com sucesso", resultado);
    assertFalse(repositoryAbastecimentos.findById(abastecimentoId).isPresent());
}

@Test
void relatorioPorVeiculo_DeveRetornarDadosAgregados() {
    // Arrange
    serviceAbastecimentos.save(dto);
    
    AbastecimentoDTO dto2 = new AbastecimentoDTO();
    dto2.setVeiculoId(veiculo.getId());
    dto2.setDataAbastecimento(LocalDate.now());
    dto2.setKilometragemVeiculo(15500.0);
    dto2.setQuantidadeLitros(30.0);
    dto2.setPrecoPorLitro(5.79);
    dto2.setTipoCombustivel("GASOLINA");
    dto2.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
    serviceAbastecimentos.save(dto2);

    // Act
    List<RelatorioCombustivelDTO> relatorios = serviceAbastecimentos.relatorioPorVeiculo();

    // Assert
    assertNotNull(relatorios);
    assertTrue(relatorios.size() > 0);
}

@Test
void relatorioPorPeriodo_DeveRetornarDadosDoPeriodo() {
    // Arrange
    serviceAbastecimentos.save(dto);
    
    LocalDate inicio = LocalDate.now().minusDays(30);
    LocalDate fim = LocalDate.now().plusDays(30);

    // Act
    List<RelatorioCombustivelDTO> relatorios = serviceAbastecimentos.relatorioPorPeriodo(inicio, fim);

    // Assert
    assertNotNull(relatorios);
}

@Test
void numeroAbastecimentoRealizados_DeveContarCorretamente() {
    // Arrange
    serviceAbastecimentos.save(dto);
    serviceAbastecimentos.save(dto);
    
    // Act
    Long quantidade = serviceAbastecimentos.numeroAbastecimentoRealizados();

    // Assert
    assertTrue(quantidade >= 2);
}

@Test
void save_ComVeiculoInexistente_DeveLancarExcecao() {
    // Arrange
    dto.setVeiculoId(999L);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceAbastecimentos.save(dto);
    });
    
    assertEquals("Veículo não encontrado", exception.getMessage());
}

@Test
void save_ComViagemInexistente_DeveLancarExcecao() {
    // Arrange
    dto.setViagemId(999L);

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceAbastecimentos.save(dto);
    });
    
    assertTrue(exception.getMessage().contains("Viagem não encontrada"));
}

@Test
void update_ComAbastecimentoInexistente_DeveLancarExcecao() {
    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceAbastecimentos.update(dto, 999L);
    });
    
    assertTrue(exception.getMessage().contains("Abastecimento não encontrado"));
}

@Test
void save_DeveCriarCustoAssociado() {
    // Act
    Map<String, String> response = serviceAbastecimentos.save(dto);

    // Assert
    assertNotNull(response.get("custoId"));
    Long custoId = Long.parseLong(response.get("custoId"));
    assertTrue(custoId > 0);
}

@Test
void update_DeveAtualizarCustoAssociado() {
    // Arrange
    Map<String, String> response = serviceAbastecimentos.save(dto);
    Long abastecimentoId = Long.parseLong(response.get("abastecimentoId"));
    
    AbastecimentoDTO updateDto = new AbastecimentoDTO();
    updateDto.setVeiculoId(veiculo.getId());
    updateDto.setQuantidadeLitros(100.0);  // Aumentar quantidade
    updateDto.setPrecoPorLitro(5.79);
    updateDto.setTipoCombustivel("GASOLINA");
    updateDto.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
    updateDto.setDataAbastecimento(LocalDate.now());
    updateDto.setKilometragemVeiculo(16000.0);

    // Act
    String resultado = serviceAbastecimentos.update(updateDto, abastecimentoId);

    // Assert
    assertEquals("sucesso ao actualizar abastecimento", resultado);
    }
}



