package com.GestaoRotas.GestaoRotas.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name="viagens")
public class Viagem {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(name = "data_hora_partida")
	    private LocalDateTime dataHoraPartida;
	    
	    @Column(name = "data_hora_chegada")
	    private LocalDateTime dataHoraChegada; 
	    
	    @Column(length = 20)
	    private String status; // "PLANEADA", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"
	    
	    @Column(name = "kilometragem_inicial")
	    private Double kilometragemInicial;
	    
	    @Column(name = "kilometragem_final")
	    private Double kilometragemFinal;
	    private String observacoes;
	    
	    private LocalDateTime data;
	    
	    @Column(name = "custo_pedagios" ,nullable = true) 
	    private Double custoPedagios;
	      
	    @Column(name = "valor", nullable = false) 
	    private Double valor;
	    
	    // ManyToOne com Motorista 

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "motorista_id", nullable = false)
	    @JsonIgnoreProperties({"viagem", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private Motorista motorista;
	    
	    // ManyToOne com Veiculo
	
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "veiculo_id", nullable = false)
	    @JsonIgnoreProperties({"viagem", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private Veiculo veiculo;
	    
	    // ManyToOne com Rota   o lado N que e as viagens que podem ter varias routas

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JsonIgnoreProperties({"viagem", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    @JoinColumn(name = "rota_id", nullable = false)
	    private Rotas rota;
	     
	    // OneToMany com Abastecimento
	  @OneToMany(mappedBy = "viagem", cascade = {CascadeType.PERSIST})
	    @JsonIgnoreProperties({"viagem", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private List<abastecimentos> abastecimentos = new ArrayList<>();
	    

	    public Viagem(Motorista motorista, Veiculo veiculo, Rotas rota, LocalDateTime dataHoraPartida) {
	        this.motorista = motorista;
	        this.veiculo = veiculo;
	        this.rota = rota;
	        this.dataHoraPartida = dataHoraPartida;
	        this.status = "PLANEADA";
	    }
	    
	
	    // Métodos de negócio
	    public void iniciarViagem() {
	        this.status = "EM_ANDAMENTO";
	        this.dataHoraPartida = LocalDateTime.now();
	    } 
	    
	    public void concluirViagem() {
	        this.status = "CONCLUIDA";
	        this.dataHoraChegada = LocalDateTime.now();
	    }
	    
	    public void cancelarViagem() {
	        this.status = "CANCELADA";
	    }
	    
	    // Método calculado
	    public Double getDistanciaPercorrida() {
	        if (kilometragemInicial != null && kilometragemFinal != null) {
	            return kilometragemFinal - kilometragemInicial;
	        }
	        return 0.0;      
	    }
	    
	    public void addAbastecimento(abastecimentos abastecimento) {
	        abastecimentos.add(abastecimento);
	        abastecimento.setViagem(this);
	    }
	 
	}


    
                               
