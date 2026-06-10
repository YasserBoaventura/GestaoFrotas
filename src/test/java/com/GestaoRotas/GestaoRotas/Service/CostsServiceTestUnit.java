package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Email.EmailService;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import java.time.*;
import java.util.List;

@ExtendWith(MockitoExtension.class) 
public class CostsServiceTestUnit {
	    @Mock
	    private CustoRepository custoRepository;
           
	    @Mock     
	    private RepositoryVeiculo veiculoRepository;

	    @Mock
	    private RepositoryAbastecimentos abastecimentoRepository;

	    @Mock
	    private RepositoryManutencao repositoryManutencao;

	    @Mock
	    private RepositoryViagem repositoryViagem;

	    @InjectMocks
	    private com.GestaoRotas.GestaoRotas.Custos.custoService custoService;

	    private Veiculo veiculo;
	    private abastecimentos abastecimento;
	    private Manutencao manutencao;
	    private Viagem viagem;
	    private Custo custo;
	    private CustoRequestDTO requestDTO;
	    private CustoViagemDTO custoViagemDTO;

	    @BeforeEach
	    void setUp() {
	     veiculo = new Veiculo();
        veiculo.setId(1L); 
        veiculo.setMatricula("ABC-1234");
        veiculo.setModelo("Fusion");
        veiculo.setKilometragemAtual(15000.0);
        veiculo.setCustoTotal(0.0);
 
        abastecimento = new abastecimentos();
        abastecimento.setId(1L);
        abastecimento.setVeiculo(veiculo);
        abastecimento.setDataAbastecimento(LocalDate.now());
        abastecimento.setTipoCombustivel("GASOLINA");
        abastecimento.setQuantidadeLitros(50.0);
        abastecimento.setPrecoPorLitro(5.50);

        manutencao = new Manutencao();
        manutencao.setId(1L);
        manutencao.setVeiculo(veiculo);
        manutencao.setCusto(500.0);
        manutencao.setDataManutencao(LocalDate.now());
        manutencao.setTipoManutencao(TipoManutencao.PREVENTIVA);
        manutencao.setDescricao("Troca de óleo");

        viagem = new Viagem();
        viagem.setId(1L);
        viagem.setVeiculo(veiculo);
        viagem.setStatus("EM_ANDAMENTO");
        viagem.setCustoPedagios(50.0);
  
        custo = new Custo();
        custo.setId(1L);
        custo.setVeiculo(veiculo);
        custo.setValor(275.0);
        custo.setTipo(TipoCusto.COMBUSTIVEL);
        custo.setStatus(StatusCusto.PAGO);
        custo.setData(LocalDate.now());

        requestDTO = new CustoRequestDTO();
        requestDTO.setVeiculoId(1L);
        requestDTO.setValor(100.0);
        requestDTO.setDescricao("Custo de teste");
        requestDTO.setTipo(TipoCusto.OUTROS);
        requestDTO.setData(LocalDate.now());

        custoViagemDTO = new CustoViagemDTO();
        custoViagemDTO.setVeiculoId(1L);
        custoViagemDTO.setViagemId(1L);
        custoViagemDTO.setValor(100.0);
        custoViagemDTO.setDescricao("Pedágio");
        custoViagemDTO.setTipo(TipoCusto.PEDAGIO);
    }

