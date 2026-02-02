package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbastecimentoDTO {
 
	
	    private Long id;
	 
	    private String dataAbastecimento;
	    private Double quantidadeLitros;
	    private Double precoPorLitro;
	    private String tipoCombustivel;
	    private Double kilometragemVeiculo;
	    private String statusAbastecimento; 
	    private Long veiculoId;        // Apenas o ID
	    private Long viagemId; 
} 
