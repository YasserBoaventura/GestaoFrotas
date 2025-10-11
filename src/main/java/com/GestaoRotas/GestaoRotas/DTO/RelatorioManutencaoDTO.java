package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioManutencaoDTO {

	    private String veiculo;
	    private Long totalManutencoes;
	    private Double custoTotal;
	    private Double custoMedio;


}