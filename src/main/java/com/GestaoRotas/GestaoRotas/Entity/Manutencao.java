package com.GestaoRotas.GestaoRotas.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manutencoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manutencao {
      //------> namuntecao
        @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(name = "data_manutencao")
	    private LocalDate dataManutencao;
	     
	    @Column(name = "tipo_manutencao", length = 50)
	    @Enumerated(EnumType.STRING)
	    private TipoManutencao tipoManutencao; // "PREVENTIVA", "CORRETIVA", "TROCA_OLEO", "REVISAO"
	    
	    @Column(length = 500)
	    private String descricao;
	    
	    private Double custo;
	    
	    @Column(name = "kilometragem_veiculo")
	    private Double kilometragemVeiculo;
	
	    
	    //  CAMPOS NOVOS NECESSÁRIOS
	    @Column(name = "proxima_manutencao_km")
	    private Integer proximaManutencaoKm;
	    
	    @Column(name = "proxima_manutencao_data")
	    private LocalDate proximaManutencaoData;
	    
	    @Column(name = "dataConclusao")
	    private LocalDateTime dataConclusao;
	    @Column(name ="dataInicio")
	    private LocalDateTime dataInicio;
	    
	    // ManyToOne com Veiculo
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "veiculo_id", nullable = false)
	    @JsonIgnoreProperties({"Manutencao", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private Veiculo veiculo;
	    

	    @Enumerated(EnumType.STRING)  
	    private statusManutencao status;
	    
    public Manutencao(Veiculo veiculo, String tipoManutencao, String descricao, Double custo) {
        this.veiculo = veiculo;
       // this.tipoManutencao = tipoManutencao;
        this.descricao = descricao;
        this.custo = custo;
        this.dataManutencao = LocalDate.now();
    }
    
  
    // Método auxiliar
    @PrePersist
    public void prePersist() {
        if (dataManutencao == null) {
            dataManutencao = LocalDate.now();
        }
    }
    //  Método corrigido - use este nome no seu código
    public LocalDate getProximaRevisao() {
        return this.proximaManutencaoData;
    }
	    
	    // Método auxiliar para verificar se está vencida
	    public boolean isVencida() {
	        if (proximaManutencaoData != null && proximaManutencaoData.isBefore(LocalDate.now())) {
	            return true;
	        }  
	        if (proximaManutencaoKm != null && veiculo != null && veiculo.getKilometragemAtual() != null && 
	            veiculo.getKilometragemAtual() >= proximaManutencaoKm) {
	            return true;
	        }
	        return false; 
	    }
	
}