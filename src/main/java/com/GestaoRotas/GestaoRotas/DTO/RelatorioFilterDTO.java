package com.GestaoRotas.GestaoRotas.DTO;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Getter
@Setter
@AllArgsConstructor 
public class RelatorioFilterDTO {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long veiculoId;
    private String tipoCusto;
    private String statusCusto;
    private boolean agruparPorVeiculo = true;
    private boolean agruparPorTipo = true;
    private boolean incluirDetalhado = true;
	    
	    // Construtor com valores padrão
	    public RelatorioFilterDTO() {
	        this.dataInicio = LocalDate.now().minusMonths(1);
	        this.dataFim = LocalDate.now();
	    }
}
