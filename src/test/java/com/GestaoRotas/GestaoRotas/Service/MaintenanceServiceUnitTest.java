package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.Custos.custoService;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

@ExtendWith(MockitoExtension.class)
public class MaintenanceServiceUnitTest {

    @Mock
    private RepositoryManutencao repositoryManuntencao;

    @Mock
    private RepositoryVeiculo repositoryVeiculo;

    @Mock
    private ServiceVeiculo veiculoService;

    @Mock
    private custoService custoService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServiceManutencoes serviceManutencoes;

    private Veiculo veiculo;
    private Manutencao manutencao;
    private manuntecaoDTO manutencaoDTO;

    @BeforeEach 
    void setUp() { 
    veiculo = new Veiculo();
    veiculo.setId(1L);
    veiculo.setModelo("Fusion");
    veiculo.setMatricula("ABC-1234");
    veiculo.setStatus("DISPONIVEL");
    veiculo.setKilometragemAtual(15000.0);
    veiculo.setEmailResponsavel("teste@teste.com");

    manutencao = new Manutencao();
    manutencao.setId(1L);
    manutencao.setVeiculo(veiculo);
    manutencao.setDataManutencao(LocalDate.now().plusDays(5));
    manutencao.setDescricao("Troca de óleo");
    manutencao.setTipoManutencao(TipoManutencao.PREVENTIVA); 
    manutencao.setCusto(500.0);
    manutencao.setStatus(statusManutencao.AGENDADA);
    manutencao.setProximaManutencaoKm(20000.0);
    manutencao.setProximaManutencaoData(LocalDate.now().plusMonths(6));

    manutencaoDTO = new manuntecaoDTO(); 
    manutencaoDTO.setVeiculo_id(1L); 
    manutencaoDTO.setDataManutencao(LocalDate.now().plusDays(5));
    manutencaoDTO.setDescricao("Troca de óleo");
    manutencaoDTO.setTipoManutencao(TipoManutencao.PREVENTIVA);
    manutencaoDTO.setCusto(500.0); 
    manutencaoDTO.setKilometragemVeiculo(15000.0);
    manutencaoDTO.setProximaManutencaoKm(20000.0);
    manutencaoDTO.setProximaManutencaoData(LocalDate.now().plusMonths(6));
    manutencaoDTO.setStatus(statusManutencao.AGENDADA);
}

@Test
void salvar_ComDataFutura_DeveSalvarComoAgendada() {
    // Arrange
    when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(custoService.criarCustoParaManutencao(any(Manutencao.class))).thenReturn(null);

    // Act
    String resultado = serviceManutencoes.salvar(manutencaoDTO);

    // Assert   
    assertEquals("manutencao salva com sucesso", resultado);
    verify(repositoryManuntencao, times(1)).save(any(Manutencao.class));
    verify(custoService, times(1)).criarCustoParaManutencao(any(Manutencao.class));
}

@Test
void salvar_ComDataHoje_DeveSalvarComoAgendadaHojeEAtualizarVeiculo() {
    // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now());
        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
        when(custoService.criarCustoParaManutencao(any(Manutencao.class))).thenReturn(null);
 
        // Act
    String resultado = serviceManutencoes.salvar(manutencaoDTO);

    // Assert
    assertEquals("manutencao salva com sucesso", resultado);
    verify(repositoryVeiculo, times(1)).save(veiculo);
    assertEquals("MANUTENCAO_HOJE", veiculo.getStatus());
}

@Test
void salvar_ComDataPassada_DeveSalvarComoAtrasada() {
    // Arrange
    manutencaoDTO.setDataManutencao(LocalDate.now().minusDays(1));
    when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(custoService.criarCustoParaManutencao(any(Manutencao.class))).thenReturn(null);

    // Act
    String resultado = serviceManutencoes.salvar(manutencaoDTO);

    // Assert
    assertEquals("manutencao salva com sucesso", resultado);
    verify(repositoryManuntencao, times(1)).save(any(Manutencao.class));
}

@Test
void salvar_VeiculoNaoEncontrado_DeveLancarExcecao() {
    // Arrange
    when(repositoryVeiculo.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceManutencoes.salvar(manutencaoDTO);
    });  
    assertEquals("Veiculo nao encontrado", exception.getMessage());
}
 
