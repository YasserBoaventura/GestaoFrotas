package com.GestaoRotas.GestaoRotas.Custos;

import java.io.Serializable;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.OneToOne;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Custo implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data", updatable = false)
    private LocalDate data;
    private String descricao;
    private Double valor;  
     
    @Enumerated(EnumType.STRING)
    private TipoCusto tipo;
    
    @Enumerated(EnumType.STRING)
    private StatusCusto status = StatusCusto.PAGO; //pago como default
     
    // RELACIONAMENTOS - Usar @JsonIgnore em vez de @JsonBackReference/@JsonManagedReference
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    @JsonIgnoreProperties({"custos", "abastecimentos", "manutencoes", "viagem"})
    private Veiculo veiculo; 
     
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abastecimento_id", unique = true)
    @JsonIgnoreProperties({"custo", "veiculo", "viagem"})
    private abastecimentos abastecimento; 
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manutencao_id", unique = true)
    @JsonIgnoreProperties({"custo", "veiculo"})   
    private Manutencao manutencao; 
      
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id") 
    @JsonIgnoreProperties({"custos", "veiculo", "motorista", "rota", "abastecimentos"})
    private Viagem viagem;
    
    private String observacoes;
    private String numeroDocumento;
    private LocalDateTime dataActualizacao; 
    // Método para identificar a origem
    public String getOrigem() {
        if (abastecimento != null) return "ABASTECIMENTO";
        if (manutencao != null) return "MANUTENCAO";
        if (viagem != null) return "VIAGEM";
        return "MANUAL"; 
    }
    
    // Adicione este método para evitar loop infinito
    @Override
    public String toString() {
        return "Custo{id=" + id + 
               ", data=" + data + 
               ", valor=" + valor + 
               ", tipo=" + tipo + 
               ", status=" + status + "}";
    }
   
}