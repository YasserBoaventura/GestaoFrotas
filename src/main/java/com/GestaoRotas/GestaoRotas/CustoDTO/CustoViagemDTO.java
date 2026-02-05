package com.GestaoRotas.GestaoRotas.CustoDTO;

import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor     
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustoViagemDTO {
    private Long viagemId;
    private Long veiculoId; 
    private TipoCusto tipo; 
    private String descricao;
    private String observacoes;
    private Double valor;
}
