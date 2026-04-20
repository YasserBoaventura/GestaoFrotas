package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoListDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoViagemDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.Custos.CustoRepository;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoUpdateDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCarga;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Model.statusRota;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import java.time.*;
import jakarta.transaction.Transactional;
import java.util.*; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CostsServiceIntegrationTest {

    @Autowired
    private com.GestaoRotas.GestaoRotas.Custos.custoService custoService;

    @Autowired
    private CustoRepository custoRepository;

    @Autowired
    private RepositoryVeiculo veiculoRepository;

    @Autowired
    private RepositoryAbastecimentos abastecimentoRepository;

    @Autowired
    private RepositoryManutencao manutencaoRepository;

    @Autowired
    private RepositoryViagem viagemRepository;

    @Autowired
    private RepositoryMotorista motoristaRepository;

    @Autowired
    private RepositoryRotas rotaRepository;

    private Veiculo veiculo;
    private abastecimentos abastecimento;
    private Manutencao manutencao;
    private Viagem viagem;
    private Motorista motorista;
    private Rotas rota;
    private  CustoViagemDTO custoViagemDTO; 

    @BeforeEach
    void setUp() {
        // Criar Motorista
    motorista = new Motorista();
    motorista.setNome("João Silva");
    motorista.setEmail("joao@teste.com");
    motorista.setTelefone("11999999999");
    motorista.setNumeroCarta("12345678900");
    motorista.setCategoriaHabilitacao("B");
    motorista.setDataNascimento(LocalDate.of(1990, 1, 1));
    motorista.setStatus(statusMotorista.DISPONIVEL);
    motorista = motoristaRepository.save(motorista);

    // Criar Rota
    rota = new Rotas();
    rota.setOrigem("São Paulo");
    rota.setDestino("Rio de Janeiro");
    rota.setDistanciaKm(430.0);
    rota.setTempoEstimadoHoras(6.0);
    rota.setStatusRota(statusRota.ATIVA);
    rota = rotaRepository.save(rota);
  
    // Criar Veículo
    veiculo = new Veiculo();
    veiculo.setModelo("Fusion");
    veiculo.setMatricula("XYZ-9876");
    veiculo.setAnoFabricacao(2021);
    veiculo.setCapacidadeTanque(60.0);
    veiculo.setKilometragemAtual(15000.0);
    veiculo.setStatus("DISPONIVEL");
    veiculo.setEmailResponsavel("teste@teste.com");
    veiculo = veiculoRepository.save(veiculo);

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
    viagem = viagemRepository.save(viagem);

    // Criar Abastecimento
    abastecimento = new abastecimentos();
    abastecimento.setVeiculo(veiculo);
    abastecimento.setViagem(viagem);
    abastecimento.setDataAbastecimento(LocalDate.now());
    abastecimento.setKilometragemVeiculo(15200.0);
    abastecimento.setQuantidadeLitros(45.5);
    abastecimento.setPrecoPorLitro(5.79);
    abastecimento.setTipoCombustivel("GASOLINA");
    abastecimento.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
    abastecimento = abastecimentoRepository.save(abastecimento);

    // Criar Manutencao
    manutencao = new Manutencao();
    manutencao.setVeiculo(veiculo);
    manutencao.setDataManutencao(LocalDate.now());
    manutencao.setDescricao("Troca de óleo");
    manutencao.setTipoManutencao(TipoManutencao.PREVENTIVA);
    manutencao.setCusto(350.0);
    manutencao.setStatus(statusManutencao.CONCLUIDA);
    manutencao = manutencaoRepository.save(manutencao);  
   
   //setUp pra a dto 
    custoViagemDTO = new CustoViagemDTO();
    custoViagemDTO.setVeiculoId(veiculo.getId());
    custoViagemDTO.setViagemId(viagem.getId());
    custoViagemDTO.setValor(75.50);
    custoViagemDTO.setDescricao("Pedágio");
    custoViagemDTO.setTipo(TipoCusto.PEDAGIO);
    custoViagemDTO.setObservacoes("Pedágio na Dutra");
      
  
    
} 

@Test                          
void registrarCustoManual_DevePersistirCusto() {
    // Arrange
    CustoRequestDTO request = new CustoRequestDTO();
    request.setVeiculoId(veiculo.getId());
    request.setValor(150.0);
    request.setDescricao("Troca de pneu");
    request.setTipo(TipoCusto.MANUTENCAO_CORRETIVA);
    request.setData(LocalDate.now());

    // Act 
    Custo resultado = custoService.registrarCustoManual(request);

    // Assert  
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(150.0, resultado.getValor());
    assertEquals("Troca de pneu", resultado.getDescricao());
    
    Optional<Custo> custoSalvo = custoRepository.findById(resultado.getId());
    assertTrue(custoSalvo.isPresent());
}

@Test
void registrarCustoManual_ComTodosCampos_DeveSalvarCompletamente() {
    // Arrange
    CustoRequestDTO request = new CustoRequestDTO();
    request.setVeiculoId(veiculo.getId());
    request.setViagemId(viagem.getId());
    request.setAbastecimentoId(abastecimento.getId());
    request.setManutencaoId(manutencao.getId());
    request.setValor(500.0);
    request.setDescricao("Custo completo");
    request.setTipo(TipoCusto.OUTROS);
    request.setStatus(StatusCusto.PAGO);
    request.setData(LocalDate.now());
    request.setObservacoes("Observação de teste");

    // Act
    Custo resultado = custoService.registrarCustoManual(request);

    // Assert
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(viagem.getId(), resultado.getViagem().getId());
    assertEquals(abastecimento.getId(), resultado.getAbastecimento().getId());
    assertEquals(manutencao.getId(), resultado.getManutencao().getId());
}

@Test
void criarCustoParaAbastecimento_DeveCriarCustoAutomaticamente() {
    // Act
    Custo resultado = custoService.criarCustoParaAbastecimento(abastecimento);

    // Assert
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(abastecimento.getValorTotal(), resultado.getValor());
    assertEquals(TipoCusto.COMBUSTIVEL, resultado.getTipo());
    assertEquals(abastecimento.getId(), resultado.getAbastecimento().getId());
}

@Test
void criarCustoParaManutencao_DeveCriarCustoAutomaticamente() {
    // Act
    Custo resultado = custoService.criarCustoParaManutencao(manutencao);

    // Assert
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(manutencao.getCusto(), resultado.getValor());
    assertEquals(TipoCusto.MANUTENCAO_PREVENTIVA, resultado.getTipo());
    assertEquals(manutencao.getId(), resultado.getManutencao().getId());
}

@Test 
void criarCustoParaViagem_DeveCriarCusto() {
    // Arrange
    CustoViagemDTO custoViagemDTO = new CustoViagemDTO();
    custoViagemDTO.setVeiculoId(veiculo.getId());
    custoViagemDTO.setViagemId(viagem.getId());
    custoViagemDTO.setValor(75.50);
    custoViagemDTO.setDescricao("Pedágio");
    custoViagemDTO.setTipo(TipoCusto.PEDAGIO);
    custoViagemDTO.setObservacoes("Pedágio na Dutra");

    // Act
    Custo resultado = custoService.criarCustoParaViagem(custoViagemDTO);

    // Assert
    assertNotNull(resultado);
    assertNotNull(resultado.getId());
    assertEquals(75.50, resultado.getValor());
    assertEquals(TipoCusto.PEDAGIO, resultado.getTipo());
    assertEquals("Pedágio", resultado.getDescricao());
}

@Test
void atualizarTotaisVeiculo_DeveCalcularCorretamente() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    // Act
    custoService.atualizarTotaisVeiculo(veiculo.getId());

    // Assert
    Veiculo veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).get();
    assertNotNull(veiculoAtualizado.getCustoTotal());
    assertTrue(veiculoAtualizado.getCustoTotal() > 0);
    assertNotNull(veiculoAtualizado.getCustoCombustivel());
    assertNotNull(veiculoAtualizado.getCustoManutencao());
}

