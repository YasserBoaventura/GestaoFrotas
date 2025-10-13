package com.GestaoRotas.GestaoRotas.DTO;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioCombustivelDTO {

	    private String veiculo;
	    private Double totalLitros;
	    private Double totalGasto;
	    private Double mediaPorLitro;
}
