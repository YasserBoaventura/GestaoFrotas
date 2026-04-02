package com.GestaoRotas.GestaoRotas.Tracking;

import java.io.Serializable;
import java.time.*;

import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne   
    @JsonIgnore  
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;     
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
