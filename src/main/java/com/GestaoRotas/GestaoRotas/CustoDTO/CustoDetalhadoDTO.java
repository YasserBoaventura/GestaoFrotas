package com.GestaoRotas.GestaoRotas.CustoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;

import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import java.math.*;
@Data

@NoArgsConstructor
@AllArgsConstructor
public class CustoDetalhadoDTO {
	 private Long id;
	    private String descricao;
	    private BigDecimal valor;
	    private LocalDate data;
	    private String tipo;
	    private String status;
	    private String veiculoMatricula;
	    private String veiculoModelo;
	    
	    // Construtor específico para a query JPQL
	    public CustoDetalhadoDTO(Long id, String descricao, Double valor, 
	                            LocalDate data, TipoCusto tipo, StatusCusto status,
	                            String veiculoMatricula, String veiculoModelo) {
	        this.id = id;
	        this.descricao = descricao;
	        this.valor = BigDecimal.valueOf(valor); 
	        this.data = data;
	        this.tipo = tipo != null ? tipo.name() : null;  
	        this.status = status != null ? status.name() : null;  
	        this.veiculoMatricula = veiculoMatricula;
	        this.veiculoModelo = veiculoModelo;
	    }
}