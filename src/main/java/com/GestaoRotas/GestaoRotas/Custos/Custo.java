package com.GestaoRotas.GestaoRotas.Custos;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
public class Custo implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    private LocalDate data;
	    private String descricao;
	    private Double valor;
	    
	    @Enumerated(EnumType.STRING)
	    private TipoCusto tipo;
	    
	    @Enumerated(EnumType.STRING)
	    private StatusCusto status = StatusCusto.PAGO; // Pago como default
	     
	    // RELACIONAMENTOS
	    @ManyToOne
	    @JoinColumn(name = "veiculo_id")
	    @JsonIgnoreProperties({"custos", "hibernateLazyInitializer", "handler"})
	    private Veiculo veiculo;
	    
	    @ManyToOne
	    @JoinColumn(name = "abastecimento_id")
	    @JsonIgnoreProperties({"custo", "hibernateLazyInitializer", "handler"})
	    private abastecimentos abastecimento;
	    
	    @ManyToOne
	    @JoinColumn(name = "manutencao_id")
	    @JsonIgnoreProperties({"custo", "hibernateLazyInitializer", "handler"})
	    private Manutencao manutencao;
	    
	    @ManyToOne
	    @JoinColumn(name = "viagem_id")
	    @JsonIgnoreProperties({"custos", "hibernateLazyInitializer", "handler"})
	    private Viagem viagem;
	    
	    private String observacoes;
	    private String numeroDocumento;
	    
	    // Método para identificar a origem
	    public String getOrigem() {
	        if (abastecimento != null) return "ABASTECIMENTO";
	        if (manutencao != null) return "MANUTENCAO";
	        if (viagem != null) return "VIAGEM";
	        return "MANUAL"; 
	    }
}
