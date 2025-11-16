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
    private String placa;
    private String modelo;
    private Integer ano;
    private String tipo;
    private Marca marca;
	private Set<Motorista> motoristas; 
	  
	} 

