package com.GestaoRotas.GestaoRotas.DTO;

import java.math.BigDecimal;

import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class RelatorioManutencaoDTO {
    private String veiculo;
    private Long totalManutencoes;
    private Double custoTotal;
    private Double custoMedio;
    private statusManutencao status;
	
	    public RelatorioManutencaoDTO(String veiculo, Long totalManutencoes, 
                Double custoTotal, Double custoMedio, 
                statusManutencao status) {
this.veiculo = veiculo;
this.totalManutencoes = totalManutencoes;
this.custoTotal = custoTotal;
this.custoMedio = custoMedio;
this.status = status;
} 
    }
