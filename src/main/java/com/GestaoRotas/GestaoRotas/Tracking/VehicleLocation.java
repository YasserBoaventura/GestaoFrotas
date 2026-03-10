package com.GestaoRotas.GestaoRotas.Tracking;

import java.io.Serializable;
import java.time.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="vehicle_locations") 
@Data
public class VehicleLocation implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String vehicleId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String status;
    private LocalDateTime timestamp;
    
    @PrePersist  
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

}
