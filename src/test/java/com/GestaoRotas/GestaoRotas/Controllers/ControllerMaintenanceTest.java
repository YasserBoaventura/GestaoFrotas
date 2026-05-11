package com.GestaoRotas.GestaoRotas.Controllers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.*; 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.GestaoRotas.GestaoRotas.Controller.ControllerManutencoes;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Service.ServiceManutencoes;

@SpringBootTest 
public class ControllerMaintenanceTest {

    @Autowired
    private ControllerManutencoes controllerManutencoes;

    @MockitoBean 
    private ServiceManutencoes serviceManutencoes;

    private Manutencao manutencao;
    private manuntecaoDTO manutencaoDTO;
    private Veiculo veiculo;
    private RelatorioManutencaoDTO relatorioDTO;

    @BeforeEach
    void setup() {
        // Setup Veiculo
        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setMatricula("ABC-1234");
        veiculo.setEmailResponsavel("teste@email.com");
        veiculo.setKilometragemAtual(50000.0);
        veiculo.setStatus("DISPONIVEL");

        // Setup Manutencao
        manutencao = new Manutencao();
        manutencao.setId(1L);
        manutencao.setTipoManutencao(TipoManutencao.PREVENTIVA);
        manutencao.setVeiculo(veiculo);
        manutencao.setDataManutencao(LocalDate.now().plusDays(15));
        manutencao.setDescricao("Troca de óleo");
        manutencao.setKilometragemVeiculo(50000.0);
        manutencao.setCusto(350.0);
        manutencao.setStatus(statusManutencao.AGENDADA);
        manutencao.setProximaManutencaoData(LocalDate.now().plusDays(45));
        manutencao.setProximaManutencaoKm(55000.0);

        // Setup ManutencaoDTO
        manutencaoDTO = new manuntecaoDTO();
        manutencaoDTO.setVeiculoId(1L);
        manutencaoDTO.setTipoManutencao(TipoManutencao.PREVENTIVA);
        manutencaoDTO.setDataManutencao(LocalDate.now().plusDays(15));
        manutencaoDTO.setDescricao("Troca de óleo");
        manutencaoDTO.setKilometragemVeiculo(50000.0);
        manutencaoDTO.setCusto(350.0);
        manutencaoDTO.setStatus(statusManutencao.AGENDADA);
        manutencaoDTO.setProximaManutencaoData(LocalDate.now().plusDays(45));
        manutencaoDTO.setProximaManutencaoKm(55000.0);

        // Setup RelatorioDTO
        relatorioDTO = new RelatorioManutencaoDTO(
            "ABC-1234",
            5L,
            1750.0,
            350.0,
            statusManutencao.CONCLUIDA
        );

        // Mock para findAll
        List<Manutencao> listaManutencoes = Arrays.asList(manutencao);
        when(serviceManutencoes.findAll()).thenReturn(listaManutencoes);

        when(serviceManutencoes.findById(1L)).thenReturn(manutencao);
        
    
        when(serviceManutencoes.deleteById(1L)).thenReturn("Manutenção deletada com sucesso");
          
        // Mock para listarPorVeiculo
        when(serviceManutencoes.listarPorVeiculo(1L)).thenReturn(Arrays.asList(manutencao));
        
        // Mock para listarPorTipo
        when(serviceManutencoes.listarPorTipo("PREVENTIVA")).thenReturn(Arrays.asList(manutencao));
 
        
        
        // Mock para gerarRelatorioPorVeiculo
        when(serviceManutencoes.gerarRelatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioDTO));
        
