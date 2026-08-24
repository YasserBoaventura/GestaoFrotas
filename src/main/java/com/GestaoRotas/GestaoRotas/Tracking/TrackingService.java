package com.GestaoRotas.GestaoRotas.Tracking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;
import org.springframework.stereotype.Service;


public sealed interface TrackingService permits TrackingService_Impl {
   
	VehicleLocation saveLocation(LocationDTO locationDTO);
      
   Optional<VehicleLocation> getLastLocation(Long vehicleId) ;
      
    List<VehicleLocation> getLocationHistory(Long vehicleId, LocalDateTime since);

 List<VehicleLocation> findAll();
}
  