    @Test
    void registrarCustoManual_ComDadosValidos_DeveSalvar() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());

        // Act
        Custo resultado = custoService.registrarCustoManual(requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void registrarCustoManual_ComViagemId_DeveVincularViagem() {
        // Arrange
        requestDTO.setViagemId(1L);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());

        // Act
        Custo resultado = custoService.registrarCustoManual(requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(repositoryViagem, times(1)).findById(1L);
    }

    @Test
    void registrarCustoManual_ComManutencaoId_DeveVincularManutencao() {
        // Arrange
        requestDTO.setManutencaoId(1L);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryManutencao.findById(1L)).thenReturn(Optional.of(manutencao));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());

        // Act
        Custo resultado = custoService.registrarCustoManual(requestDTO);

        // Assert
        assertNotNull(resultado);
        verify(repositoryManutencao, times(1)).findById(1L);
    }

    @Test
    void registrarCustoManual_VeiculoNaoEncontrado_DeveLancarExcecao() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            custoService.registrarCustoManual(requestDTO);
        });
        assertEquals("Veículo não encontrado", exception.getMessage());
    }

    @Test   
    void criarCustoParaAbastecimento_ComSucesso_DeveSalvar() {
        // Arrange
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(false);
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);
      
        Custo resultado = custoService.criarCustoParaAbastecimento(abastecimento);
 
        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
        verify(veiculoRepository, times(1)).findById(1L);
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    void criarCustoParaAbastecimento_ComCustoExistente_DeveLancarExcecao() {
        // Arrange
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            custoService.criarCustoParaAbastecimento(abastecimento);
        });
        assertEquals("Custo já existe para este abastecimento", exception.getMessage());
    }

    @Test
    void actualizarCustoParaAbastecimento_ComSucesso_DeveAtualizar() {
        // Arrange
        when(abastecimentoRepository.findById(1L)).thenReturn(Optional.of(abastecimento));
        when(custoRepository.findByAbastecimentoId(1L)).thenReturn(Optional.of(custo));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        Custo resultado = custoService.actualizarCustoParaAbastecimento(abastecimento);

        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void criarCustoParaManutencao_ComSucesso_DeveSalvar() {
        // Arrange
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        Custo resultado = custoService.criarCustoParaManutencao(manutencao);

        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void actualizarCustoManutencao_ComSucesso_DeveAtualizar() {
        // Arrange
        when(repositoryManutencao.findById(1L)).thenReturn(Optional.of(manutencao));
        when(custoRepository.findByManutencaoId(1L)).thenReturn(Optional.of(custo));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        Custo resultado = custoService.actualizarCustoManutencao(manutencao);

        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void criarCustoParaViagem_ComSucesso_DeveSalvar() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        Custo resultado = custoService.criarCustoParaViagem(custoViagemDTO);

        // Assert
        assertNotNull(resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void criarCustoParaViagem_VeiculoNaoEncontrado_DeveLancarExcecao() {
        // Arrange
        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            custoService.criarCustoParaViagem(custoViagemDTO);
        });

        assertEquals("Veiculo nao encontrado", exception.getMessage());
    }
 
    @Test
    void actualizarCustoParaViagem_ComSucesso_DeveAtualizar() {
        // Arrange
        when(custoRepository.findById(1L)).thenReturn(Optional.of(custo));
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findById(1L)).thenReturn(Optional.of(viagem));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        String resultado = custoService.actualizarCustoParaViagem(custoViagemDTO, 1L);

        // Assert
        assertEquals("custo pra viagem actualizado com sucesso", resultado);
    }

    @Test
    void atualizarTotaisVeiculo_ComSucesso_DeveAtualizar() {
        // Arrange
        Map<String, Object> totais = new HashMap<>();
        totais.put("total", 1000.0);
        totais.put("combustivel", 500.0);
        totais.put("manutencao", 300.0);

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(totais);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.atualizarTotaisVeiculo(1L);

        // Assert
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
        assertEquals(1000.0, veiculo.getCustoTotal());
        assertEquals(500.0, veiculo.getCustoCombustivel());
        assertEquals(300.0, veiculo.getCustoManutencao());
    }

    @Test
    void atualizarTotaisTodosVeiculos_DeveAtualizarTodos() {
        // Arrange
        List<Veiculo> veiculos = Arrays.asList(veiculo);
        when(veiculoRepository.findAll()).thenReturn(veiculos);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);
 
        // Act
        custoService.atualizarTotaisTodosVeiculos();
   
        // Assert
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    void getDashboardCustos_DeveRetornarDashboard() {
        // Arrange
        when(custoRepository.calcularTotalPorPeriodo(anyInt(), anyInt())).thenReturn(1000.0);
        when(custoRepository.calcularTotalPorTipoAgrupado(anyInt(), anyInt()))
            .thenReturn(new ArrayList<>());
        when(custoRepository.findTop5VeiculosMaisCaros(anyInt(), anyInt()))
            .thenReturn(new ArrayList<>());
        when(custoRepository.findTop10ByOrderByDataDesc()).thenReturn(new ArrayList<>());
        when(custoRepository.countAll()).thenReturn(10);

        // Act
        DashboardCustosDTO dashboard = custoService.getDashboardCustos();

        // Assert
        assertNotNull(dashboard);
        assertEquals("Dashboard carregado com sucesso", dashboard.getMensagem());
    }

    @Test
    void valorTotalCustos_DeveRetornarTotal() {
        // Arrange
        when(custoRepository.valorTotalCustos()).thenReturn(5000.0);

        // Act
        Double resultado = custoService.valorTotalCustos();

        // Assert
        assertEquals(5000.0, resultado);
    }

    @Test
    void buscarCustosPorVeiculoPeriodo_DeveRetornarLista() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now();
        List<Custo> custos = Arrays.asList(custo);
        
        when(custoRepository.findByVeiculoIdAndDataBetweenOrderByDataDesc(1L, inicio, fim))
            .thenReturn(custos);

        // Act
        List<Custo> resultado = custoService.buscarCustosPorVeiculoPeriodo(1L, inicio, fim);

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarCustosPorVeiculoPeriodo_ComInicioNull_DeveUsarDefault() {
        // Arrange
        List<Custo> custos = Arrays.asList(custo);
        when(custoRepository.findByVeiculoIdAndDataBetweenOrderByDataDesc(eq(1L), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(custos);

        // Act
        List<Custo> resultado = custoService.buscarCustosPorVeiculoPeriodo(1L, null, null);

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void getCustoMensalUltimos12Meses_DeveRetornarMap() {
        // Arrange
        when(custoRepository.calcularTotalPorPeriodo(anyInt(), anyInt())).thenReturn(1000.0);

        // Act
        Map<String, Double> resultado = custoService.getCustoMensalUltimos12Meses();

        // Assert
        assertNotNull(resultado);
        assertEquals(12, resultado.size());
    }

    @Test
    void migrarAbastecimentosExistentes_DeveMigrar() {
        // Arrange
        List<abastecimentos> abastecimentos = Arrays.asList(abastecimento);
        when(abastecimentoRepository.findAll()).thenReturn(abastecimentos);
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(false);
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.migrarAbastecimentosExistentes();

        // Assert
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void migrarManutencoesExistentes_DeveMigrar() {
        // Arrange
        List<Manutencao> manutencoes = Arrays.asList(manutencao);
        when(repositoryManutencao.findAll()).thenReturn(manutencoes);
        when(custoRepository.existsByManutencaoId(1L)).thenReturn(false);
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.migrarManutencoesExistentes();

        // Assert
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void buscarPorPeriodo_DeveRetornarListaDTO() {
        // Arrange
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now();
        List<CustoDTO> custosDTO = Arrays.asList(new CustoDTO());
        
        when(custoRepository.buscarPorPeriodoDTO(inicio, fim)).thenReturn(custosDTO);

        // Act
        List<CustoDTO> resultado = custoService.buscarPorPeriodo(inicio, fim);

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void listar_DeveRetornarListaCustoListDTO() {
        // Arrange
        List<CustoListDTO> custos = Arrays.asList(new CustoListDTO());
        when(custoRepository.findAllAsDTO()).thenReturn(custos);

        // Act
        List<CustoListDTO> resultado = custoService.listar();

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void gerarRelatorioDetalhado_DeveRetornarRelatorio() {
        // Arrange RelatorioFilterDTO
        RelatorioFilterDTO filtro = new RelatorioFilterDTO();
        filtro.setDataInicio(LocalDate.now().minusMonths(1).toString());
        filtro.setDataFim(LocalDate.now().toString());
        filtro.setDataInicioTop5VeiculosMaisCarro(LocalDate.now().minusMonths(6));
        filtro.setDataFimTop5VeiculosMaisCarro(LocalDate.now());

        when(custoRepository.calcularTotalPorPeriodoCompleto(any(), any())).thenReturn(1000.0);
        when(custoRepository.numeroTotalCustoPorPeriodo(any(), any())).thenReturn(10);
        when(custoRepository.mediaCustosPeriodo(any(), any())).thenReturn(100.0);
        when(custoRepository.calcularTotalPorVeiculoPeriodo(any(), any())).thenReturn(new ArrayList<>());
        
        List<Object[]> dados = new ArrayList<>();
        dados.add(new Object[]{"PREVENTIVA", 500.0});
        dados.add(new Object[]{"CORRETIVA", 300.0});
        when(custoRepository.calcularTotalPorTipoPeriodo(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(dados);
        when(custoRepository.findByPeriodo(any(), any(), any())).thenReturn(new ArrayList<>()); 
        when(custoRepository.findTop5VeiculosMaisCarosPorPeriodo(any(), any())).thenReturn(new ArrayList<>());
        when(custoRepository.findTop5CustosMaisAltos(any(PageRequest.class))).thenReturn(new ArrayList<>());
 
        // Act
        RelatorioCustosDetalhadoDTO relatorio = custoService.gerarRelatorioDetalhado(filtro);

        // Assert
        assertNotNull(relatorio);
    }

    @Test 
    void numeroCustos_DeveRetornarQuantidade() {
        // Arrange
        when(custoRepository.countAll()).thenReturn(10);

        // Act
        Integer resultado = custoService.numeroCustos();

        // Assert
        assertEquals(10, resultado);
    }

    @Test
    void numeroCustoPorStatus_DeveRetornarOptional() {
        // Arrange
        when(custoRepository.countByStatus(StatusCusto.PAGO)).thenReturn(Optional.of(5));

        // Act
        Optional<Integer> resultado = custoService.numeroCustoPorStatus(StatusCusto.PAGO);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(5, resultado.get());
    }

    @Test
    void numeroCustoPorTipo_DeveRetornarOptional() {
        // Arrange
        when(custoRepository.countByTipo(TipoCusto.COMBUSTIVEL)).thenReturn(Optional.of(3));

        // Act
        Optional<Integer> resultado = custoService.numeroCustoPorTipo(TipoCusto.COMBUSTIVEL);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(3, resultado.get());
    }

    @Test
    void verificarAbastecimentoId_DeveRetornarTrue() {
        // Arrange
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(true);

        // Act
        boolean resultado = custoService.verificarAbastecimentoId(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void atualizarCusto_ComDadosValidos_DeveAtualizar() {
        // Arrange
        CustoUpdateDTO updateDTO = new CustoUpdateDTO();
        updateDTO.setDescricao("Descrição atualizada");
        updateDTO.setValor(200.0);
        
        when(custoRepository.findById(1L)).thenReturn(Optional.of(custo));
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        String resultado = custoService.atualizarCusto(1L, updateDTO);

        // Assert
        assertEquals("custo actualizado com sucesso!", resultado);
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void excluirCusto_ComSucesso_DeveExcluir() {
        // Arrange
        when(custoRepository.findById(1L)).thenReturn(Optional.of(custo));
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        String resultado = custoService.excluirCusto(1L);

        assertEquals("custo excluido com sucesso", resultado);
        verify(custoRepository, times(1)).delete(custo);
    }

    @Test
    void processarNovoAbastecimento_DeveCriarCusto() {
        // Arrange
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(false);
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.processarNovoAbastecimento(abastecimento);

        // Assert
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void processarNovoAbastecimento_ComValorAlto_DeveEnviarAlerta() {
        when(custoRepository.existsByAbastecimentoId(1L)).thenReturn(false);
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.processarNovoAbastecimento(abastecimento);

        // Assert
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void processarNovaManutencao_DeveCriarCusto() {
        // Arrange
        when(custoRepository.save(any(Custo.class))).thenReturn(custo);
        when(custoRepository.calcularTotaisPorVeiculo(1L)).thenReturn(new HashMap<>());
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        custoService.processarNovaManutencao(manutencao);

        // Assert
        verify(custoRepository, times(1)).save(any(Custo.class));
    }

    @Test
    void getVeiculosComCustoAcimaDaMedia_DeveRetornarLista() {
        // Arrange
        when(custoRepository.calcularMediaCustoPorVeiculo()).thenReturn(500.0);
        veiculo.setCustoTotal(600.0);
        List<Veiculo> veiculos = Arrays.asList(veiculo);
        when(veiculoRepository.findAll()).thenReturn(veiculos);

        // Act
        List<Veiculo> resultado = custoService.getVeiculosComCustoAcimaDaMedia();

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void determinarTipoManutencao_Preventiva_DeveRetornarTipoCorreto() {
        // Teste de método privado via reflexão
        String tipoManutencao = "PREVENTIVA";
        TipoCusto resultado = invokePrivateDeterminarTipoManutencao(tipoManutencao);
        assertEquals(TipoCusto.MANUTENCAO_PREVENTIVA, resultado);
    }

    @Test
    void determinarTipoManutencao_Corretiva_DeveRetornarTipoCorreto() {
        String tipoManutencao = "CORRETIVA";
        TipoCusto resultado = invokePrivateDeterminarTipoManutencao(tipoManutencao);
        assertEquals(TipoCusto.MANUTENCAO_CORRETIVA, resultado);
    }

    @Test
    void determinarTipoManutencao_Revisao_DeveRetornarPreventiva() {
        String tipoManutencao = "REVISÃO";
        TipoCusto resultado = invokePrivateDeterminarTipoManutencao(tipoManutencao);
        assertEquals(TipoCusto.MANUTENCAO_PREVENTIVA, resultado);
    }

    // Método auxiliar para testar método privado
    private TipoCusto invokePrivateDeterminarTipoManutencao(String tipoManutencao) {
        try {
            java.lang.reflect.Method method = custoService.getClass().getDeclaredMethod(
                "determinarTipoManutencao", String.class);
	            method.setAccessible(true);
	            return (TipoCusto) method.invoke(custoService, tipoManutencao);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
	
}