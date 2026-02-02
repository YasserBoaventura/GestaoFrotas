package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioTopMotoristasDTO {
    private String nomeMotorista;
    private Long totalViagens;
    private Double totalKilometragem;
}
