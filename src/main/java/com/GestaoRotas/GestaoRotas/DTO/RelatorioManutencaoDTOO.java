package com.GestaoRotas.GestaoRotas.DTO;

import java.math.BigDecimal;

import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

public class RelatorioManutencaoDTOO {
	
	
	   private String veiculo;
	    private Long totalManutencoes;
	    private BigDecimal custoTotal;
	    private BigDecimal custoMedio;
	    private statusManutencao status;

	    public RelatorioManutencaoDTOO(
	        String veiculo,
	        Long totalManutencoes,
	        BigDecimal custoTotal,
	        BigDecimal custoMedio,
	        statusManutencao status
	    ) {
	        this.veiculo = veiculo;
	        this.totalManutencoes = totalManutencoes != null ? totalManutencoes : 0;
	        this.custoTotal = custoTotal != null ? custoTotal : BigDecimal.ZERO;
	        this.custoMedio = custoMedio != null ? custoMedio : BigDecimal.ZERO;
	        this.status = status;
	    }

}