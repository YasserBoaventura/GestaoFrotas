package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.*;
import java.util.*; 
import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Tracking.TrackingService;
import com.GestaoRotas.GestaoRotas.Tracking.VehicleLocation;
import com.GestaoRotas.GestaoRotas.Tracking.VehicleLocationRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TrackingServiceIntegrationTest {

    @Autowired
    private TrackingService trackingService;

    @Autowired
    private VehicleLocationRepository locationRepository;

    @Autowired
    private RepositoryVeiculo veiculoRepository;

    private Veiculo veiculo;
    private LocationDTO locationDTO;

    @BeforeEach
    void setUp() { 

        veiculo = new Veiculo();
        veiculo.setModelo("Toyota");
        veiculo.setMatricula("ABC-123");
        veiculo.setAnoFabricacao(2020);
        veiculo.setCapacidadeTanque(50.0);
        veiculo.setKilometragemAtual(10000.0);
        veiculo.setStatus("DISPONIVEL");
        veiculo.setEmailResponsavel("teste@teste.com");

        veiculo = veiculoRepository.save(veiculo); 

      
        locationDTO = new LocationDTO();
        locationDTO.setVehicleId(veiculo.getId()); 
        locationDTO.setLatitude(-25.9692);
        locationDTO.setLongitude(32.5732);
        locationDTO.setSpeed(50.0);
        locationDTO.setStatus("EM_MOVIMENTO");
    }
    @Test
    void saveLocation_DevePersistirLocalizacaoNoBanco() {
        // Act
        VehicleLocation resultado = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(veiculo.getId(), resultado.getVeiculo().getId());
        assertEquals(-25.9692, resultado.getLatitude());
        assertEquals(32.5732, resultado.getLongitude());
        assertEquals(50.0, resultado.getSpeed());
        assertEquals("EM_MOVIMENTO", resultado.getStatus());
        assertNotNull(resultado.getTimestamp());

        // Verificar se foi realmente salvo no banco
        Optional<VehicleLocation> locationSalva = locationRepository.findById(resultado.getId());
        assertTrue(locationSalva.isPresent());
    }

    @Test
    void saveLocation_MultiplasLocalizacoes_DeveSalvarTodas() {
        // Act
        VehicleLocation location1 = trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        locationDTO.setSpeed(75.0);
        VehicleLocation location2 = trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        locationDTO.setSpeed(70.0);
        VehicleLocation location3 = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(location1.getId());
        assertNotNull(location2.getId());
        assertNotNull(location3.getId());
        
        List<VehicleLocation> locations = locationRepository.findAll();
        assertTrue(locations.size() >= 3);
    }

    @Test
    void getLastLocation_DeveRetornarUltimaLocalizacao() {
        // Arrange
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        locationDTO.setSpeed(75.0);
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        locationDTO.setSpeed(70.0);
        VehicleLocation ultimaLocation = trackingService.saveLocation(locationDTO);

        // Act
        Optional<VehicleLocation> resultado = trackingService.getLastLocation(veiculo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(ultimaLocation.getId(), resultado.get().getId());
        assertEquals(-23.5705, resultado.get().getLatitude());
        assertEquals(-46.6533, resultado.get().getLongitude());
        assertEquals(70.0, resultado.get().getSpeed());
    }

    @Test
    void getLastLocation_ParaVeiculoSemLocalizacoes_DeveRetornarVazio() {
        // Act
        Optional<VehicleLocation> resultado = trackingService.getLastLocation(veiculo.getId());

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void getLocationHistory_DeveRetornarHistoricoCompleto() {
        // Arrange
        LocalDateTime antes = LocalDateTime.now();
        
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        locationDTO.setSpeed(75.0);
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        locationDTO.setSpeed(70.0);
        trackingService.saveLocation(locationDTO);
        
        LocalDateTime depois = LocalDateTime.now();

        // Act
        List<VehicleLocation> historico = trackingService.getLocationHistory(veiculo.getId(), antes);

        // Assert
        assertNotNull(historico);
        assertTrue(historico.size() >= 3);
        
    }

    @Test
    void getLocationHistory_ComDataDesde_DeveFiltrarPorData() {
        // Arrange
        trackingService.saveLocation(locationDTO);
        
        // Aguarda um pouco para garantir timestamps diferentes
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
         
        LocalDateTime marco = LocalDateTime.now();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        trackingService.saveLocation(locationDTO);

        // Act
        List<VehicleLocation> historico = trackingService.getLocationHistory(veiculo.getId(), marco);

        // Assert
        assertNotNull(historico);
        // Deve ter apenas as localizações após o marco
        assertTrue(historico.size() >= 2);
    }
    @Test
    void getLocationHistory_ParaVeiculoDiferente_DeveRetornarHistoricoEspecifico() {
        // Arrange
        Veiculo veiculo2 = new Veiculo();
        veiculo2.setModelo("Civic");
        veiculo2.setMatricula("ABC-1234");
        veiculo2.setAnoFabricacao(2022);
        veiculo2.setCapacidadeTanque(50.0);
        veiculo2.setKilometragemAtual(10000.0);
        veiculo2.setStatus("DISPONIVEL");
        veiculo2.setEmailResponsavel("teste2@teste.com");

        Veiculo veiculo2Salvo = veiculoRepository.save(veiculo2);

        // Salvar localizações
        trackingService.saveLocation(locationDTO);

        LocationDTO locationDTO2 = new LocationDTO();
        locationDTO2.setVehicleId(veiculo2Salvo.getId());
        locationDTO2.setLatitude(-23.5505);
        locationDTO2.setLongitude(-46.6333);
        locationDTO2.setSpeed(60.0);
        locationDTO2.setStatus("EM_MOVIMENTO");

        trackingService.saveLocation(locationDTO2);

        LocalDateTime since = LocalDateTime.now().minusHours(1);

        // Act
        List<VehicleLocation> historicoVeiculo1 =
                trackingService.getLocationHistory(veiculo.getId(), since);

        List<VehicleLocation> historicoVeiculo2 =
                trackingService.getLocationHistory(veiculo2Salvo.getId(), since);

        // Assert
        assertTrue(historicoVeiculo1.stream().allMatch(l ->
                l.getVeiculo().getId().equals(veiculo.getId())
        ));

        assertTrue(historicoVeiculo2.stream().allMatch(l ->
                l.getVeiculo().getId().equals(veiculo2Salvo.getId())
        ));
    }

    @Test
    void findAll_DeveRetornarTodasLocalizacoes() {
        // Arrange
        trackingService.saveLocation(locationDTO);
        trackingService.saveLocation(locationDTO);
        trackingService.saveLocation(locationDTO);

        // Act
        List<VehicleLocation> locations = trackingService.findAll();

        // Assert
        assertNotNull(locations);
        assertTrue(locations.size() >= 3);
    }

    @Test
    void findAll_QuandoNaoHaLocalizacoes_DeveRetornarListaVazia() {
        // Act
        List<VehicleLocation> locations = trackingService.findAll();

        assertTrue(!locations.isEmpty());
    } 

    @Test
    void count_DeveContarCorretamente() {
        // Arrange
        long countInicial = trackingService.count();
        
        trackingService.saveLocation(locationDTO);
        trackingService.saveLocation(locationDTO);
        trackingService.saveLocation(locationDTO);

        // Act
        Long countFinal = trackingService.count();

        // Assert
        assertEquals(countInicial + 3, countFinal);
    }

    @Test
    void saveLocation_ComStatusParado_DeveSalvar() {
        // Arrange
        locationDTO.setStatus("PARADO");
        locationDTO.setSpeed(0.0);

        // Act
        VehicleLocation resultado = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("PARADO", resultado.getStatus());
        assertEquals(0.0, resultado.getSpeed());
    }

    @Test
    void saveLocation_ComStatusEstacionado_DeveSalvar() {
        // Arrange
        locationDTO.setStatus("ESTACIONADO");
        locationDTO.setSpeed(0.0);

        // Act
        VehicleLocation resultado = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("ESTACIONADO", resultado.getStatus());
    }

    @Test
    void getLastLocation_AposMultiplasLocalizacoes_DeveRetornarAMaisRecente() {
        // Arrange
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        VehicleLocation ultima = trackingService.saveLocation(locationDTO);

        // Act
        Optional<VehicleLocation> resultado = trackingService.getLastLocation(veiculo.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(ultima.getLatitude(), resultado.get().getLatitude());
        assertEquals(ultima.getLongitude(), resultado.get().getLongitude());
    }

    @Test
    void getLocationHistory_ComPeriodoLongo_DeveRetornarHistoricoCompleto() {
        // Arrange
        // Salvar localizações com diferentes timestamps
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5605);
        locationDTO.setLongitude(-46.6433);
        trackingService.saveLocation(locationDTO);
        
        locationDTO.setLatitude(-23.5705);
        locationDTO.setLongitude(-46.6533);
        trackingService.saveLocation(locationDTO);

        LocalDateTime desde = LocalDateTime.now().minusDays(7);

        // Act
        List<VehicleLocation> historico = trackingService.getLocationHistory(veiculo.getId(), desde);

        // Assert
        assertNotNull(historico);
        assertTrue(historico.size() >= 3);
    }

    @Test
    void saveLocation_ComVelocidadeAlta_DeveSalvar() {
        // Arrange
        locationDTO.setSpeed(180.5);
        locationDTO.setStatus("EM_MOVIMENTO_RAPIDO");

        // Act
        VehicleLocation resultado = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(180.5, resultado.getSpeed());
        assertEquals("EM_MOVIMENTO_RAPIDO", resultado.getStatus());
    }

    @Test
    void saveLocation_ComCoordenadasExtremas_DeveSalvar() {
        // Arrange
        locationDTO.setLatitude(90.0);
        locationDTO.setLongitude(180.0);

        // Act
        VehicleLocation resultado = trackingService.saveLocation(locationDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(90.0, resultado.getLatitude());
        assertEquals(180.0, resultado.getLongitude());
    }

    @Test
    void getLocationHistory_ComVeiculoIdInvalido_DeveRetornarListaVazia() {
        // Arrange
        LocalDateTime since = LocalDateTime.now().minusHours(1);

        // Act
        List<VehicleLocation> historico = trackingService.getLocationHistory(999L, since);

        // Assert
        assertNotNull(historico);
        assertTrue(historico.isEmpty());
    }

}
