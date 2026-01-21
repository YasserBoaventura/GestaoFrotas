package com.GestaoRotas.GestaoRotas.DTO;

import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

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

	    private String matricula;
	    private Double totalLitros;
	    private Double valorTotal;   
	    private Double precoMedio;
	    private  statusAbastecimentos status;     
	    public RelatorioCombustivelDTO(String matricula, Double totalLitros, 
	                                   Double valorTotal, Double precoMedio, 
	                                   statusAbastecimentos status) {
	        this.matricula = matricula;
	        this.totalLitros = totalLitros;
	        this.valorTotal = valorTotal;
	        this.precoMedio = precoMedio; 
	        this.status = status;
	    }
}
	     