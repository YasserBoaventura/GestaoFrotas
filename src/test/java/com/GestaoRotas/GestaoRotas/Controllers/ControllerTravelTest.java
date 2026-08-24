package com.GestaoRotas.GestaoRotas.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.*;
import java.time.*;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.GestaoRotas.GestaoRotas.Controller.ControllerViagem;
import com.GestaoRotas.GestaoRotas.DTO.CancelarViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioGeralDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioTopMotoristasDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceViagem;

@SpringBootTest
public class ControllerTravelTest {
	

    @Autowired
    private ControllerViagem controllerViagem;

    @MockitoBean
    private ServiceViagem serviceViagem;

    @MockitoBean
    private RepositoryViagem repositoryViagem;

    @MockitoBean
    private RepositoryVeiculo repositoryVeiculo;

    @MockitoBean
    private RepositoryMotorista repositoryMotorista;

    private Viagem viagem;
    private ViagensDTO viagensDTO;
    private Veiculo veiculo;
    private Motorista motorista;
    private ConcluirViagemRequest concluirRequest;
    private CancelarViagemRequest cancelarRequest;
    private RelatorioMotoristaDTO relatorioMotoristaDTO;
    private RelatorioPorVeiculoDTO relatorioVeiculoDTO;
    private RelatorioGeralDTO relatorioGeralDTO;
    private RelatorioTopMotoristasDTO topMotoristaDTO;