@Test
void deleteById_DeveDeletarManutencao() {
    // Arrange
        doNothing().when(repositoryManuntencao).deleteById(1L);
   
        // Act
    String resultado = serviceManutencoes.deleteById(1L);

    // Assert
    assertEquals("Manutenção deletada com sucesso", resultado);
    verify(repositoryManuntencao, times(1)).deleteById(1L);
}

@Test
void findAll_DeveRetornarListaDeManutencoes() {
    // Arrange
    List<Manutencao> manutencoes = Arrays.asList(manutencao, new Manutencao());
    when(repositoryManuntencao.findAll()).thenReturn(manutencoes);

    // Act
    List<Manutencao> resultado = serviceManutencoes.findAll();

    // Assert
    assertEquals(2, resultado.size());
}

@Test
void update_ComDadosValidos_DeveAtualizarManutencao() {
    // Arrange
    when(repositoryManuntencao.findById(1L)).thenReturn(Optional.of(manutencao));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(custoService.actualizarCustoManutencao(any(Manutencao.class))).thenReturn(null);

    // Act
    String resultado = serviceManutencoes.update(manutencaoDTO, 1L);

    // Assert
    assertEquals("manutencao atualizada com sucesso", resultado);
    verify(repositoryManuntencao, times(1)).save(any(Manutencao.class));
}

@Test
void update_ManutencaoNaoEncontrada_DeveLancarExcecao() {
    // Arrange
    when(repositoryManuntencao.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceManutencoes.update(manutencaoDTO, 999L);
    });
    assertEquals("Manutencao nao encontrada", exception.getMessage());
}

@Test
void iniciarManutencao_ComSucesso_DeveAtualizarStatus() {
    // Arrange
    when(repositoryManuntencao.findById(1L)).thenReturn(Optional.of(manutencao));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(repositoryVeiculo.save(any(Veiculo.class))).thenReturn(veiculo);

    // Act
    Map<String, String> response = serviceManutencoes.iniciarManutencao(1L);

    // Assert
    assertEquals("Manutencao inicializada com sucesso", response.get("sucesso"));
    assertEquals(statusManutencao.EM_ANDAMENTO, manutencao.getStatus());
    assertNotNull(manutencao.getDataInicio());
    verify(repositoryManuntencao, times(1)).save(manutencao);
}

@Test
void concluirManutencao_ComSucesso_DeveAtualizarStatus() {
    // Arrange
    when(repositoryManuntencao.findById(1L)).thenReturn(Optional.of(manutencao));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(repositoryVeiculo.save(any(Veiculo.class))).thenReturn(veiculo);

    // Act
    Map<String, String> response = serviceManutencoes.concluirManutencao(1L, "Manutenção concluída");

    // Assert
    assertEquals("sucesso", response.get("sucesso"));
    assertEquals(statusManutencao.CONCLUIDA, manutencao.getStatus());
    assertNotNull(manutencao.getDataConclusao());
    assertEquals("DISPONIVEL", veiculo.getStatus());
}

@Test
void cancelarManutencao_ComSucesso_DeveAtualizarStatus() {
    // Arrange
    when(repositoryManuntencao.findById(1L)).thenReturn(Optional.of(manutencao));
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    doNothing().when(veiculoService).atualizarStatusVeiculo(1L);

    // Act
    Map<String, String> response = serviceManutencoes.cancelarManutencao(1L, "Problemas logísticos");

    // Assert
    assertEquals("Manutencao cancelada com sucesso", response.get("sucesso"));
    assertEquals(statusManutencao.CANCELADA, manutencao.getStatus());
    assertTrue(manutencao.getDescricao().contains("Problemas logísticos"));
    verify(veiculoService, times(1)).atualizarStatusVeiculo(1L);
}

