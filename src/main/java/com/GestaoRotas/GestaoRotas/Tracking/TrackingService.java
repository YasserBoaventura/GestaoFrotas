package com.GestaoRotas.GestaoRotas.Tracking;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public non-sealed class TrackingService implements TrackingServiceImpl{
	  
    private final VehicleLocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;
     
         
    public VehicleLocation saveLocation(LocationDTO locationDTO) {
        VehicleLocation location = new VehicleLocation();
        location.setVehicleId(locationDTO.getVehicleId());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setSpeed(locationDTO.getSpeed());
        location.setStatus(locationDTO.getStatus());
        
        VehicleLocation saved = locationRepository.save(location);
        
        // Enviar via WebSocket para clientes conectados
        messagingTemplate.convertAndSend("/topic/locations/" + locationDTO.getVehicleId(), saved);
        
        return saved; 
    } 
    public VehicleLocation getLastLocation(String vehicleId) {
        return locationRepository.findLastLocation(vehicleId);
    }
    public List<VehicleLocation> getLocationHistory(String vehicleId, LocalDateTime since) {
        return locationRepository.findRecentLocations(vehicleId, since);
    }
    public List<VehicleLocation> findAll(){
    	return locationRepository.findAll(); 
    	} 
    public Long count() {
    	return locationRepository.count(); 
    }
	 
}
