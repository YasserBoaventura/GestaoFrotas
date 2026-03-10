package com.GestaoRotas.GestaoRotas.Tracking;

import org.eclipse.angus.mail.imap.protocol.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; 
import java.time.*; 
import java.util.*;   
import org.springframework.stereotype.Repository;
@Repository
public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long>{

	
    List<VehicleLocation> findByVehicleIdOrderByTimestampDesc(String vehicleId);
    
    @Query("SELECT v FROM VehicleLocation v WHERE v.vehicleId = :vehicleId AND v.timestamp >= :since")
    List<VehicleLocation> findRecentLocations(@Param("vehicleId") String vehicleId, 
                                              @Param("since") LocalDateTime since);
    
    @Query(value = "SELECT * FROM vehicle_locations v WHERE v.vehicle_id = :vehicleId ORDER BY v.timestamp DESC LIMIT 2", nativeQuery = true)
    VehicleLocation findLastLocation(@Param("vehicleId") String vehicleId);


}
