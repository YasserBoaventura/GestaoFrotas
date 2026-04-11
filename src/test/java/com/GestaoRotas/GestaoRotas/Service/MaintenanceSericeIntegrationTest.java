package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.*;
import java.time.*;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import jakarta.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class MaintenanceSericeIntegrationTest {

	    @Autowired
	    private ServiceManutencoes serviceManutencoes;

	    @Autowired
	    private RepositoryManutencao repositoryManutencao;

	    @Autowired
	    private RepositoryVeiculo repositoryVeiculo;

	    private Veiculo veiculo;
	    private manuntecaoDTO manutencaoDTO;

	    @BeforeEach
	    void setUp() {
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

        // Setup DTO
        manutencaoDTO = new manuntecaoDTO();
        manutencaoDTO.setVeiculo_id(veiculo.getId());
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
    void salvar_DevePersistirManutencaoNoBanco() {
        // Act
        String resultado = serviceManutencoes.salvar(manutencaoDTO);

        // Assert
        assertEquals("manutencao salva com sucesso", resultado);
        
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        assertTrue(manutencoes.size() >= 1);
        
        Manutencao manutencaoSalva = manutencoes.get(manutencoes.size() - 1);
        assertEquals(veiculo.getId(), manutencaoSalva.getVeiculo().getId());
        assertEquals("Troca de óleo", manutencaoSalva.getDescricao());
        assertEquals(500.0, manutencaoSalva.getCusto());
    }

    @Test
    void salvar_ComDataHoje_DeveAtualizarVeiculo() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now());

        // Act
        String resultado = serviceManutencoes.salvar(manutencaoDTO);

        // Assert
        assertEquals("manutencao salva com sucesso", resultado);
        
        Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
        assertEquals("MANUTENCAO_HOJE", veiculoAtualizado.getStatus());
    }

    @Test
    void update_DeveAtualizarManutencaoExistente() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();
        
        manuntecaoDTO updateDTO = new manuntecaoDTO();
        updateDTO.setVeiculo_id(veiculo.getId());
        updateDTO.setDataManutencao(LocalDate.now().plusDays(10));
        updateDTO.setDescricao("Troca de filtros");
        updateDTO.setTipoManutencao(TipoManutencao.CORRETIVA);
        updateDTO.setCusto(350.0);
        updateDTO.setKilometragemVeiculo(16000.0);
        updateDTO.setProximaManutencaoKm(25000.0);
        updateDTO.setProximaManutencaoData(LocalDate.now().plusMonths(4));
        updateDTO.setStatus(statusManutencao.AGENDADA);

        // Act
        String resultado = serviceManutencoes.update(updateDTO, manutencaoId);

        // Assert
        assertEquals("manutencao atualizada com sucesso", resultado);
        
        Manutencao manutencaoAtualizada = repositoryManutencao.findById(manutencaoId).get();
        assertEquals("Troca de filtros", manutencaoAtualizada.getDescricao());
        assertEquals("CORRETIVA", manutencaoAtualizada.getTipoManutencao().name());
        assertEquals(350.0, manutencaoAtualizada.getCusto());
    }

    @Test
    void iniciarManutencao_DeveAtualizarStatus() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();

        // Act
        Map<String, String> response = serviceManutencoes.iniciarManutencao(manutencaoId);

        // Assert
        assertEquals("Manutencao inicializada com sucesso", response.get("sucesso"));
        
        Manutencao manutencaoIniciada = repositoryManutencao.findById(manutencaoId).get();
        assertEquals(statusManutencao.EM_ANDAMENTO, manutencaoIniciada.getStatus());
        assertNotNull(manutencaoIniciada.getDataInicio());
        
        Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
        assertEquals("EM_MANUTENCAO", veiculoAtualizado.getStatus());
    }

    @Test
    void concluirManutencao_DeveFinalizarManutencao() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();
        
        serviceManutencoes.iniciarManutencao(manutencaoId);

        // Act
        Map<String, String> response = serviceManutencoes.concluirManutencao(manutencaoId, "Manutenção concluída com sucesso");

        // Assert
        assertEquals("sucesso", response.get("sucesso"));
        
        Manutencao manutencaoConcluida = repositoryManutencao.findById(manutencaoId).get();
        assertEquals(statusManutencao.CONCLUIDA, manutencaoConcluida.getStatus());
        assertNotNull(manutencaoConcluida.getDataConclusao());
        
        Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
        assertEquals("DISPONIVEL", veiculoAtualizado.getStatus());
    }

    @Test
    void cancelarManutencao_DeveCancelarManutencao() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();

        // Act
        Map<String, String> response = serviceManutencoes.cancelarManutencao(manutencaoId, "Problemas logísticos");

        // Assert
        assertEquals("Manutencao cancelada com sucesso", response.get("sucesso"));
        
        Manutencao manutencaoCancelada = repositoryManutencao.findById(manutencaoId).get();
        assertEquals(statusManutencao.CANCELADA, manutencaoCancelada.getStatus());
        assertTrue(manutencaoCancelada.getDescricao().contains("Problemas logísticos"));
    }

    @Test
    void findAll_DeveRetornarTodasManutencoes() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        
        manuntecaoDTO dto2 = new manuntecaoDTO();
        dto2.setVeiculo_id(veiculo.getId());
        dto2.setDataManutencao(LocalDate.now().plusDays(10));
        dto2.setDescricao("Alinhamento");
        dto2.setTipoManutencao(TipoManutencao.PREVENTIVA);
        dto2.setCusto(200.0);
        dto2.setKilometragemVeiculo(16000.0);
        serviceManutencoes.salvar(dto2);

        // Act
        List<Manutencao> manutencoes = serviceManutencoes.findAll();

        // Assert
        assertTrue(manutencoes.size() >= 2);
    }

    @Test
    void findById_DeveRetornarManutencaoCorreta() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();

        // Act
        Manutencao manutencaoEncontrada = serviceManutencoes.findById(manutencaoId);

        // Assert
        assertNotNull(manutencaoEncontrada);
        assertEquals(manutencaoId, manutencaoEncontrada.getId());
        assertEquals("Troca de óleo", manutencaoEncontrada.getDescricao());
    }

    @Test
    void deleteById_DeveRemoverManutencao() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
        Long manutencaoId = manutencoes.get(manutencoes.size() - 1).getId();
        
        assertTrue(repositoryManutencao.findById(manutencaoId).isPresent());

        // Act
        String resultado = serviceManutencoes.deleteById(manutencaoId);

        // Assert
        assertEquals("Manutenção deletada com sucesso", resultado);
        assertFalse(repositoryManutencao.findById(manutencaoId).isPresent());
    }

    @Test
    void listarPorVeiculo_DeveRetornarManutencoesDoVeiculo() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        List<Manutencao> manutencoes = serviceManutencoes.listarPorVeiculo(veiculo.getId());

        // Assert
        assertTrue(manutencoes.size() >= 2);
        assertTrue(manutencoes.stream().allMatch(m -> 
            m.getVeiculo().getId().equals(veiculo.getId())
        ));
    }

    @Test
    void listarPorTipo_DeveRetornarManutencoesPorTipo() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        
        manuntecaoDTO dto2 = new manuntecaoDTO();
        dto2.setVeiculo_id(veiculo.getId());
        dto2.setDataManutencao(LocalDate.now().plusDays(10));
        dto2.setDescricao("Troca de pastilhas");
        dto2.setTipoManutencao(TipoManutencao.PREVENTIVA);
        dto2.setCusto(300.0);
        dto2.setKilometragemVeiculo(16000.0);
        serviceManutencoes.salvar(dto2);
 
        // Act 
        List<Manutencao> preventivas = serviceManutencoes.listarPorTipo("PREVENTIVA");
        List<Manutencao> corretivas = serviceManutencoes.listarPorTipo("CORRETIVA");
        System.out.println("Yasser: "+preventivas.size()); 
        // Assert 
        assertTrue(preventivas.size() >= 1);
        assertTrue(corretivas.size() >= 1);
    }

    @Test
    void veiculoTemManutencaoHoje_ComManutencaoHoje_DeveRetornarTrue() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now());
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        boolean resultado = serviceManutencoes.veiculoTemManutencaoHoje(veiculo.getId());

        // Assert
        assertTrue(resultado);
    }

    @Test
    void veiculoTemManutencaoHoje_SemManutencaoHoje_DeveRetornarFalse() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now().plusDays(5));
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        boolean resultado = serviceManutencoes.veiculoTemManutencaoHoje(veiculo.getId());

        // Assert
        assertFalse(resultado);
    }

    @Test
    void getManutencoesHoje_DeveRetornarManutencoesDoDia() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now());
        serviceManutencoes.salvar(manutencaoDTO);
        
        manutencaoDTO.setDataManutencao(LocalDate.now().plusDays(1));
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        List<Manutencao> manutencoesHoje = serviceManutencoes.getManutencoesHoje();

        // Assert
        assertTrue(manutencoesHoje.size() >= 1);
        assertTrue(manutencoesHoje.stream().allMatch(m -> 
            m.getDataManutencao().equals(LocalDate.now())
        ));
    }

    @Test
    void gerarRelatorioPorVeiculo_DeveRetornarDadosAgregados() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        
        manuntecaoDTO dto2 = new manuntecaoDTO();
        dto2.setVeiculo_id(veiculo.getId());
        dto2.setDataManutencao(LocalDate.now().minusDays(10));
        dto2.setDescricao("Manutenção anterior");
        dto2.setTipoManutencao(TipoManutencao.PREVENTIVA);
        dto2.setCusto(400.0);
        dto2.setKilometragemVeiculo(14000.0);
        serviceManutencoes.salvar(dto2);

        // Act
        List<RelatorioManutencaoDTO> relatorios = serviceManutencoes.gerarRelatorioPorVeiculo();

        // Assert
        assertNotNull(relatorios);
    }

    @Test
    void relatorioPorPeriodo_DeveRetornarDadosDoPeriodo() {
        // Arrange
        serviceManutencoes.salvar(manutencaoDTO);
        
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now().plusMonths(1);

        // Act
        List<RelatorioManutencaoDTO> relatorios = serviceManutencoes.relatorioPorPeriodo(inicio, fim);

        // Assert
        assertNotNull(relatorios);
    }

    @Test
    void buscarVencidas_DeveRetornarManutencoesVencidas() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now().minusDays(5));
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        List<Manutencao> vencidas = serviceManutencoes.buscarVencidas();

        // Assert
        assertTrue(vencidas.size() >= 1);
        assertTrue(vencidas.stream().anyMatch(m -> 
            m.getDataManutencao().isBefore(LocalDate.now())
        ));
    }

    @Test
    void buscarProximas30Dias_DeveRetornarManutencoesProximas() {
        // Arrange
        manutencaoDTO.setDataManutencao(LocalDate.now().plusDays(15));
        serviceManutencoes.salvar(manutencaoDTO);

        // Act
        List<Manutencao> proximas = serviceManutencoes.buscarProximas30Dias();

        // Assert
	        assertTrue(proximas.size() >= 1);
	    }
	
	
	

}
