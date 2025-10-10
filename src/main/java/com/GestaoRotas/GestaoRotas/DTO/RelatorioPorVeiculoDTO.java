package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPorVeiculoDTO {

    private String veiculo;
    private Long totalViagens;
    private Double totalKm;
    private Double totalCombustivel;

	
}