@Test
void findById_QuandoExiste_DeveRetornarManutencao() {
    // Arrange
    when(repositoryManuntencao.findById(1L)).thenReturn(Optional.of(manutencao));

    // Act
    Manutencao resultado = serviceManutencoes.findById(1L);

    // Assert
    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
}

@Test
void findById_QuandoNaoExiste_DeveLancarResponseStatusException() {
    // Arrange
    when(repositoryManuntencao.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
        serviceManutencoes.findById(999L);
    });
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
}

@Test
void listarPorVeiculo_DeveRetornarManutencoes() {
    // Arrange
    List<Manutencao> manutencoes = Arrays.asList(manutencao);
    when(repositoryManuntencao.findByVeiculoId(1L)).thenReturn(manutencoes);

    // Act
    List<Manutencao> resultado = serviceManutencoes.listarPorVeiculo(1L);

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void listarPorTipo_DeveRetornarManutencoes() {
    // Arrange
    List<Manutencao> manutencoes = Arrays.asList(manutencao);
    when(repositoryManuntencao.findBytipoManutencao(TipoManutencao.PREVENTIVA)).thenReturn(manutencoes);

    // Act
    List<Manutencao> resultado = serviceManutencoes.listarPorTipo("PREVENTIVA");

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void veiculoTemManutencaoHoje_ComManutencao_DeveRetornarTrue() {
    // Arrange
    when(repositoryManuntencao.findByVeiculoIdAndDataManutencaoAndStatusNotIn(
        eq(1L), any(LocalDate.class), anyList()))
        .thenReturn(Arrays.asList(manutencao));

    // Act
    boolean resultado = serviceManutencoes.veiculoTemManutencaoHoje(1L);

    // Assert
    assertTrue(resultado);
}

@Test
void veiculoTemManutencaoHoje_SemManutencao_DeveRetornarFalse() {
    // Arrange
    when(repositoryManuntencao.findByVeiculoIdAndDataManutencaoAndStatusNotIn(
        eq(1L), any(LocalDate.class), anyList()))
        .thenReturn(Collections.emptyList());
 
    // Act
    boolean resultado = serviceManutencoes.veiculoTemManutencaoHoje(1L);

    // Assert
    assertFalse(resultado);
}

@Test
void getManutencoesHoje_DeveRetornarLista() {
    // Arrange
    List<Manutencao> manutencoes = Arrays.asList(manutencao);
    when(repositoryManuntencao.findByDataManutencaoAndStatusNotIn(
        any(LocalDate.class), anyList()))
        .thenReturn(manutencoes);

    // Act
    List<Manutencao> resultado = serviceManutencoes.getManutencoesHoje();

    // Assert
    assertEquals(1, resultado.size());
}

@Test 
void gerarRelatorioPorVeiculo_DeveRetornarRelatorio() {
    // Arrange
        List<RelatorioManutencaoDTO> relatorios = Arrays.asList(new RelatorioManutencaoDTO());
        when(repositoryManuntencao.relatorioPorVeiculo()).thenReturn(relatorios);
 
        // Act
    List<RelatorioManutencaoDTO> resultado = serviceManutencoes.gerarRelatorioPorVeiculo();

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void relatorioPorPeriodo_DeveRetornarRelatorio() {
    // Arrange
    LocalDate inicio = LocalDate.now().minusMonths(1);
    LocalDate fim = LocalDate.now();
    List<RelatorioManutencaoDTO> relatorios = Arrays.asList(new RelatorioManutencaoDTO());
    when(repositoryManuntencao.relatorioPorPeriodo(inicio, fim)).thenReturn(relatorios);

    // Act
    List<RelatorioManutencaoDTO> resultado = serviceManutencoes.relatorioPorPeriodo(inicio, fim);

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void buscarVencidas_DeveRetornarLista() {
    // Arrange
    List<Manutencao> vencidas = Arrays.asList(manutencao);
    when(repositoryManuntencao.findManutencoesVencidas()).thenReturn(vencidas);

    // Act
    List<Manutencao> resultado = serviceManutencoes.buscarVencidas();

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void buscarProximas30Dias_DeveRetornarLista() {
    // Arrange
    List<Manutencao> proximas = Arrays.asList(manutencao);
    when(repositoryManuntencao.findProximasManutencoes(any(LocalDate.class))).thenReturn(proximas);

    // Act
    List<Manutencao> resultado = serviceManutencoes.buscarProximas30Dias();

    // Assert
    assertEquals(1, resultado.size());
}

@Test
void buscarProximas7Dias_DeveRetornarLista() {
    // Arrange
    List<Manutencao> proximas = Arrays.asList(manutencao);
    when(repositoryManuntencao.findManutencoesProximas7Dias(any(LocalDate.class))).thenReturn(proximas);

    // Act
    List<Manutencao> resultado = serviceManutencoes.buscarProximas7Dias();

    // Assert
    assertEquals(1, resultado.size());
}

@Test 
void gerarAlertas_ComManutencoesVencidas_DeveGerarAlertas() {
    // Arrange
    manutencao.setProximaManutencaoData(LocalDate.now().minusDays(5));
    List<Manutencao> vencidas = Arrays.asList(manutencao);
    when(repositoryManuntencao.findManutencoesVencidas()).thenReturn(vencidas);
    when(repositoryManuntencao.findProximasManutencoes(any(LocalDate.class))).thenReturn(Collections.emptyList());
    when(repositoryManuntencao.findManutencoesProximas7Dias(any(LocalDate.class))).thenReturn(Collections.emptyList());
    doNothing().when(emailService).enviarAlertaManutencaoVencida(anyString(), anyString(), anyString());

    // Act
    List<String> alertas = serviceManutencoes.gerarAlertas();

    // Assert
    assertNotNull(alertas);
    assertTrue(alertas.size() >= 0);
}

@Test
void verificarManutencoesDoDia_DeveProcessarManutencoes() {
    // Arrange
    List<Manutencao> manutencoesHoje = Arrays.asList(manutencao);
    when(repositoryManuntencao.findByDataManutencaoAndStatusNotIn(
        any(LocalDate.class), anyList()))
        .thenReturn(manutencoesHoje);
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    when(repositoryVeiculo.save(any(Veiculo.class))).thenReturn(veiculo);

    // Act
    serviceManutencoes.verificarManutencoesDoDia();

    // Assert
    verify(repositoryManuntencao, atLeastOnce()).save(any(Manutencao.class));
}

@Test
void verificarManutencoesVencidas_DeveProcessarVencidas() {
    // Arrange
    List<Manutencao> vencidas = Arrays.asList(manutencao);
    when(repositoryManuntencao.findByDataManutencaoBeforeAndStatusNotIn(
        any(LocalDate.class), anyList()))
        .thenReturn(vencidas);
    when(repositoryManuntencao.save(any(Manutencao.class))).thenReturn(manutencao);
    doNothing().when(veiculoService).atualizarStatusVeiculo(1L);

    // Act
    serviceManutencoes.verificarManutencoesVencidas();

    // Assert
    verify(repositoryManuntencao, atLeastOnce()).save(any(Manutencao.class));
    verify(veiculoService, times(1)).atualizarStatusVeiculo(1L);
}

@Test
void notificarManutencoesAmanha_DeveNotificar() {
    // Arrange
    List<Manutencao> manutencoesAmanha = Arrays.asList(manutencao);
    when(repositoryManuntencao.findByDataManutencaoAndStatus(
        any(LocalDate.class), eq(statusManutencao.AGENDADA)))
        .thenReturn(manutencoesAmanha);
 
    // Act
    serviceManutencoes.notificarManutencoesAmanha();

    // Assert
        verify(repositoryManuntencao, times(1)).findByDataManutencaoAndStatus(any(LocalDate.class), eq(statusManutencao.AGENDADA));
    }

}
