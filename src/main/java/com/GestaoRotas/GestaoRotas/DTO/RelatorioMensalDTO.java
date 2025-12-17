package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

 @Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioMensalDTO {
    private Integer ano;
    private Integer mes;
    private Long quantidadeViagens;
    private Double totalKilometragem;
    private Double totalLitrosAbastecidos;


}
