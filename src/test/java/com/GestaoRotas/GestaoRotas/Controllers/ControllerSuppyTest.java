package com.GestaoRotas.GestaoRotas.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.GestaoRotas.GestaoRotas.Controller.ControllerAbastecimentos;
import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Service.ServiceAbastecimentos;

@SpringBootTest 
public class ControllerSuppyTest {
	
	   @Autowired
	    private ControllerAbastecimentos controllerAbastecimentos;

	    @MockitoBean
	    private ServiceAbastecimentos abastecimentosService;

	    private abastecimentos abastecimento;
	    private AbastecimentoDTO abastecimentoDTO;
	    private RelatorioCombustivelDTO relatorioDTO;
	    private Veiculo veiculo;

	    @BeforeEach
	    void setup() {
	        // Setup Veiculo
    veiculo = new Veiculo();
    veiculo.setId(1L);
    veiculo.setMatricula("ABC-1234");
    veiculo.setKilometragemAtual(50000.0);
    veiculo.setCapacidadeTanque(50.0);

    // Setup abastecimentos
	        abastecimento = new abastecimentos();
	        abastecimento.setId(1L);
	        abastecimento.setVeiculo(veiculo);
	        abastecimento.setDataAbastecimento(LocalDate.now());
	        abastecimento.setQuantidadeLitros(40.0);
	        abastecimento.setKilometragemVeiculo(50000.0);
	        abastecimento.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
 
	        // Setup AbastecimentoDTO
    abastecimentoDTO = new AbastecimentoDTO();
    abastecimentoDTO.setVeiculoId(1L);
    abastecimentoDTO.setDataAbastecimento(LocalDate.now());
    abastecimento.setQuantidadeLitros(40.0);

    abastecimento.setKilometragemVeiculo(50000.0);
    abastecimentoDTO.setTipoCombustivel("GASOLINA");
	        abastecimentoDTO.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
 
	        // Setup RelatorioCombustivelDTO - Usando o construtor correto com 6 parâmetros
    relatorioDTO = new RelatorioCombustivelDTO(
        "ABC-1234",                    // matricula
        40.0,                          // totalLitros
        200.0,                         // valorTotal
        5.0,                           // precoMedio (200/40 = 5)
        12.5,                          // MediaPorLitro 
        statusAbastecimentos.REALIZADA // status
    );

    // Mock para save
    Map<String, String> saveResponse = new HashMap<>();
    saveResponse.put("message", "Abastecimento salvo com sucesso");
    when(abastecimentosService.save(any(AbastecimentoDTO.class))).thenReturn(saveResponse);
    
    // Mock para update
    when(abastecimentosService.update(any(AbastecimentoDTO.class), eq(1L)))
        .thenReturn("Abastecimento atualizado com sucesso");
    when(abastecimentosService.update(any(AbastecimentoDTO.class), eq(99L)))
        .thenThrow(new RuntimeException("Abastecimento não encontrado"));
    
    // Mock para findAll
    List<abastecimentos> listaAbastecimentos = Arrays.asList(abastecimento);
    when(abastecimentosService.findAll()).thenReturn(listaAbastecimentos);
    
    // Mock para findById
    when(abastecimentosService.findById(1L)).thenReturn(abastecimento);
   
    
    // Mock para deleteById
    when(abastecimentosService.deletar(1L)).thenReturn("Abastecimento deletado com sucesso");
    
    
    // Mock para relatorioPorVeiculo
    when(abastecimentosService.relatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioDTO));
    
