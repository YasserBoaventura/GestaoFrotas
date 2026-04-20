	package com.GestaoRotas.GestaoRotas.Service;
	
	import org.junit.jupiter.api.BeforeEach;
	import org.junit.jupiter.api.Test;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.boot.test.context.SpringBootTest;
	import org.springframework.test.context.ActiveProfiles;
	
	import java.time.LocalDateTime;
	import java.util.*;
	
	import com.GestaoRotas.GestaoRotas.DTO.CancelarViagemRequest;
	import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
	import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
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
	
	import jakarta.transaction.Transactional;
	import static org.junit.jupiter.api.Assertions.*;
	import static org.mockito.ArgumentMatchers.*;
	import static org.mockito.Mockito.*;
	
	@SpringBootTest
	@ActiveProfiles("test")
	@Transactional
	public class travelServiceIntegration {
		
	    @Autowired
	    private ServiceViagem serviceViagem;
	
	    @Autowired
	    private RepositoryViagem repositoryViagem;
	
	    @Autowired
	    private RepositoryMotorista repositoryMotorista;
	
	    @Autowired
	    private RepositoryVeiculo repositoryVeiculo;
	
	    @Autowired 
	    private RepositoryRotas repositoryRotas;
	
	    private Motorista motorista;
	    private Veiculo veiculo;
	    private Rotas rota;
	    private ViagensDTO viagemDTO;
	    private Viagem viagem; 
	
	    @BeforeEach
	    void setUp() { 
	        // Criar Motorista
	motorista = new Motorista();
	motorista.setNome("João Silva");
	motorista.setEmail("joao@teste.com");
	motorista.setTelefone("11999999999");
	motorista.setNumeroCarta("12345678900");
	motorista.setCategoriaHabilitacao("B");
	motorista.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
	motorista.setStatus(statusMotorista.DISPONIVEL);
	motorista = repositoryMotorista.save(motorista);
	
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
	
	// Criar Rota
	rota = new Rotas();
	rota.setOrigem("São Paulo");
	rota.setDestino("Rio de Janeiro");
	rota.setDistanciaKm(430.0);
	rota.setTempoEstimadoHoras(6.0);
	rota = repositoryRotas.save(rota);
	
	// Setup DTO
	viagemDTO = new ViagensDTO();
	viagemDTO.setMotoristaId(motorista.getId());
	viagemDTO.setVeiculoId(veiculo.getId()); 
	viagemDTO.setRotaId(rota.getId());
	viagemDTO.setStatus("AGENDADA");
	viagemDTO.setDataHoraPartida(LocalDateTime.now().plusDays(1));
	viagemDTO.setKilometragemInicial(veiculo.getKilometragemAtual());
	viagemDTO.setTipoCarga(TipoCarga.GERAL);
	viagemDTO.setObservacoes("Viagem de teste");
	}
	
	@Test
	void salvar_DevePersistirViagemNoBanco() {
	    // Act
	String resultado = serviceViagem.salvar(viagemDTO);
	
	// Assert
	assertEquals("viagem salva com sucesso", resultado);
	
	List<Viagem> viagens = repositoryViagem.findAll();
	assertTrue(viagens.size() >= 1);
	
	Viagem viagemSalva = viagens.get(viagens.size() - 1);
	assertEquals(motorista.getId(), viagemSalva.getMotorista().getId());
	assertEquals(veiculo.getId(), viagemSalva.getVeiculo().getId());
	assertEquals(rota.getId(), viagemSalva.getRota().getId());
	assertEquals("AGENDADA", viagemSalva.getStatus());
	}
	
	@Test
	void update_DeveAtualizarViagemExistente() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId(); 
	
	ViagensDTO updateDTO = new ViagensDTO();
	updateDTO.setMotoristaId(motorista.getId());
	updateDTO.setVeiculoId(veiculo.getId());
	updateDTO.setRotaId(rota.getId());
	updateDTO.setStatus("EM_ANDAMENTO");
	updateDTO.setDataHoraPartida(LocalDateTime.now());
	updateDTO.setKilometragemInicial(veiculo.getKilometragemAtual());
	updateDTO.setTipoCarga(TipoCarga.GERAL);
	
	//Act       
	String resultado = serviceViagem.update(updateDTO, viagemId);
	
	// Assert
	assertEquals("viagem atualizada com sucesso!", resultado);
	
	Viagem viagemAtualizada = repositoryViagem.findById(viagemId).get();
	assertEquals("EM_ANDAMENTO", viagemAtualizada.getStatus());
	    assertEquals(TipoCarga.GERAL, viagemAtualizada.getTipoCarga()); 
	} 
	   
	@Test
	void iniciarViagem_DeveAtualizarStatusViagemMotoristaVeiculo() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	// Act
	Map<String, String> response = serviceViagem.iniciarViagem(viagemId);
	
	// Assert
	assertEquals("viagem inicializada com sucesso", response.get("message"));
	
	Viagem viagemIniciada = repositoryViagem.findById(viagemId).get();
	assertEquals("EM_ANDAMENTO", viagemIniciada.getStatus());
	assertNotNull(viagemIniciada.getDataHoraPartida());
	
	Motorista motoristaAtualizado = repositoryMotorista.findById(motorista.getId()).get();
	assertEquals(statusMotorista.EM_VIAGEM, motoristaAtualizado.getStatus());
	
	Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
	assertEquals("EM_VIAGEM", veiculoAtualizado.getStatus());
	}   
	
	@Test
	void concluirViagem_DeveFinalizarViagemEAtualizarStatus() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	// Iniciar viagem primeiro
	serviceViagem.iniciarViagem(viagemId);
	 
	ConcluirViagemRequest concluirRequest = new ConcluirViagemRequest();
	concluirRequest.setKilometragemFinal(veiculo.getKilometragemAtual() + 430.0);
	concluirRequest.setDataHoraChegada(LocalDateTime.now().plusHours(6));
	concluirRequest.setObservacoes("Chegada ao destino");
	  
	// Act
	Map<String, String> response = serviceViagem.ConcluirViagem(concluirRequest, viagemId);
	
	// Assert             
	assertEquals("viagem concluida com sucesso", response.get("sucesso"));
	
	Viagem viagemConcluida = repositoryViagem.findById(viagemId).get();
	assertEquals("CONCLUIDA", viagemConcluida.getStatus());
	assertEquals(concluirRequest.getKilometragemFinal(), viagemConcluida.getKilometragemFinal());
	
	Motorista motoristaAtualizado = repositoryMotorista.findById(motorista.getId()).get();
	assertEquals(statusMotorista.DISPONIVEL, motoristaAtualizado.getStatus());
	
	Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
	assertEquals("DISPONIVEL", veiculoAtualizado.getStatus());
	}
	@Test 
	void cancelarViagem_DeveCancelarEAtualizarStatus() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	CancelarViagemRequest cancelarRequest = new CancelarViagemRequest();
	cancelarRequest.setMotivo("Problemas logísticos");
	
	// Act
	Map<String, String> response = serviceViagem.cancelarViagem(cancelarRequest, viagemId);
	
	// Assert
	assertEquals("canselada com sucesso", response.get("sucesso"));
	
	Viagem viagemCancelada = repositoryViagem.findById(viagemId).get();
	assertEquals("CANCELADA", viagemCancelada.getStatus());
	assertTrue(viagemCancelada.getObservacoes().contains("Problemas logísticos"));
	
	Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
	assertEquals("DISPONIVEL", veiculoAtualizado.getStatus());
	}
	
	@Test
	void findAll_DeveRetornarTodasViagens() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	
	ViagensDTO viagemDTO2 = new ViagensDTO();
	viagemDTO2.setMotoristaId(motorista.getId());
	viagemDTO2.setVeiculoId(veiculo.getId());
	viagemDTO2.setRotaId(rota.getId());
	viagemDTO2.setStatus("AGENDADA");
	        viagemDTO2.setDataHoraPartida(LocalDateTime.now().plusDays(2));
	        viagemDTO2.setKilometragemInicial(veiculo.getKilometragemAtual());
	        viagemDTO2.setTipoCarga(TipoCarga.FRÁGIL);
	        serviceViagem.salvar(viagemDTO2);
	 
	        // Act
	List<Viagem> viagens = serviceViagem.findAll();
	
	// Assert
	    assertTrue(viagens.size() >= 2);
	}
	
	@Test
	void findById_DeveRetornarViagemCorreta() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	// Act
	Viagem viagemEncontrada = serviceViagem.findById(viagemId);
	
	// Assert
	    assertNotNull(viagemEncontrada);
	    assertEquals(viagemId, viagemEncontrada.getId());
	    assertEquals(motorista.getId(), viagemEncontrada.getMotorista().getId());
	}
	
	@Test
	void delete_DeveRemoverViagem() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	assertTrue(repositoryViagem.findById(viagemId).isPresent());
	
	// Act
	String resultado = serviceViagem.delete(viagemId);
	
	// Assert
	assertEquals("deletado com sucesso", resultado);
	    assertFalse(repositoryViagem.findById(viagemId).isPresent());
	}
	
	@Test
	void getContByStatus_DeveContarCorretamente() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	serviceViagem.salvar(viagemDTO);
	
	ViagensDTO viagemCancelada = new ViagensDTO();
	viagemCancelada.setMotoristaId(motorista.getId());
	viagemCancelada.setVeiculoId(veiculo.getId());
	viagemCancelada.setRotaId(rota.getId());
	viagemCancelada.setStatus("CANCELADA");
	viagemCancelada.setDataHoraPartida(LocalDateTime.now().plusDays(3));
	viagemCancelada.setKilometragemInicial(veiculo.getKilometragemAtual());
	viagemCancelada.setTipoCarga(TipoCarga.GERAL);
	serviceViagem.salvar(viagemCancelada);
	
	// Act
	Long quantidadeAgendadas = serviceViagem.getContByStatus("AGENDADA");
	Long quantidadeCanceladas = serviceViagem.getContByStatus("CANCELADA");
	
	// Assert
	    assertTrue(quantidadeAgendadas >= 2);
	    assertTrue(quantidadeCanceladas >= 1);
	}
	
	@Test
	void findByIdMotorista_DeveRetornarViagensDoMotorista() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	serviceViagem.salvar(viagemDTO);
	
	// Act
	List<Viagem> viagensMotorista = serviceViagem.findByIdMotorista(motorista.getId());
	
	// Assert
	    assertTrue(viagensMotorista.size() >= 2);
	    assertTrue(viagensMotorista.stream().allMatch(v -> 
	        v.getMotorista().getId().equals(motorista.getId())
	    ));
	}
	
	@Test
	void findByVeiculoId_DeveRetornarViagensDoVeiculo() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	serviceViagem.salvar(viagemDTO);
	
	// Act
	List<Viagem> viagensVeiculo = serviceViagem.findByVeiculoId(veiculo.getId());
	
	// Assert
	    assertTrue(viagensVeiculo.size() >= 2);
	    assertTrue(viagensVeiculo.stream().allMatch(v -> 
	        v.getVeiculo().getId().equals(veiculo.getId())
	    ));
	}
	@Test
	void iniciarViagem_ComVeiculoIndisponivel_DevePermitir() {
	    // Arrange
	veiculo.setStatus("EM_MANUTENCAO");
	repositoryVeiculo.save(veiculo);
	
	serviceViagem.salvar(viagemDTO);
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	
	// Act - O método iniciarViagem não valida veiculo, então deve funcionar
	Map<String, String> response = serviceViagem.iniciarViagem(viagemId);
	
	// Assert
	assertEquals("viagem inicializada com sucesso", response.get("message"));
	
	Veiculo veiculoAtualizado = repositoryVeiculo.findById(veiculo.getId()).get();
	assertEquals("EM_MANUTENCAO", veiculoAtualizado.getStatus());
	}
	
	@Test
	void salvar_ComMotoristaJaEmViagem_DeveBloquear() {
	    // Arrange
	
	motorista.setStatus(statusMotorista.EM_VIAGEM);
	repositoryMotorista.save(motorista);
	 
	// Act
	    String resultado = serviceViagem.salvar(viagemDTO);
	
	    // Assert 
	assertTrue(resultado.contains("Motorista não está disponível"));
	    
	    List<Viagem> viagens = repositoryViagem.findAll();
	    assertTrue(viagens.stream().noneMatch(v -> 
	        v.getMotorista().getId().equals(motorista.getId())
	    ));
	}
	
	@Test
	void relatorioPorMotorista_DeveRetornarDadosAgregados() {
	    // Arrange
	serviceViagem.salvar(viagemDTO);
	
	// Iniciar e concluir viagem para ter dados no relatório
	List<Viagem> viagens = repositoryViagem.findAll();
	Long viagemId = viagens.get(viagens.size() - 1).getId();
	serviceViagem.iniciarViagem(viagemId);
	
	ConcluirViagemRequest concluirRequest = new ConcluirViagemRequest();
	concluirRequest.setKilometragemFinal(veiculo.getKilometragemAtual() + 430.0);
	concluirRequest.setDataHoraChegada(LocalDateTime.now().plusHours(6));
	serviceViagem.ConcluirViagem(concluirRequest, viagemId);
	
	// Act
	List<RelatorioMotoristaDTO> relatorios = serviceViagem.relatorioPorMotorista();
	
	// Assert
	        assertNotNull(relatorios);
	    }
	}
