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
@NoArgsConstructor
public class RelatorioCombustivelDTO {


	    
	    private String veiculo;
	    private Double totalLitros;
	    private Double totalGasto;
	    private Double mediaPorLitro;

    //  Construtor para a query JPA (ORDEM DOS PARÂMETROS É IMPORTANTE!)
    public RelatorioCombustivelDTO(String veiculo, Double totalLitros, Double totalGasto, Double mediaPorLitro) {
        this.veiculo = veiculo;
        this.totalLitros = totalLitros != null ? totalLitros : 0.0;
        this.totalGasto = totalGasto != null ? totalGasto : 0.0;
        this.mediaPorLitro = mediaPorLitro != null ? mediaPorLitro : 0.0;
    }

    // ✅ Método para formatar os valores
    public String getTotalLitrosFormatado() {
        return String.format("%.2f L", totalLitros);
    }

    public String getTotalGastoFormatado() {
        return String.format("R$ %.2f", totalGasto);
    }

    public String getMediaPorLitroFormatado() {
        return String.format("R$ %.2f", mediaPorLitro);
    }

}
