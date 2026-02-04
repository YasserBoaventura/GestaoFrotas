package com.GestaoRotas.GestaoRotas.CustoDTO;

import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor             
public class CustoViagemDTO {
    private Long viagemId;
    private Long veiculoId; 
    private TipoCusto tipo;
    private String descricao;
    private Double valor;
}
