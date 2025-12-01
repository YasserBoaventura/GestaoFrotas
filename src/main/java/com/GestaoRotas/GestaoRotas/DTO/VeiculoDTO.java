package com.GestaoRotas.GestaoRotas.DTO;
import java.util.*;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VeiculoDTO {
	
	
	    private Long id;
	    private String modelo;
	    private String matricula;
	    private Integer anoFabricacao;
	    private Double capacidadeTanque;
	    private Double kilometragemAtual;
	    private String marcaNome;
	    private Long marcaId;
	    
	    // Métodos calculados que você pode incluir
	    private Double mediaConsumo;
	    private Integer totalViagensConcluidas;
	
	} 

