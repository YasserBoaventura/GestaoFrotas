package com.GestaoRotas.GestaoRotas.Tracking;
import java.time.LocalDateTime;
import java.util.List;
import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;

public sealed interface TrackingServiceImpl permits TrackingService{
    VehicleLocation saveLocation(LocationDTO locationDTO) ;
    
    VehicleLocation getLastLocation(String vehicleId) ;
     
    List<VehicleLocation> getLocationHistory(String vehicleId, LocalDateTime since);
}
