package com.GestaoRotas.GestaoRotas.Tracking;

import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import jakarta.validation.Valid;

import java.lang.foreign.Linker.Option;
import java.time.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin("*")
@RequiredArgsConstructor  
public class TrackingController {
 
 private final TrackingService trackingService;
                                                                                                        
 @PostMapping("/location")   
 @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
 public ResponseEntity<VehicleLocation> updateLocation(@RequestBody @Valid LocationDTO locationDTO) {
     VehicleLocation saved = trackingService.saveLocation(locationDTO);
     return ResponseEntity.ok(saved);          
 }        
                  
   @GetMapping("/location/{vehicleId}/last")
   @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
   public ResponseEntity<VehicleLocation> getLastLocation(@PathVariable Long vehicleId) {
    Optional<VehicleLocation> location = trackingService.getLastLocation(vehicleId);
           return location          
             .map(ResponseEntity::ok)     
             .orElseGet(() -> ResponseEntity.notFound().build());
 }                         
  @GetMapping("/location/{vehicleId}/history")
  @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')") 
    public ResponseEntity<List<VehicleLocation>> getLocationHistory(
            @PathVariable Long vehicleId, 
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
