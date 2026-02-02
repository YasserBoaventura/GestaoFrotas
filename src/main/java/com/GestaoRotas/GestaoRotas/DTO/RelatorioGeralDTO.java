package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RelatorioGeralDTO {
	    private Long totalViagens;
	    private Long totalMotoristas;
	    private Long totalVeiculos;
	    private Double totalKilometragem;
	    private Double totalLitrosAbastecidos;
	    private Double mediaKilometragemPorViagem;

}
