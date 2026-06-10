package com.GestaoRotas.GestaoRotas.DTO;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
public class RelatorioPorVeiculoDTO {
  
	
    private String veiculo;
    private String modelo;
    private Long totalViagens;
    private Double totalKm;
    private Double totalCombustivel;
    //media combustivel
    private Double mediaCombustivel; 
  
    // Construtor para os campos principais (usado nos testes)

	
}
    
