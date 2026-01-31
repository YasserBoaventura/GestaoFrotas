package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;
import java.util.*;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.VeiculoCustoDTO;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter 
public class RelatorioCustosDetalhadoDTO {
	 // Informações do relatório
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private LocalDate dataGeracao = LocalDate.now();
    private String titulo = "Relatório de Custos Detalhado";
    
    // Totais gerais
    private Double totalPeriodo;
    private Integer quantidadeCustos; 
    
    // Agrupamentos
    private Map<String, Double> totalPorVeiculo;          // Matrícula -> Total
    private List<?> totalPorTipo;             // Tipo -> Total
    private Map<String, Double> totalPorCentroCusto;      // CentroCusto -> Total
    private Map<String, Double> totalPorMes;              // Mês/Ano -> Total
    
    // Estatísticas
    private Double mediaDiaria; 
    private Double mediaPorVeiculo;
    private Double maiorCusto;
    private String veiculoMaisCaro;
    private String tipoMaisFrequente;
    
    // Top 5
    private List<CustoDetalhadoDTO> top5CustosMaisAltos;
    private List<VeiculoCustoDTO> top5VeiculosMaisCaros;
     
    // Detalhamento  
    private List<CustoDTO> custosDetalhados;
     
    // Métodos auxiliares
  
    
    public Double getPercentualPorVeiculo(String matricula) {
        if (totalPeriodo == null || totalPeriodo == 0) return 0.0;
        Double totalVeiculo = totalPorVeiculo.get(matricula);
        if (totalVeiculo == null) return 0.0;
        return (totalVeiculo / totalPeriodo) * 100;
    }
}
