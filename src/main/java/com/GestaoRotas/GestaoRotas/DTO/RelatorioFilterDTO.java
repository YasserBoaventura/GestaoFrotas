package com.GestaoRotas.GestaoRotas.DTO;

import java.util.*;

import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@Getter
@Setter
@AllArgsConstructor 
public class RelatorioFilterDTO {
	 private LocalDate dataInicio;
	    private LocalDate dataFim;
	    private Long veiculoId;
	    private TipoCusto tipoCusto; // Mantém como enum
	    private StatusCusto statusCusto = StatusCusto.PAGO; // Mantém como enum
	    private boolean agruparPorVeiculo = true;
	    private boolean agruparPorTipo = true;
	    private boolean incluirDetalhado = true;
	    
	    private  LocalDate dataFimTop5VeiculosMaisCarro ;
	    private LocalDate  dataInicioTop5VeiculosMaisCarro;
	     
	    
	    // Construtor com valores padrão
	    public RelatorioFilterDTO() {
	        this.dataInicio = LocalDate.now().minusMonths(1);
	        this.dataFim = LocalDate.now();
	    }
	    
	    // Getters e Setters normais...
	    
	    // SETTERS ESPECIAIS PARA JSON
	    public void setDataInicio(String dataInicio) {
	        if (dataInicio != null) {
	            this.dataInicio = LocalDate.parse(dataInicio);
	        }
	    }
	    
	    public void setDataFim(String dataFim) {
	        if (dataFim != null) {
	            this.dataFim = LocalDate.parse(dataFim);
	        }
	    }
	    
	    public void setTipoCusto(String tipoCusto) {
	        if (tipoCusto != null) {
	            try {
	                this.tipoCusto = TipoCusto.valueOf(tipoCusto.toUpperCase());
	            } catch (IllegalArgumentException e) {
	                // Pode definir um valor padrão ou null
	                this.tipoCusto = null;
	            }
	        }
	    }
	    
	    public void setStatusCusto(String statusCusto) {
	        if (statusCusto != null) {
	            try {
	                this.statusCusto = StatusCusto.valueOf(statusCusto.toUpperCase());
	            } catch (IllegalArgumentException e) {
	                this.statusCusto = StatusCusto.PAGO;
	            }
	        }
	    }
	    
	    // Também mantenha os setters que aceitam enums
	    public void setTipoCusto(TipoCusto tipoCusto) {
	        this.tipoCusto = tipoCusto;
	    }
	    
	    public void setStatusCusto(StatusCusto statusCusto) {
	        this.statusCusto = statusCusto;
	    }
	    
	    // Getters...
	    public LocalDate getDataInicio() {
	        return dataInicio;
	    }
	    
	    public LocalDate getDataFim() {
	        return dataFim;
	    }
	    
	    public Long getVeiculoId() {
	        return veiculoId;
	    }
	    
	    public void setVeiculoId(Long veiculoId) {
	        this.veiculoId = veiculoId;
	    }
	    
	    public TipoCusto getTipoCusto() {
	        return tipoCusto;
	    }
	    
	    public StatusCusto getStatusCusto() {
	        return statusCusto;
	    }
	    
	    public boolean isAgruparPorVeiculo() {
	        return agruparPorVeiculo;
	    }
	    
	    public void setAgruparPorVeiculo(boolean agruparPorVeiculo) {
	        this.agruparPorVeiculo = agruparPorVeiculo;
	    }
	    
	    public boolean isAgruparPorTipo() {
	        return agruparPorTipo;
	    }
	    
	    public void setAgruparPorTipo(boolean agruparPorTipo) {
	        this.agruparPorTipo = agruparPorTipo;
	    }
	    
	    public boolean isIncluirDetalhado() {
	        return incluirDetalhado;
	    }
	    
	    public void setIncluirDetalhado(boolean incluirDetalhado) {
	        this.incluirDetalhado = incluirDetalhado;
	    }
}
