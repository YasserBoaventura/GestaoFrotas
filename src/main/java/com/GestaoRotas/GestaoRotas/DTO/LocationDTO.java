package com.GestaoRotas.GestaoRotas.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class LocationDTO {
    private String vehicleId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String status; 

}
