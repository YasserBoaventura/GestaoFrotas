package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude; 
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor  
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardCustosDTO { 
    private Double totalMesAtual = 0.0;
    private Double totalMesAnterior = 0.0;
    private Double variacaoPercentual = 0.0;
    private Map<String, Double> custosPorTipo;
    private List<VeiculoCustoDTO> veiculosMaisCaros;
    private List<CustoDTO> ultimosCustos;
    private String mensagem; // Para debug 
}