@Test
void getDashboardCustos_DeveRetornarDadosCompletos() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    // Act
    DashboardCustosDTO dashboard = custoService.getDashboardCustos();

    // Assert
    assertNotNull(dashboard);
    assertNotNull(dashboard.getTotalMesAtual());
    assertNotNull(dashboard.getCustosPorTipo());
    assertNotNull(dashboard.getVeiculosMaisCaros());
    assertNotNull(dashboard.getUltimosCustos());
    assertNotNull(dashboard.getTotalCustos());
}

@Test
void buscarCustosPorVeiculoPeriodo_DeveRetornarCustos() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    LocalDate inicio = LocalDate.now().minusMonths(1);
    LocalDate fim = LocalDate.now().plusMonths(1);

    // Act
    List<Custo> custos = custoService.buscarCustosPorVeiculoPeriodo(veiculo.getId(), inicio, fim);

    // Assert
    assertNotNull(custos);
    assertTrue(custos.size() > 0);
}

@Test
void getCustoMensalUltimos12Meses_DeveRetornarMap() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);

    // Act
    Map<String, Double> custoMensal = custoService.getCustoMensalUltimos12Meses();

    // Assert
    assertNotNull(custoMensal);
    assertEquals(12, custoMensal.size());
}

@Test
void gerarRelatorioDetalhado_DeveRetornarRelatorioCompleto() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    RelatorioFilterDTO filtro = new RelatorioFilterDTO();
    filtro.setDataInicio(LocalDate.now().minusMonths(1).toString());
    filtro.setDataFim(LocalDate.now().plusMonths(1).toString());
    filtro.setDataInicioTop5VeiculosMaisCarro(LocalDate.now().minusMonths(6));
    filtro.setDataFimTop5VeiculosMaisCarro(LocalDate.now());

    // Act
    RelatorioCustosDetalhadoDTO  relatorio = custoService.gerarRelatorioDetalhado(filtro);

    // Assert
    assertNotNull(relatorio);
    assertNotNull(relatorio.getTotalPeriodo());
    assertNotNull(relatorio.getQuantidadeCustos());
    assertNotNull(relatorio.getTotalPorTipo());
    assertNotNull(relatorio.getCustosDetalhados());
}