    @BeforeEach 
    void setup() {
        // Setup Veiculo
        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setMatricula("ABC-1234");
        veiculo.setModelo("Toyota Corolla");
        veiculo.setKilometragemAtual(50000.0);

        // Setup Motorista
        motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("João Silva");
        motorista.setTelefone("11999999999");
        motorista.setStatus(statusMotorista.ATIVO);
        motorista.setNumeroCarta("123456789");

        Rotas rota = new Rotas();
        rota.setId(1L);
        rota.setOrigem("São Paulo");
        rota.setDestino("Rio de Janeiro");
        rota.setDistanciaKm(430.0);
        rota.setTempoEstimadoHoras(6.0);
        rota.setDescricao("Viagem SP -> RJ");

        // Setup Viagem
        viagem = new Viagem();
        viagem.setId(1L);
        viagem.setVeiculo(veiculo);
        viagem.setMotorista(motorista);
        viagem.setRota(rota); // ASSOCIAR A ROTA
        viagem.setDataHoraPartida(LocalDateTime.now());
        viagem.setStatus("AGENDADA");
 
        // Setup ViagensDTO
        viagensDTO = new ViagensDTO();
        viagensDTO.setVeiculoId(1L);
        viagensDTO.setMotoristaId(1L);
        viagensDTO.setStatus("São Paulo");
      
        viagensDTO.setDataHoraPartida(LocalDateTime.now().plusDays(1));
        viagensDTO.setKilometragemInicial(430.0);
 
        // Setup ConcluirViagemRequest
        concluirRequest = new ConcluirViagemRequest();
        concluirRequest.setKilometragemFinal(50430.0);
        concluirRequest.setObservacoes("Viagem concluída com sucesso");

        // Setup CancelarViagemRequest
        cancelarRequest = new CancelarViagemRequest();
        cancelarRequest.setMotivo("Cliente cancelou");
       

        // Setup RelatorioMotoristaDTO 
        relatorioMotoristaDTO = new RelatorioMotoristaDTO(
                "João Silva",
                "870464693",
                "ativo",// nomeMotorista
                10L,             // totalViagens
                4300.0,// totalQuilometragem
                860.0, // totalCombustivel
                200.0//media 
            );


        // Setup RelatorioPorVeiculoDTO 
        relatorioVeiculoDTO = new RelatorioPorVeiculoDTO(
                "ABC-1234",  
                "Modelo",// veiculo (placa)
                15L,             // totalViagens
                6450.0,          // totalKm
                1290.0,// totalCombustivel
                120.0//media
            ); 

        // Setup RelatorioGeralDTO
        relatorioGeralDTO = new RelatorioGeralDTO(
            115L,   // totalViagens
            8L,     // viagensConcluidas
            5L,     // viagensCanceladas
            10100.5, // kmPercorridos
            982.3,   // totalCombustivel
            87.8     // mediaConsumo
        );

        // Setup RelatorioTopMotoristasDTO
        topMotoristaDTO = new RelatorioTopMotoristasDTO(
            "João Silva",    // nomeMotorista
            20L,             // totalViagens
            1560.2           // totalKm
        );

        // Mocks para ServiceViagem
        when(serviceViagem.salvar(any(ViagensDTO.class))).thenReturn("Viagem criada com sucesso");
        when(serviceViagem.findAll()).thenReturn(Arrays.asList(viagem));
        when(serviceViagem.delete(1L)).thenReturn("Viagem deletada com sucesso");
        when(serviceViagem.delete(99L)).thenReturn("Viagem não encontrada");
        when(serviceViagem.findByIdMotorista(1L)).thenReturn(Arrays.asList(viagem));
        when(serviceViagem.findByIdMotorista(99L)).thenReturn(Collections.emptyList());
        when(serviceViagem.findByVeiculoId(1L)).thenReturn(Arrays.asList(viagem));
        when(serviceViagem.update(any(ViagensDTO.class), eq(1L))).thenReturn("Viagem atualizada com sucesso");
        
        Map<String, String> concluirResponse = new HashMap<>();
        concluirResponse.put("message", "Viagem concluída com sucesso");
        when(serviceViagem.ConcluirViagem(any(ConcluirViagemRequest.class), eq(1L))).thenReturn(concluirResponse);
        
        Map<String, String> cancelarResponse = new HashMap<>();
        cancelarResponse.put("message", "Viagem cancelada com sucesso");
        when(serviceViagem.cancelarViagem(any(CancelarViagemRequest.class), eq(1L))).thenReturn(cancelarResponse);
        
        Map<String, String> iniciarResponse = new HashMap<>();
        iniciarResponse.put("message", "Viagem iniciada com sucesso");
        when(serviceViagem.iniciarViagem(1L)).thenReturn(iniciarResponse);
        
        when(serviceViagem.getContByStatus("CONCLUIDA")).thenReturn(10L);
        when(serviceViagem.relatorioPorMotorista()).thenReturn(Arrays.asList(relatorioMotoristaDTO));
        when(serviceViagem.gerarRelatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioVeiculoDTO));
        when(serviceViagem.findById(1L)).thenReturn(viagem);
        
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(serviceViagem.relatorioPorMotoristaPeriodo(eq(inicio), eq(fim))).thenReturn(Arrays.asList(relatorioMotoristaDTO));
        when(serviceViagem.relatorioPorVeiculoPeriodo(eq(inicio), eq(fim))).thenReturn(Arrays.asList(relatorioVeiculoDTO));
    }

    // ==================== TESTES DE CADASTRO ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testCriarViagem_ComSucesso_RetornaOk() {
        when(serviceViagem.salvar(any(ViagensDTO.class))).thenReturn("Viagem criada com sucesso");
        
        ResponseEntity<String> response = controllerViagem.criarViagem(viagensDTO);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Viagem criada com sucesso", response.getBody());
        
        verify(serviceViagem, atLeastOnce()).salvar(any(ViagensDTO.class));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testCriarViagem_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceViagem.salvar(any(ViagensDTO.class)))
            .thenThrow(new RuntimeException("Erro ao salvar"));
        
        ResponseEntity<String> response = controllerViagem.criarViagem(viagensDTO);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ==================== TESTES DE BUSCA ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testFindAll_ComListaNaoVazia_RetornaLista() {
        when(serviceViagem.findAll()).thenReturn(Arrays.asList(viagem));
        
        ResponseEntity<List<Viagem>> response = controllerViagem.findAll();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        
        verify(serviceViagem, atLeastOnce()).findAll();
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testFindAll_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceViagem.findAll()).thenThrow(new RuntimeException("Erro ao buscar"));
        
        ResponseEntity<List<Viagem>> response = controllerViagem.findAll();
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
    
    @Test
    void testFindByIDMotorista_ComMotoristaExistente_RetornaLista() {
        when(serviceViagem.findByIdMotorista(1L)).thenReturn(Arrays.asList(viagem));
        
        ResponseEntity<List<Viagem>> response = controllerViagem.findByIDMotorista(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceViagem, atLeastOnce()).findByIdMotorista(1L);
    }
    
    @Test
    void testFindByIDMotorista_SemViagens_RetornaNoContent() {
        when(serviceViagem.findByIdMotorista(99L)).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Viagem>> response = controllerViagem.findByIDMotorista(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(serviceViagem, atLeastOnce()).findByIdMotorista(99L);
    }
    
    @Test
    void testFindByVeiculoId_ComVeiculoExistente_RetornaLista() {
        when(serviceViagem.findByVeiculoId(1L)).thenReturn(Arrays.asList(viagem));
        
        ResponseEntity<List<Viagem>> response = controllerViagem.findByVeiculoId(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(serviceViagem, atLeastOnce()).findByVeiculoId(1L);
    }
    
    @Test
    void testFindById_ComIdExistente_RetornaViagem() {
        when(serviceViagem.findById(1L)).thenReturn(viagem);
        
        ResponseEntity<Viagem> response = controllerViagem.findById(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        
        verify(serviceViagem, atLeastOnce()).findById(1L);
    }
    
    @Test
    void testFindById_ComIdInexistente_RetornaBadRequest() {
        when(serviceViagem.findById(99L))
            .thenThrow(new RuntimeException("Viagem não encontrada"));
        
        ResponseEntity<Viagem> response = controllerViagem.findById(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== TESTES DE UPDATE ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"}) 
    void testUpdate_ComSucesso_RetornaOk() {
        when(serviceViagem.update(any(ViagensDTO.class), eq(1L)))
            .thenReturn("Viagem atualizada com sucesso");
        
        ResponseEntity<String> response = controllerViagem.update(viagensDTO, 1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Viagem atualizada com sucesso", response.getBody());
        
        verify(serviceViagem, atLeastOnce()).update(any(ViagensDTO.class), eq(1L));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdate_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceViagem.update(any(ViagensDTO.class), eq(99L)))
            .thenThrow(new RuntimeException("Viagem não encontrada"));
        
        ResponseEntity<String> response = controllerViagem.update(viagensDTO, 99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Erro ao atualizar"));
    }

    // ==================== TESTES DE DELETE ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testExcluir_ComSucesso_RetornaOk() {
        when(serviceViagem.delete(1L)).thenReturn("Viagem deletada com sucesso");
        
        ResponseEntity<String> response = controllerViagem.excluir(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Viagem deletada com sucesso", response.getBody());
        
        verify(serviceViagem, atLeastOnce()).delete(1L);
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testExcluir_ViagemNaoEncontrada_RetornaNotFound() {
        when(serviceViagem.delete(99L)).thenReturn("Viagem não encontrada");
        
        ResponseEntity<String> response = controllerViagem.excluir(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Viagem não encontrada", response.getBody());
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testExcluir_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceViagem.delete(999L))
            .thenThrow(new RuntimeException("Erro ao deletar"));
        
        ResponseEntity<String> response = controllerViagem.excluir(999L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao deletar Viagem", response.getBody());
    }

    // ==================== TESTES DE MUDANÇA DE ESTADO ====================
    
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testConcluirViagem_ComSucesso_RetornaOk() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Viagem concluída com sucesso");
        when(serviceViagem.ConcluirViagem(any(ConcluirViagemRequest.class), eq(1L)))
            .thenReturn(responseMap);
        
        ResponseEntity<Map<String, String>> response = controllerViagem.ConcluirViagem(concluirRequest, 1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Viagem concluída com sucesso", response.getBody().get("message"));
        
        verify(serviceViagem, atLeastOnce()).ConcluirViagem(any(ConcluirViagemRequest.class), eq(1L));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testCancelarViagem_ComSucesso_RetornaOk() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Viagem cancelada com sucesso");
        when(serviceViagem.cancelarViagem(any(CancelarViagemRequest.class), eq(1L)))
            .thenReturn(responseMap);
        
        ResponseEntity<Map<String, String>> response = controllerViagem.cancelarViagem(cancelarRequest, 1L);
          
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Viagem cancelada com sucesso", response.getBody().get("message"));
        
        verify(serviceViagem, atLeastOnce()).cancelarViagem(any(CancelarViagemRequest.class), eq(1L));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testIniciarViagem_ComSucesso_RetornaOk() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Viagem iniciada com sucesso");
        when(serviceViagem.iniciarViagem(1L)).thenReturn(responseMap);
        
        ResponseEntity<Map<String, String>> response = controllerViagem.iniciarViagem(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Viagem iniciada com sucesso", response.getBody().get("message"));
        
        verify(serviceViagem, atLeastOnce()).iniciarViagem(1L);
    }

    // ==================== TESTES DE ESTATÍSTICAS ====================
    
    @Test
    void testCountByStatus_ComStatusExistente_RetornaQuantidade() {
        when(serviceViagem.getContByStatus("CONCLUIDA")).thenReturn(10L);
        
        ResponseEntity<Long> response = controllerViagem.countByStatus("CONCLUIDA");
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody());
        
        verify(serviceViagem, atLeastOnce()).getContByStatus("CONCLUIDA");
    }

    // ==================== TESTES DE RELATÓRIOS ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorMotorista_ComSucesso_RetornaLista() {
        when(serviceViagem.relatorioPorMotorista()).thenReturn(Arrays.asList(relatorioMotoristaDTO));
        
        ResponseEntity<List<RelatorioMotoristaDTO>> response = controllerViagem.relatorioPorMotorista();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).getNomeMotorista());
        assertEquals(10L, response.getBody().get(0).getTotalViagens());
        assertEquals(4300.0, response.getBody().get(0).getTotalQuilometragem());
        assertEquals(860.0, response.getBody().get(0).getTotalCombustivel());
        
        verify(serviceViagem, atLeastOnce()).relatorioPorMotorista();
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorVeiculo_ComSucesso_RetornaLista() {
        when(serviceViagem.gerarRelatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioVeiculoDTO));
        
        ResponseEntity<List<RelatorioPorVeiculoDTO>> response = controllerViagem.relatorioPorVeiculo();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC-1234", response.getBody().get(0).getVeiculo());
        assertEquals(15L, response.getBody().get(0).getTotalViagens());
        assertEquals(6450.0, response.getBody().get(0).getTotalKm());
        assertEquals(1290.0, response.getBody().get(0).getTotalCombustivel());
        
        verify(serviceViagem, atLeastOnce()).gerarRelatorioPorVeiculo();
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorPeriodoMotorista_ComDatasValidas_RetornaLista() {
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2024, 12, 31, 23, 59);
        
        when(serviceViagem.relatorioPorMotoristaPeriodo(eq(inicio), eq(fim)))
            .thenReturn(Arrays.asList(relatorioMotoristaDTO));
        
        ResponseEntity<List<RelatorioMotoristaDTO>> response = 
            controllerViagem.relatorioPorPeriodoMotorista(inicio, fim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceViagem, atLeastOnce()).relatorioPorMotoristaPeriodo(eq(inicio), eq(fim));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorPeriodoVeiculo_ComDatasValidas_RetornaLista() {
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime fim = LocalDateTime.of(2024, 12, 31, 23, 59);
        
        when(serviceViagem.relatorioPorVeiculoPeriodo(eq(inicio), eq(fim)))
            .thenReturn(Arrays.asList(relatorioVeiculoDTO));
        
        ResponseEntity<List<RelatorioPorVeiculoDTO>> response = 
            controllerViagem.relatorioPorPeriodoVeiculo(inicio, fim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceViagem, atLeastOnce()).relatorioPorVeiculoPeriodo(eq(inicio), eq(fim));
    }

    // ==================== TESTES DE RELATÓRIOS GERAIS ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioGeral_ComDatasValidas_RetornaRelatorio() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(repositoryViagem.relatorioGeralPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(relatorioGeralDTO);
        
        ResponseEntity<RelatorioGeralDTO> response = controllerViagem.relatorioGeral(dataInicio, dataFim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(115L, response.getBody().getTotalViagens());
        
        verify(repositoryViagem, atLeastOnce()).relatorioGeralPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioGeral_QuandoServiceLancaExcecao_RetornaDadosMock() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(repositoryViagem.relatorioGeralPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Erro no banco"));
        
        ResponseEntity<RelatorioGeralDTO> response = controllerViagem.relatorioGeral(dataInicio, dataFim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(115L, response.getBody().getTotalViagens());
    }

    // ==================== TESTES DE TOP MOTORISTAS ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testTopMotoristas_ComDatasValidas_RetornaLista() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        List<RelatorioTopMotoristasDTO> listaTop = Arrays.asList(topMotoristaDTO);
        when(repositoryViagem.findTopMotoristasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(listaTop);
        
        ResponseEntity<List<RelatorioTopMotoristasDTO>> response = 
            controllerViagem.topMotoristas(dataInicio, dataFim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).getNomeMotorista());
        assertEquals(20L, response.getBody().get(0).getTotalViagens());
        
        verify(repositoryViagem, atLeastOnce()).findTopMotoristasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testTopMotoristas_QuandoListaVazia_RetornaDadosMock() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(repositoryViagem.findTopMotoristasPorPeriodo(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        ResponseEntity<List<RelatorioTopMotoristasDTO>> response = 
            controllerViagem.topMotoristas(dataInicio, dataFim);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }
	
	
	
}
