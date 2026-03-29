package com.GestaoRotas.GestaoRotas.Tracking;
import java.lang.foreign.Linker.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;

public sealed interface TrackingServiceImpl permits TrackingService{
   
	VehicleLocation saveLocation(LocationDTO locationDTO);
      
   Optional<VehicleLocation> getLastLocation(Long vehicleId) ;
      
    List<VehicleLocation> getLocationHistory(Long vehicleId, LocalDateTime since);
}
  