package com.GestaoRotas.GestaoRotas.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class LocationDTO {
	@NotNull
    private Long vehicleId; 
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String status; 

}
