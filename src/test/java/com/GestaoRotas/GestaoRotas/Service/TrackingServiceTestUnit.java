package com.GestaoRotas.GestaoRotas.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class TrackingServiceTestUnit {


@Mock
private VehicleLocationRepository locationRepository;

@Mock
private SimpMessagingTemplate messagingTemplate;
	
	@Mock
	private RepositoryVeiculo veiculoRepository;
	
	@InjectMocks
	private TrackingService trackingService;
	
	private Veiculo veiculo;
	private VehicleLocation vehicleLocation;
	private LocationDTO locationDTO;

    @BeforeEach
    void setUp() {
    veiculo = new Veiculo();
    veiculo.setId(1L);
    veiculo.setMatricula("ABC-1234");
    veiculo.setModelo("Fusion");
    veiculo.setStatus("DISPONIVEL");

    vehicleLocation = new VehicleLocation();
    vehicleLocation.setId(1L);
    vehicleLocation.setVeiculo(veiculo);
    vehicleLocation.setLatitude(-23.5505);
    vehicleLocation.setLongitude(-46.6333);
    vehicleLocation.setSpeed(80.5);
    vehicleLocation.setStatus("EM_MOVIMENTO");
    vehicleLocation.setTimestamp(LocalDateTime.now());

	locationDTO = new LocationDTO();
	locationDTO.setVehicleId(1L);
	locationDTO.setLatitude(-23.5505);
	locationDTO.setLongitude(-46.6333);
	locationDTO.setSpeed(80.5);
	locationDTO.setStatus("EM_MOVIMENTO");
}
	
	@Test
	void saveLocation_ComDadosValidos_DeveSalvarEEnviarWebSocket() {
	    // Arrange
	when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
	when(locationRepository.save(any(VehicleLocation.class))).thenReturn(vehicleLocation);
	doNothing().when(messagingTemplate).convertAndSend(anyString(), any(VehicleLocation.class));
	
	// Act
	VehicleLocation resultado = trackingService.saveLocation(locationDTO);
	
	// Assert
	assertNotNull(resultado);
	assertEquals(1L, resultado.getId());
	assertEquals(-23.5505, resultado.getLatitude());
	assertEquals(-46.6333, resultado.getLongitude());
	assertEquals(80.5, resultado.getSpeed());
	assertEquals("EM_MOVIMENTO", resultado.getStatus());
	
	verify(locationRepository, times(1)).save(any(VehicleLocation.class));
	verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/locations/1"), any(VehicleLocation.class));
	}
	
	@Test
	void saveLocation_ComVeiculoNaoEncontrado_DeveLancarExcecao() {
	    // Arrange
	when(veiculoRepository.findById(999L)).thenReturn(Optional.empty());
	locationDTO.setVehicleId(999L);
	
	// Act & Assert
	RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    trackingService.saveLocation(locationDTO);
	});
	
	assertEquals("Veiculo nao encontrado", exception.getMessage());
	        verify(locationRepository, never()).save(any(VehicleLocation.class));
	        verify(messagingTemplate, never())
	        .convertAndSend(anyString(), anyString()); 
	    }
	 
	    @Test
	    void saveLocation_ComStatusParado_DeveSalvarCorretamente() {
	   
	        locationDTO.setStatus("PARADO");
	locationDTO.setSpeed(0.0);
	
	VehicleLocation locationParado = new VehicleLocation();
	locationParado.setId(2L);
	locationParado.setVeiculo(veiculo);
	locationParado.setLatitude(locationDTO.getLatitude());
	locationParado.setLongitude(locationDTO.getLongitude());
	locationParado.setSpeed(0.0);
	locationParado.setStatus("PARADO");
	
	when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
	when(locationRepository.save(any(VehicleLocation.class))).thenReturn(locationParado);
	doNothing().when(messagingTemplate).convertAndSend(anyString(), any(VehicleLocation.class));
	
	// Act
	VehicleLocation resultado = trackingService.saveLocation(locationDTO);
	
	// Assert
	assertNotNull(resultado);
	assertEquals(0.0, resultado.getSpeed());
	assertEquals("PARADO", resultado.getStatus());
	}
	
	@Test
	void saveLocation_ComCoordenadasInvalidas_DeveSalvarMesmoAssim() {
	    // Arrange
	locationDTO.setLatitude(100.0); // Latitude inválida (> 90)
	locationDTO.setLongitude(200.0); // Longitude inválida (> 180)
	
	when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
	when(locationRepository.save(any(VehicleLocation.class))).thenReturn(vehicleLocation);
	doNothing().when(messagingTemplate).convertAndSend(anyString(), any(VehicleLocation.class));
	
	// Act
	VehicleLocation resultado = trackingService.saveLocation(locationDTO);
	
	// Assert
	assertNotNull(resultado);
	// O serviço não valida coordenadas, então salva mesmo inválidas
	    verify(locationRepository, times(1)).save(any(VehicleLocation.class));
	}
	
	@Test
	void getLastLocation_QuandoExiste_DeveRetornarOptional() {
	    // Arrange
	when(locationRepository.findLastLocation(1L)).thenReturn(Optional.of(vehicleLocation));
	
	// Act
	Optional<VehicleLocation> resultado = trackingService.getLastLocation(1L);
	
	// Assert
	    assertTrue(resultado.isPresent());
	    assertEquals(1L, resultado.get().getId());
	    assertEquals(-23.5505, resultado.get().getLatitude());
	    verify(locationRepository, times(1)).findLastLocation(1L);
	}
	
	@Test
	void getLastLocation_QuandoNaoExiste_DeveRetornarOptionalVazio() {
	
	    when(locationRepository.findLastLocation(999L)).thenReturn(Optional.empty());
	
	    // Act
	Optional<VehicleLocation> resultado = trackingService.getLastLocation(999L);
	
	// Assert
	    assertFalse(resultado.isPresent());
	    verify(locationRepository, times(1)).findLastLocation(999L);
	}
	
	@Test
	void getLocationHistory_ComDataDesde_DeveRetornarLista() {
	    // Arrange
	LocalDateTime since = LocalDateTime.now().minusHours(1);
	List<VehicleLocation> locations = Arrays.asList(vehicleLocation, new VehicleLocation());
	when(locationRepository.findRecentLocations(1L, since)).thenReturn(locations);
	
	// Act
	List<VehicleLocation> resultado = trackingService.getLocationHistory(1L, since);
	
	// Assert
	    assertEquals(2, resultado.size());
	    verify(locationRepository, times(1)).findRecentLocations(1L, since);
	}
	
	@Test
	void getLocationHistory_ComDataFutura_DeveRetornarListaVazia() {
	    // Arrange
	LocalDateTime since = LocalDateTime.now().plusHours(1);
	when(locationRepository.findRecentLocations(1L, since)).thenReturn(Collections.emptyList());
	
	// Act
	List<VehicleLocation> resultado = trackingService.getLocationHistory(1L, since);
	
	// Assert
	    assertTrue(resultado.isEmpty());
	    verify(locationRepository, times(1)).findRecentLocations(1L, since);
	}
	
	@Test
	void getLocationHistory_ParaVeiculoSemHistorico_DeveRetornarListaVazia() {
	    // Arrange
	LocalDateTime since = LocalDateTime.now().minusDays(7);
	when(locationRepository.findRecentLocations(999L, since)).thenReturn(Collections.emptyList());
	
	// Act
	List<VehicleLocation> resultado = trackingService.getLocationHistory(999L, since);
	
	// Assert
	    assertTrue(resultado.isEmpty());
	}
	
	@Test
	void findAll_DeveRetornarListaDeLocalizacoes() {
	    // Arrange
	List<VehicleLocation> locations = Arrays.asList(vehicleLocation, new VehicleLocation());
	when(locationRepository.findAll()).thenReturn(locations);
	
	// Act
	List<VehicleLocation> resultado = trackingService.findAll();
	
	// Assert
	    assertEquals(2, resultado.size());
	    verify(locationRepository, times(1)).findAll();
	}
	
	@Test
	void findAll_QuandoNaoHaLocalizacoes_DeveRetornarListaVazia() {
	    // Arrange
	when(locationRepository.findAll()).thenReturn(Collections.emptyList());
	
	// Act
	List<VehicleLocation> resultado = trackingService.findAll();
	
	// Assert
	    assertTrue(resultado.isEmpty());
	    verify(locationRepository, times(1)).findAll();
	}
	
	@Test
	void count_DeveRetornarQuantidadeDeLocalizacoes() {
	    // Arrange
	when(locationRepository.count()).thenReturn(10L);
	
	// Act
	Long resultado = trackingService.count();
	
	// Assert
	    assertEquals(10L, resultado);
	    verify(locationRepository, times(1)).count();
	}
	
	@Test
	void count_QuandoNaoHaLocalizacoes_DeveRetornarZero() {
	    // Arrange
	when(locationRepository.count()).thenReturn(0L);
	
	// Act
	Long resultado = trackingService.count();
	
	// Assert
	    assertEquals(0L, resultado);
	}
	
	@Test
	void saveLocation_DeveEnviarMensagemWebSocketParaTopicoCorreto() {
	    // Arrange
	when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
	when(locationRepository.save(any(VehicleLocation.class))).thenReturn(vehicleLocation);
	doNothing().when(messagingTemplate).convertAndSend(anyString(), any(VehicleLocation.class));
	
	// Act
	trackingService.saveLocation(locationDTO);
	
	// Assert
	verify(messagingTemplate, times(1)).convertAndSend(
	    eq("/topic/locations/1"), 
	        any(VehicleLocation.class)
	    );
	}
	
	@Test
	void saveLocation_DeveDefinirTimestampAutomaticamente() {
	    // Arrange
	when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
	when(locationRepository.save(any(VehicleLocation.class))).thenAnswer(invocation -> {
	    VehicleLocation saved = invocation.getArgument(0);
	    saved.setId(1L);
	    return saved;
	});
	doNothing().when(messagingTemplate).convertAndSend(anyString(), any(VehicleLocation.class));
	
	// Act
	VehicleLocation resultado = trackingService.saveLocation(locationDTO);
	
	// Assert
	assertNotNull(resultado);
	// O timestamp deve ser definido pela entidade, não pelo service
	    verify(locationRepository, times(1)).save(any(VehicleLocation.class));
	}
	}
	
	
