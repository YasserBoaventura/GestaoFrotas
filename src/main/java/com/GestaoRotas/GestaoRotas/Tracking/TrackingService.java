package com.GestaoRotas.GestaoRotas.Tracking;

import java.lang.foreign.Linker.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public non-sealed class TrackingService implements TrackingServiceImpl{
	  
    private final VehicleLocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RepositoryVeiculo veiculoRepository;
    
          
    @Transactional
    public VehicleLocation saveLocation(@Valid LocationDTO locationDTO) {
        VehicleLocation location = new VehicleLocation();
          
        Veiculo veiculo = veiculoRepository.findById(locationDTO.getVehicleId()).orElseThrow(() -> new RuntimeException("Veiculo nao encontrado")); 
        location.setVeiculo(veiculo);
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setSpeed(locationDTO.getSpeed());
        location.setStatus(locationDTO.getStatus());
        
        VehicleLocation saved = locationRepository.save(location);
         
        // Enviar via WebSocket para clientes conectados 
        messagingTemplate.convertAndSend("/topic/locations/" + locationDTO.getVehicleId(), saved);
          
        return saved;  
    }       
    @Transactional
    public Optional<VehicleLocation> getLastLocation(Long vehicleId) {
        return locationRepository.findLastLocation(vehicleId);
    }
    @Transactional
    public List<VehicleLocation> getLocationHistory(Long vehicleId, LocalDateTime since) {
        return locationRepository.findRecentLocations(vehicleId, since);
    }
    public List<VehicleLocation> findAll(){
    	return locationRepository.findAll(); 
    	} 
    public Long count() {
    	return locationRepository.count(); 
    }
	 
}
