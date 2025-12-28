package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class concluirManutencaoRequest {
	private LocalDateTime horaConcluida;
    private String observacoes;
}
