package com.GestaoRotas.GestaoRotas.CustoDTO;

import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

    
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustoViagemDTO {
    private Long viagemId;
    private Long veiculoId; 
    private TipoCusto tipo; 
    private String descricao;
    private String observacoes;
    private Double valor;
    
    
    public CustoViagemDTO(Long viagemId, Long veiculoId, TipoCusto tipo, 
	            String descricao, String observacoes, Double valor) {
	this.viagemId = viagemId;
	this.veiculoId = veiculoId;
	this.tipo = tipo;
	this.descricao = descricao;
	this.observacoes = observacoes;
	this.valor = valor;
	}
    public CustoViagemDTO() {
    	
    }
	
	// Getters
	public Long getViagemId() {
	return viagemId;
	}
	
	public Long getVeiculoId() {
	return veiculoId;
	}
	
	public TipoCusto getTipo() {
	return tipo;
	}
	
	public String getDescricao() {
	return descricao;
	}
	
	public String getObservacoes() {
	return observacoes;
	}
	
	public Double getValor() {
	return valor;
	}
	
	// Setters
	public void setViagemId(Long viagemId) {
	this.viagemId = viagemId;
	}
	
	public void setVeiculoId(Long veiculoId) {
	this.veiculoId = veiculoId;
	}
	
	public void setTipo(TipoCusto tipo) {
	this.tipo = tipo;
	}
	
	public void setDescricao(String descricao) {
	this.descricao = descricao;
	}
	
	public void setObservacoes(String observacoes) {
	this.observacoes = observacoes;
	}
	
	public void setValor(Double valor) {
	this.valor = valor;
	}

}
