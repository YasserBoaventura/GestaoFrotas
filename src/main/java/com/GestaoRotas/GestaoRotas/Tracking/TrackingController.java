package com.GestaoRotas.GestaoRotas.Tracking;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GestaoRotas.GestaoRotas.DTO.LocationDTO;

import jakarta.annotation.PostConstruct;

import java.time.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin("*")
@RequiredArgsConstructor  
public class TrackingController {
 
 private final TrackingService trackingService;
                                                                
    @PostMapping("/location")        
    public ResponseEntity<VehicleLocation> updateLocation(@RequestBody LocationDTO locationDTO) {
        VehicleLocation saved = trackingService.saveLocation(locationDTO);
        return ResponseEntity.ok(saved);          
    }  
    @GetMapping("/location/{vehicleId}/last")
    public ResponseEntity<VehicleLocation> getLastLocation(@PathVariable String vehicleId) {
        VehicleLocation location = trackingService.getLastLocation(vehicleId);
        return ResponseEntity.ok(location); 
    }              
    @GetMapping("/location/{vehicleId}/history")
    public ResponseEntity<List<VehicleLocation>> getLocationHistory(
            @PathVariable String vehicleId, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        if (since == null) { 
            since = LocalDateTime.now().minusHours(1);
        }      
    List<VehicleLocation> history = trackingService.getLocationHistory(vehicleId, since);
        return ResponseEntity.ok(history);
    } 
     @GetMapping("/findAll") 
     public ResponseEntity<List<VehicleLocation>> findAll(){
    	 return ResponseEntity.ok(trackingService.findAll()); 
     }
    
	
     
}