@Test
void gerarRelatorioDetalhado_ComFiltroVeiculo_DeveFiltrarPorVeiculo() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    RelatorioFilterDTO filtro = new RelatorioFilterDTO();
    filtro.setVeiculoId(veiculo.getId());
    filtro.setDataInicio(LocalDate.now().minusMonths(1).toString());
    filtro.setDataFim(LocalDate.now().plusMonths(1).toString());
    filtro.setDataInicioTop5VeiculosMaisCarro(LocalDate.now().minusMonths(6));
    filtro.setDataFimTop5VeiculosMaisCarro(LocalDate.now());

    // Act
    RelatorioCustosDetalhadoDTO relatorio = custoService.gerarRelatorioDetalhado(filtro);

    // Assert
    assertNotNull(relatorio);
}

@Test
void atualizarCusto_DeveModificarCustoExistente() {
    // Arrange
    Custo custo = custoService.criarCustoParaAbastecimento(abastecimento);
    
    CustoUpdateDTO updateDTO = new CustoUpdateDTO();
    updateDTO.setDescricao("Descrição atualizada");
    updateDTO.setValor(300.0);
    updateDTO.setObservacoes("Observação de teste");
    updateDTO.setStatus(StatusCusto.PAGO);
    updateDTO.setTipo(TipoCusto.COMBUSTIVEL);

    // Act
    String resultado = custoService.atualizarCusto(custo.getId(), updateDTO);

    // Assert
    assertEquals("custo actualizado com sucesso!", resultado);
    
    Custo custoAtualizado = custoRepository.findById(custo.getId()).get();
    assertEquals("Descrição atualizada", custoAtualizado.getDescricao());
    assertEquals(300.0, custoAtualizado.getValor());
    assertEquals("Observação de teste", custoAtualizado.getObservacoes());
}

@Test
void excluirCusto_DeveRemoverCusto() {
    // Arrange
    Custo custo = custoService.criarCustoParaAbastecimento(abastecimento);

Optional<Custo> encontrado = custoRepository.findById(custo.getId());

assertTrue(encontrado.isPresent());
       // Act
    String resultado = custoService.excluirCusto(custo.getId());

    // Assert
    assertEquals("custo excluido com sucesso", resultado);
    assertFalse(custoRepository.findById(custo.getId()).isPresent());
}

@Test
void valorTotalCustos_DeveCalcularSoma() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    // Act
    Double total = custoService.valorTotalCustos();

    // Assert
    assertNotNull(total);
    assertTrue(total > 0);
}

