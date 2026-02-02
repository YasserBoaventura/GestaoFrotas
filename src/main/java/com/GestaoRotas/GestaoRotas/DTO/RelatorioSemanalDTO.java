package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelatorioSemanalDTO {
    private String semana;
    private Long quantidadeViagens;
    private Double totalKilometragem;
    private Double totalLitrosAbastecidos;
}