    // Mock para relatorioPorPeriodo
    when(abastecimentosService.relatorioPorPeriodo(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(Arrays.asList(relatorioDTO));
    
    // Mock para numeroAbastecimentoRealizados
    when(abastecimentosService.numeroAbastecimentoRealizados()).thenReturn(10L);
    
    // Mock para numeroAbastecimentoPlaneado
    when(abastecimentosService.numeroAbastecimentoPlaneado()).thenReturn(Optional.of(5L));
    
    // Mock para numeroAbastecimentoCancelados
    when(abastecimentosService.numeroAbastecimentoCancelados()).thenReturn(Optional.of(2L));
}

// teste cadastro

@Test
@WithMockUser(authorities = {"ADMIN"})
void testSalvar_ComSucesso_RetornaOk() {
    Map<String, String> saveResponse = new HashMap<>();
    saveResponse.put("message", "Abastecimento salvo com sucesso");
    when(abastecimentosService.save(any(AbastecimentoDTO.class))).thenReturn(saveResponse);
    
    ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(abastecimentoDTO);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Abastecimento salvo com sucesso", response.getBody().get("message"));
    
    verify(abastecimentosService, atLeastOnce()).save(any(AbastecimentoDTO.class));
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testSalvar_QuandoServiceLancaExcecao_RetornaBadRequest() {
    when(abastecimentosService.save(any(AbastecimentoDTO.class)))
        .thenThrow(new RuntimeException("Erro ao salvar"));
    
    ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(abastecimentoDTO);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

//teste update 

@Test
@WithMockUser(authorities = {"ADMIN"})
void testUpdate_ComSucesso_RetornaOk() {
    when(abastecimentosService.update(any(AbastecimentoDTO.class), eq(1L)))
        .thenReturn("Abastecimento atualizado com sucesso");
    
    ResponseEntity<String> response = controllerAbastecimentos.update(1L, abastecimentoDTO);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Abastecimento atualizado com sucesso", response.getBody());
    
    verify(abastecimentosService, atLeastOnce()).update(any(AbastecimentoDTO.class), eq(1L));
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testUpdate_QuandoServiceLancaExcecao_RetornaBadRequest() {
    when(abastecimentosService.update(any(AbastecimentoDTO.class), eq(99L)))
        .thenThrow(new RuntimeException("Abastecimento não encontrado"));
    
    ResponseEntity<String> response = controllerAbastecimentos.update(99L, abastecimentoDTO);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().contains("erro ao actualizar abastecimento"));
}

// teste busca
@Test
@WithMockUser(authorities = {"ADMIN"})
void testFindAll_ComListaNaoVazia_RetornaLista() {
    List<abastecimentos> lista = Arrays.asList(abastecimento);
    when(abastecimentosService.findAll()).thenReturn(lista);
    
    ResponseEntity<List<abastecimentos>> response = controllerAbastecimentos.findAll();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(1L, response.getBody().get(0).getId());
    
    verify(abastecimentosService, atLeastOnce()).findAll();
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testFindAll_QuandoServiceLancaExcecao_RetornaBadRequest() {
    when(abastecimentosService.findAll())
        .thenThrow(new RuntimeException("Erro ao buscar"));
    
    ResponseEntity<List<abastecimentos>> response = controllerAbastecimentos.findAll();
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testFindById_ComIdExistente_RetornaAbastecimento() {
    when(abastecimentosService.findById(1L)).thenReturn(abastecimento);
    
    ResponseEntity<abastecimentos> response = controllerAbastecimentos.findById(1L);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
    
    verify(abastecimentosService, atLeastOnce()).findById(1L);
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testFindById_ComIdInexistente_RetornaBadRequest() {
    when(abastecimentosService.findById(99L))
        .thenThrow(new RuntimeException("Abastecimento não encontrado"));
    
    ResponseEntity<abastecimentos> response = controllerAbastecimentos.findById(99L);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNull(response.getBody());
}

// teste delete

@Test
@WithMockUser(authorities = {"ADMIN"})
void testDeleteById_ComSucesso_RetornaOk() {
    when(abastecimentosService.deletar(1L)).thenReturn("Abastecimento deletado com sucesso");
    
    ResponseEntity<String> response = controllerAbastecimentos.deleteById(1L);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Abastecimento deletado com sucesso", response.getBody());
    
    verify(abastecimentosService, atLeastOnce()).deletar(1L);
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testDeleteById_QuandoServiceLancaExcecao_RetornaBadRequest() {
    when(abastecimentosService.deletar(99L))
        .thenThrow(new RuntimeException("Erro ao deletar"));
    
    ResponseEntity<String> response = controllerAbastecimentos.deleteById(99L);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

// testes relatorios

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorVeiculo_ComSucesso_RetornaLista() {
    List<RelatorioCombustivelDTO> relatorios = Arrays.asList(relatorioDTO);
    when(abastecimentosService.relatorioPorVeiculo()).thenReturn(relatorios);
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorVeiculo();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    
    // Verificando os campos do DTO
    RelatorioCombustivelDTO dto = response.getBody().get(0);
    assertEquals("ABC-1234", dto.getMatricula());
    assertEquals(40.0, dto.getTotalLitros());
    assertEquals(200.0, dto.getValorTotal());
    assertEquals(5.0, dto.getPrecoMedio());
    assertEquals(12.5, dto.getMediaPorLitro());
    assertEquals(statusAbastecimentos.REALIZADA, dto.getStatus());
    
    verify(abastecimentosService, atLeastOnce()).relatorioPorVeiculo();
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorVeiculo_ComListaVazia_RetornaListaVazia() {
    when(abastecimentosService.relatorioPorVeiculo()).thenReturn(Collections.emptyList());
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorVeiculo();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorPeriodo_ComDatasValidas_RetornaLista() {
    LocalDate inicio = LocalDate.of(2024, 1, 1);
    LocalDate fim = LocalDate.of(2024, 12, 31);
    
    List<RelatorioCombustivelDTO> relatorios = Arrays.asList(relatorioDTO);
    when(abastecimentosService.relatorioPorPeriodo(eq(inicio), eq(fim))).thenReturn(relatorios);
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorPeriodo(inicio, fim);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    
    verify(abastecimentosService, atLeastOnce()).relatorioPorPeriodo(eq(inicio), eq(fim));
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorPeriodo_ComDatasInvalidas_RetornaListaVazia() {
    LocalDate inicio = LocalDate.of(2024, 12, 31);
    LocalDate fim = LocalDate.of(2024, 1, 1);
    
    when(abastecimentosService.relatorioPorPeriodo(eq(inicio), eq(fim)))
        .thenReturn(Collections.emptyList());
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorPeriodo(inicio, fim);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorPeriodo_ComPeriodoDeUmAno_RetornaLista() {
    LocalDate inicio = LocalDate.now().minusYears(1);
    LocalDate fim = LocalDate.now();
    
    List<RelatorioCombustivelDTO> relatorios = Arrays.asList(relatorioDTO);
    when(abastecimentosService.relatorioPorPeriodo(eq(inicio), eq(fim))).thenReturn(relatorios);
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorPeriodo(inicio, fim);
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    
    verify(abastecimentosService, atLeastOnce()).relatorioPorPeriodo(eq(inicio), eq(fim));
}

// ==================== TESTES DE ESTATÍSTICAS ====================

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosRealizados_ComSucesso_RetornaQuantidade() {
    when(abastecimentosService.numeroAbastecimentoRealizados()).thenReturn(10L);
    
    ResponseEntity<Long> response = controllerAbastecimentos.abastecimentosRealizados();
     
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(10L, response.getBody());
    
    verify(abastecimentosService, atLeastOnce()).numeroAbastecimentoRealizados();
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosRealizados_QuandoZero_RetornaZero() {
    when(abastecimentosService.numeroAbastecimentoRealizados()).thenReturn(0L);
    
    ResponseEntity<Long> response = controllerAbastecimentos.abastecimentosRealizados();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0L, response.getBody());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosPlaneados_ComSucesso_RetornaQuantidade() {
    when(abastecimentosService.numeroAbastecimentoPlaneado()).thenReturn(Optional.of(5L));
    
    ResponseEntity<Optional<Long>> response = controllerAbastecimentos.abastecimentosPlaneados();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isPresent());
    assertEquals(5L, response.getBody().get());
    
    verify(abastecimentosService, atLeastOnce()).numeroAbastecimentoPlaneado();
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosPlaneados_QuandoVazio_RetornaOptionalVazio() {
    when(abastecimentosService.numeroAbastecimentoPlaneado()).thenReturn(Optional.empty());
    
    ResponseEntity<Optional<Long>> response = controllerAbastecimentos.abastecimentosPlaneados();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosCancelados_ComSucesso_RetornaQuantidade() {
    when(abastecimentosService.numeroAbastecimentoCancelados()).thenReturn(Optional.of(2L));
    
    ResponseEntity<Optional<Long>> response = controllerAbastecimentos.abastecimentosCancelados();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isPresent());
    assertEquals(2L, response.getBody().get());
    
    verify(abastecimentosService, atLeastOnce()).numeroAbastecimentoCancelados();
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testAbastecimentosCancelados_QuandoVazio_RetornaOptionalVazio() {
    when(abastecimentosService.numeroAbastecimentoCancelados()).thenReturn(Optional.empty());
    
    ResponseEntity<Optional<Long>> response = controllerAbastecimentos.abastecimentosCancelados();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isEmpty());
}

// teste de validacao

@Test
@WithMockUser(authorities = {"ADMIN"})
void testSalvar_ComLitrosNegativos_RetornaBadRequest() {
    AbastecimentoDTO dtoInvalido = new AbastecimentoDTO();
    dtoInvalido.setVeiculoId(1L);
    dtoInvalido.setQuantidadeLitros(-10.0);
     
    when(abastecimentosService.save(any(AbastecimentoDTO.class)))
        .thenThrow(new RuntimeException("Litros não pode ser negativo"));
    
    ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(dtoInvalido);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testSalvar_ComVeiculoInexistente_RetornaBadRequest() {
    AbastecimentoDTO dto = abastecimentoDTO;
    dto.setVeiculoId(999L);
    
    when(abastecimentosService.save(any(AbastecimentoDTO.class)))
        .thenThrow(new RuntimeException("Veículo não encontrado"));
        
        ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(dto);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testSalvar_ComValorTotalNegativo_RetornaBadRequest() {
        AbastecimentoDTO dtoInvalido = new AbastecimentoDTO();
        dtoInvalido.setVeiculoId(1L);
        dtoInvalido.setQuantidadeLitros(40.0);
 
        
        when(abastecimentosService.save(any(AbastecimentoDTO.class)))
            .thenThrow(new RuntimeException("Valor total não pode ser negativo"));
    
    ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(dtoInvalido);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testSalvar_ComDataFutura_RetornaBadRequest() {
    AbastecimentoDTO dtoInvalido = new AbastecimentoDTO();
    dtoInvalido.setVeiculoId(1L);
    dtoInvalido.setDataAbastecimento(LocalDate.now().plusDays(1));
    
    when(abastecimentosService.save(any(AbastecimentoDTO.class)))
        .thenThrow(new RuntimeException("Data não pode ser futura"));
    
    ResponseEntity<Map<String, String>> response = controllerAbastecimentos.salvar(dtoInvalido);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

@Test
@WithMockUser(authorities = {"ADMIN"})
void testUpdate_ComIdInvalido_RetornaBadRequest() {
    when(abastecimentosService.update(any(AbastecimentoDTO.class), eq(-1L)))
        .thenThrow(new RuntimeException("ID inválido"));
    
    ResponseEntity<String> response = controllerAbastecimentos.update(-1L, abastecimentoDTO);
    
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}

// ==================== TESTES DE PERFORMANCE ====================

@Test
@WithMockUser(authorities = {"ADMIN"})
void testFindAll_ComMuitosRegistros_RetornaLista() {
    List<abastecimentos> listaGrande = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
        abastecimentos a = new abastecimentos();
        a.setId((long) i);
        listaGrande.add(a);
    }
    when(abastecimentosService.findAll()).thenReturn(listaGrande);
    
    ResponseEntity<List<abastecimentos>> response = controllerAbastecimentos.findAll();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(100, response.getBody().size());
}

// ==================== TESTES DE RELATÓRIO COM DADOS ESPECÍFICOS ====================

@Test
@WithMockUser(authorities = {"ADMIN"})
void testRelatorioPorVeiculo_VerificarCamposCalculados() {
    // Criando um relatório com valores específicos
    RelatorioCombustivelDTO relatorioCompleto = new RelatorioCombustivelDTO(
        "XYZ-9876",                    // matricula
        100.0,                         // totalLitros
        600.0,                         // valorTotal
        6.0,                           // precoMedio
        15.5,                          // MediaPorLitro
        statusAbastecimentos.PLANEADA  // status
    );
    
    when(abastecimentosService.relatorioPorVeiculo()).thenReturn(Arrays.asList(relatorioCompleto));
    
    ResponseEntity<List<RelatorioCombustivelDTO>> response = controllerAbastecimentos.relatorioPorVeiculo();
    
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    
    RelatorioCombustivelDTO dto = response.getBody().get(0);
    assertEquals("XYZ-9876", dto.getMatricula());
	        assertEquals(100.0, dto.getTotalLitros());
	        assertEquals(600.0, dto.getValorTotal());
	        assertEquals(6.0, dto.getPrecoMedio());
	        assertEquals(15.5, dto.getMediaPorLitro());
	        assertEquals(statusAbastecimentos.PLANEADA, dto.getStatus());
	    }
}