@Test
void buscarPorPeriodo_DeveRetornarCustosDTO() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    LocalDate inicio = LocalDate.now().minusMonths(1);
    LocalDate fim = LocalDate.now().plusMonths(1);

    // Act
    List<CustoDTO> custos = custoService.buscarPorPeriodo(inicio, fim);

    // Assert
    assertNotNull(custos);
    assertTrue(custos.size() > 0);
}

@Test
void listar_DeveRetornarListaDeCustos() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    // Act
    List<CustoListDTO> custos = custoService.listar();

    // Assert
    assertFalse(custos.isEmpty()); 
    assertNotNull(custos);
    assertTrue(custos.size() >= 2);
}

@Test
void numeroCustos_DeveContarCorretamente() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.criarCustoParaManutencao(manutencao);

    // Act
    Integer total = custoService.numeroCustos();

    // Assert
    assertNotNull(total);
    assertTrue(total >= 2);
}

@Test
void numeroCustoPorStatus_DeveContarPorStatus() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);

    // Act
    Optional<Integer> totalPago = custoService.numeroCustoPorStatus(StatusCusto.PAGO);

    // Assert
    assertTrue(totalPago.isPresent());
    assertTrue(totalPago.get() > 0);
}

@Test
void numeroCustoPorTipo_DeveContarPorTipo() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);

    // Act
    Optional<Integer> totalCombustivel = custoService.numeroCustoPorTipo(TipoCusto.COMBUSTIVEL);

    // Assert
    assertTrue(totalCombustivel.isPresent());
    assertTrue(totalCombustivel.get() > 0);
}

@Test
void migrarAbastecimentosExistentes_DeveMigrarCorretamente() {
    // Act
    custoService.migrarAbastecimentosExistentes();

    // Assert
    boolean existe = custoService.verificarAbastecimentoId(abastecimento.getId());
    assertTrue(existe);
}

@Test
void migrarManutencoesExistentes_DeveMigrarCorretamente() {
    // Act
    custoService.migrarManutencoesExistentes();

    // Assert
    Optional<Custo> custoEncontrado = custoRepository.findByManutencaoId(manutencao.getId());
    assertTrue(custoEncontrado.isPresent());
}

@Test
void processarNovoAbastecimento_DeveCriarCustoEAlerta() {
    // Act
    custoService.processarNovoAbastecimento(abastecimento);

    // Assert
    boolean existe = custoService.verificarAbastecimentoId(abastecimento.getId());
    assertTrue(existe);
}

@Test
void processarNovaManutencao_DeveCriarCusto() {
    // Act
    custoService.processarNovaManutencao(manutencao);

    // Assert
    Optional<Custo> custoEncontrado = custoRepository.findByManutencaoId(manutencao.getId());
    assertTrue(custoEncontrado.isPresent());
}

@Test
void getVeiculosComCustoAcimaDaMedia_DeveRetornarLista() {
    // Arrange
    custoService.criarCustoParaAbastecimento(abastecimento);
    custoService.atualizarTotaisVeiculo(veiculo.getId());

    // Act
    List<Veiculo> veiculos = custoService.getVeiculosComCustoAcimaDaMedia();

    // Assert
    assertNotNull(veiculos);
}

@Test
void actualizarCustoParaViagem_DeveAtualizarCusto() {
    // Arrange
    Custo custo = custoService.criarCustoParaViagem(custoViagemDTO);
    
    CustoViagemDTO updateDTO = new CustoViagemDTO();
    updateDTO.setVeiculoId(veiculo.getId()); 
    updateDTO.setViagemId(viagem.getId()); 
    updateDTO.setValor(200.0);   
    updateDTO.setDescricao("Pedágio atualizado");
    updateDTO.setTipo(TipoCusto.PEDAGIO);
    updateDTO.setObservacoes("Observação atualizada");

    // Act
    String resultado = custoService.actualizarCustoParaViagem(updateDTO, custo.getId());

    // Assert
    assertEquals("custo pra viagem actualizado com sucesso", resultado);
}

@Test
void atualizarTotaisTodosVeiculos_DeveAtualizarTodos() {
    // Arrange 
    custoService.criarCustoParaAbastecimento(abastecimento);

    // Act   
    custoService.atualizarTotaisTodosVeiculos();

    // Assert
	        Veiculo veiculoAtualizado = veiculoRepository.findById(veiculo.getId()).get();
	        assertNotNull(veiculoAtualizado.getCustoTotal());
	    }
}
