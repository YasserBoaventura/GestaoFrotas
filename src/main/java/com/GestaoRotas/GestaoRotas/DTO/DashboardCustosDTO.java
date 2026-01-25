package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*; 
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor  
public class DashboardCustosDTO { 
    private Double totalMesAtual;
    private Double totalMesAnterior;
    private Double variacaoPercentual;
    private List<?> custosPorTipo;
    private List<?> veiculosMaisCaros;
    private List<?> ultimosCustos;
}
