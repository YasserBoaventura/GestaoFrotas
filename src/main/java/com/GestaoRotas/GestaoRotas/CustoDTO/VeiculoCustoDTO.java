package com.GestaoRotas.GestaoRotas.CustoDTO;



import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter 

public class VeiculoCustoDTO {

	     private String matricula;
	    private String modelo;
	    private Double totalCusto;
	    public VeiculoCustoDTO(String matricula, String modelo, Double totalCusto) {
	        this.matricula = matricula;
	        this.modelo = modelo;
	        this.totalCusto =  totalCusto; 
	    }
	    
	    
}
