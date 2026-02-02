package com.GestaoRotas.GestaoRotas.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConcluirViagemRequest {
       
	private LocalDateTime dataHoraChegada;
    private Double kilometragemFinal;
    private String observacoes;
  }
 