        // Mock para relatorioPorPeriodo
        when(serviceManutencoes.relatorioPorPeriodo(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(relatorioDTO));
        
        // Mock para gerarAlertas
        when(serviceManutencoes.gerarAlertas()).thenReturn(Arrays.asList("Alerta 1", "Alerta 2"));

         
        // Mock para gerarAlertasSimplificado
        when(serviceManutencoes.gerarAlertasSimplificado()).thenReturn(Arrays.asList("Alerta simplificado 1"));
        
        // Mock para buscarVencidas
        when(serviceManutencoes.buscarVencidas()).thenReturn(Arrays.asList(manutencao));
        
        // Mock para buscarProximas30Dias
        when(serviceManutencoes.buscarProximas30Dias()).thenReturn(Arrays.asList(manutencao));
        
        // Mock para buscarProximas7Dias
        when(serviceManutencoes.buscarProximas7Dias()).thenReturn(Arrays.asList(manutencao));
        
        // Mock para salvar
        when(serviceManutencoes.salvar(any(manuntecaoDTO.class)))
            .thenReturn("manutencao salva com sucesso");

        
        // Mock para update
        when(serviceManutencoes.update(any(manuntecaoDTO.class), eq(1L)))
            .thenReturn("manutencao atualizada com sucesso");
       
        
        // Mock para iniciarManutencao
        Map<String, String> iniciarResponse = new HashMap<>();
        iniciarResponse.put("sucesso", "Manutencao inicializada com sucesso");
        when(serviceManutencoes.iniciarManutencao(1L)).thenReturn(iniciarResponse);
    
        
        // Mock para concluirManutencao 
        Map<String, String> concluirResponse = new HashMap<>();
        concluirResponse.put("sucesso", "sucesso");
        when(serviceManutencoes.concluirManutencao(eq(1L), anyString())).thenReturn(concluirResponse);
    
        
        // Mock para cancelarManutencao
        Map<String, String> cancelarResponse = new HashMap<>();
        cancelarResponse.put("sucesso", "Manutencao cancelada com sucesso");
        when(serviceManutencoes.cancelarManutencao(eq(1L), anyString())).thenReturn(cancelarResponse);
    }
      //Teste Cadastro 
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testCadastrar_ComSucesso_RetornaOk() {
        // Reset para este teste específico
        when(serviceManutencoes.salvar(any(manuntecaoDTO.class)))
            .thenReturn("manutencao salva com sucesso");
        
        ResponseEntity<String> response = controllerManutencoes.cadastrar(manutencaoDTO);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("manutencao salva com sucesso", response.getBody());
        
        verify(serviceManutencoes, atLeastOnce()).salvar(any(manuntecaoDTO.class));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testCadastrar_QuandoServiceLancaExcecao_RetornaBadRequest() {
        // Reset para este teste específico
        when(serviceManutencoes.salvar(any(manuntecaoDTO.class)))
            .thenThrow(new RuntimeException("Erro ao salvar"));
        
        ResponseEntity<String> response = controllerManutencoes.cadastrar(manutencaoDTO);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== TESTES DE UPDATE ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdate_ComSucesso_RetornaOk() {
        when(serviceManutencoes.update(any(manuntecaoDTO.class), eq(1L)))
            .thenReturn("manutencao atualizada com sucesso");
        
        ResponseEntity<String> response = controllerManutencoes.update(1L, manutencaoDTO);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("manutencao atualizada com sucesso", response.getBody());
        
        verify(serviceManutencoes, atLeastOnce()).update(any(manuntecaoDTO.class), eq(1L));
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testUpdate_DeveRetorOK() {
   
        
        ResponseEntity<String> response = controllerManutencoes.update(99L, manutencaoDTO);
         
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }  

    // ==================== TESTES DE BUSCA ====================
    
    @Test
    void testListarPorVeiculo_ComVeiculoExistente_RetornaLista() {
        when(serviceManutencoes.listarPorVeiculo(1L)).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.listarPorVeiculo(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        
        verify(serviceManutencoes, atLeastOnce()).listarPorVeiculo(1L);
    }
    
    @Test
    void testListarPorVeiculo_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceManutencoes.listarPorVeiculo(99L))
            .thenThrow(new RuntimeException("Erro ao buscar"));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.listarPorVeiculo(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testFindAll_ComListaNaoVazia_RetornaLista() {
        when(serviceManutencoes.findAll()).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.findAll();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        
        verify(serviceManutencoes, atLeastOnce()).findAll();
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testFindAll_ComListaVazia_RetornaNoContent() {
        when(serviceManutencoes.findAll()).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.findAll();
        
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(serviceManutencoes, atLeastOnce()).findAll();
    }
     
    @Test 
    @WithMockUser(authorities = {"ADMIN"})
    void testFindAll_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceManutencoes.findAll())
            .thenThrow(new RuntimeException("Erro ao buscar"));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.findAll();
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
    
    @Test
    void testListarPorTipo_ComResultados_RetornaLista() {
        when(serviceManutencoes.listarPorTipo("PREVENTIVA")).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.listarPorTipo("PREVENTIVA");
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TipoManutencao.PREVENTIVA, response.getBody().get(0).getTipoManutencao());
        
        verify(serviceManutencoes, atLeastOnce()).listarPorTipo("PREVENTIVA");
    }
    
    @Test
    void testListarPorTipo_SemResultados_RetornaNoContent() {
        when(serviceManutencoes.listarPorTipo("CORRETIVA")).thenReturn(Collections.emptyList());
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.listarPorTipo("CORRETIVA");
        
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(serviceManutencoes, atLeastOnce()).listarPorTipo("CORRETIVA");
    }
    
    @Test
    void testListarPorTipo_ComTipoInvalido_RetornaBadRequest() {
        when(serviceManutencoes.listarPorTipo("INVALIDO"))
            .thenThrow(new IllegalArgumentException("Tipo inválido"));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.listarPorTipo("INVALIDO");
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testFindById_ComIdExistente_RetornaManutencao() {
        when(serviceManutencoes.findById(1L)).thenReturn(manutencao);
        
        ResponseEntity<Manutencao> response = controllerManutencoes.findById(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        
        verify(serviceManutencoes, atLeastOnce()).findById(1L);
    }
    
    @Test
    void testFindById_ComIdInexistente_RetornaBadRequest() {
        when(serviceManutencoes.findById(99L))
            .thenThrow(new RuntimeException("Manutenção não encontrada"));
        
        ResponseEntity<Manutencao> response = controllerManutencoes.findById(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ==================== TESTES DE DELETE ====================
    
    @Test 
    @WithMockUser(authorities = {"ADMIN"})
    void testExcluir_ComSucesso_RetornaOk() {
        when(serviceManutencoes.deleteById(1L)).thenReturn("Manutenção deletada com sucesso");
        
        ResponseEntity<String> response = controllerManutencoes.excluir(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Manutenção deletada com sucesso", response.getBody());
        
        verify(serviceManutencoes, atLeastOnce()).deleteById(1L);
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testExcluir_ManutencaoNaoEncontrada_RetornaNotFound() {
        when(serviceManutencoes.deleteById(99L)).thenReturn("Manutenção não encontrada");
        
        ResponseEntity<String> response = controllerManutencoes.excluir(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Manutenção não encontrada", response.getBody());
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"}) 
    void testExcluir_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceManutencoes.deleteById(999L))
            .thenThrow(new RuntimeException("Erro ao deletar"));
        
        ResponseEntity<String> response = controllerManutencoes.excluir(999L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao deletar manutenção", response.getBody());
    }

    // ==================== TESTES DE MUDANÇA DE ESTADO ====================
    
    @Test
    void testIniciarManutencao_ComSucesso_RetornaCreated() {
        Map<String, String> iniciarResponse = new HashMap<>();
        iniciarResponse.put("sucesso", "Manutencao inicializada com sucesso");
        when(serviceManutencoes.iniciarManutencao(1L)).thenReturn(iniciarResponse);
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.iniciarManutencao(1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Manutencao inicializada com sucesso", response.getBody().get("sucesso"));
        
        verify(serviceManutencoes, atLeastOnce()).iniciarManutencao(1L);
    }
    
    @Test
    void testIniciarManutencao_ComErro_RetornaBadRequest() {
        when(serviceManutencoes.iniciarManutencao(99L))
            .thenThrow(new RuntimeException("Manutenção não encontrada"));
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.iniciarManutencao(99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("erro ao inicializar a manutencao", response.getBody().get("message"));
    }
    
    @Test
    void testConcluirManutencao_ComSucesso_RetornaOk() {
        Map<String, String> concluirResponse = new HashMap<>();
        concluirResponse.put("sucesso", "sucesso");
        when(serviceManutencoes.concluirManutencao(eq(1L), anyString())).thenReturn(concluirResponse);
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.concluirManutencao("Manutenção concluída com sucesso", 1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("sucesso", response.getBody().get("sucesso"));
        
        verify(serviceManutencoes, atLeastOnce()).concluirManutencao(eq(1L), anyString());
    }
    
    @Test
    void testConcluirManutencao_ComErro_RetornaBadRequest() {
        when(serviceManutencoes.concluirManutencao(eq(99L), anyString()))
            .thenThrow(new RuntimeException("Erro ao concluir"));
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.concluirManutencao("Observações", 99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("erro ao concluir manutencao", response.getBody().get("erro"));
    }
    
    @Test
    void testCancelarManutencao_ComSucesso_RetornaOk() {
        Map<String, String> cancelarResponse = new HashMap<>();
        cancelarResponse.put("sucesso", "Manutencao cancelada com sucesso");
        when(serviceManutencoes.cancelarManutencao(eq(1L), anyString())).thenReturn(cancelarResponse);
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.cancelarManutencao("Motivo do cancelamento", 1L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Manutencao cancelada com sucesso", response.getBody().get("sucesso"));
        
        verify(serviceManutencoes, atLeastOnce()).cancelarManutencao(eq(1L), anyString());
    }
    
    @Test
    void testCancelarManutencao_ComIllegalArgumentException_RetornaBadRequest() {
        when(serviceManutencoes.cancelarManutencao(eq(99L), anyString()))
            .thenThrow(new IllegalArgumentException("Manutenção não pode ser cancelada"));
        
        ResponseEntity<Map<String, String>> response = controllerManutencoes.cancelarManutencao("Motivo", 99L);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ==================== TESTES DE RELATÓRIOS ====================
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorVeiculo_ComSucesso_RetornaLista() {
        when(serviceManutencoes.gerarRelatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioDTO));
        
        ResponseEntity<List<RelatorioManutencaoDTO>> response = controllerManutencoes.relatorioPorVeiculo();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC-1234", response.getBody().get(0).getVeiculo());
        
        verify(serviceManutencoes, atLeastOnce()).gerarRelatorioPorVeiculo();
    }
    
    @Test 
    @WithMockUser(authorities = {"ADMIN"})
    void testRelatorioPorPeriodo_ComDatasValidas_RetornaLista() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fim = LocalDate.of(2025, 12, 31);
        
        when(serviceManutencoes.relatorioPorPeriodo(eq(inicio), eq(fim))).thenReturn(Arrays.asList(relatorioDTO));
        
        ResponseEntity<List<RelatorioManutencaoDTO>> response = controllerManutencoes.relatorioPorPeriodo(inicio, fim);
          
        assertNotNull(response); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceManutencoes, atLeastOnce()).relatorioPorPeriodo(eq(inicio), eq(fim));
    }

    // ==================== TESTES DE ALERTAS ====================
    
    @Test
   @WithMockUser(authorities = {"ADMIN"})
    void testGetAlertas_ComSucesso_RetornaListaAlertas() {
        when(serviceManutencoes.gerarAlertas()).thenReturn(Arrays.asList("Alerta 1", "Alerta 2"));
         
        ResponseEntity<List<String>> response = controllerManutencoes.getAlertas();
         
        assertNotNull(response); 
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        verify(serviceManutencoes, atLeastOnce()).gerarAlertas();
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetAlertas_QuandoServiceLancaExcecao_RetornaBadRequest() {
        when(serviceManutencoes.gerarAlertas())
            .thenThrow(new RuntimeException("Erro ao gerar alertas"));
          
        ResponseEntity<List<String>> response = controllerManutencoes.getAlertas();
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testGetAlertasSimplificado_ComSucesso_RetornaLista() {
        when(serviceManutencoes.gerarAlertasSimplificado()).thenReturn(Arrays.asList("Alerta simplificado 1"));
        
        ResponseEntity<List<String>> response = controllerManutencoes.getAlertasSimplificado();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceManutencoes, atLeastOnce()).gerarAlertasSimplificado();
    }

    // ==================== TESTES DE CONSULTAS ESPECÍFICAS ====================
    
    @Test
    void testVencidas_ComSucesso_RetornaLista() {
        when(serviceManutencoes.buscarVencidas()).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.vencidas();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        
        verify(serviceManutencoes, atLeastOnce()).buscarVencidas();
    }
    
    @Test
    void testProximas30Dias_ComSucesso_RetornaLista() {
        when(serviceManutencoes.buscarProximas30Dias()).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.proximas30Dias();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceManutencoes, atLeastOnce()).buscarProximas30Dias();
    }
    
    @Test
    void testProximas7Dias_ComSucesso_RetornaLista() {
        when(serviceManutencoes.buscarProximas7Dias()).thenReturn(Arrays.asList(manutencao));
        
        ResponseEntity<List<Manutencao>> response = controllerManutencoes.proximas7Dias();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(serviceManutencoes, atLeastOnce()).buscarProximas7Dias();
    }

}
