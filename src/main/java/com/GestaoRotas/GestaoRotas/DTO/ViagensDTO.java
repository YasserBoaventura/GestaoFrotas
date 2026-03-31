package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDateTime;

import com.GestaoRotas.GestaoRotas.Model.TipoCarga;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViagensDTO {

	    private Long id;
	    private LocalDateTime dataHoraPartida;
	    private LocalDateTime dataHoraChegada;
	    private String status;
	    private Double kilometragemInicial;
	    private Double kilometragemFinal;
	    
	    @Enumerated(EnumType.STRING)
	    private TipoCarga tipoCarga; 
	    private String observacoes;
	    private Long motoristaId;
	    private Long veiculoId;
	    private Long rotaId;
	}


