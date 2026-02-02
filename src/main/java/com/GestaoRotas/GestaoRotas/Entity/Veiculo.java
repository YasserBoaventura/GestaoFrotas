package com.GestaoRotas.GestaoRotas.Entity;



import jakarta.persistence.*;
import java.util.*;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.StatusVeiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor 
@Table(name = "veiculo")
public class Veiculo {  
 //------->
        @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(nullable = false, length = 100)
	    private String modelo;
	    
	    @Column(unique = true, length = 20)
	    private String matricula;
	    
	    @Column(name = "ano_fabricacao")
	    private Integer anoFabricacao;
	    
	    @Column(name = "capacidade_tanque")
	    private Double capacidadeTanque;
	    
	    @Column(name = "kilometragem_atual")
	    private Double kilometragemAtual;
	    
	    
	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "marca_id")
	    private Marca marca;
	    
	    @Column(nullable = false, length = 255)
	    private String status = "DISPONIVEL"; // DISPONIVEL, EM_VIAGEM, EM_MANUTENCAO, MANUTENCAO_VENCIDA, MANUTENCAO_PROXIMA
	     
	    private LocalDateTime dataAtualizacaoStatus;
	     
	    
	    // novos campos pra o controle de custos
	    
	    @Column
	    private Double custoTotal;
	    
	    @Column
	    private Double custoMedioPorKm;  
	    
	    @Column(name = "ultima_atualizacao_custo")
	    private LocalDateTime ultimaAtualizacaoCusto;
	    
	    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL)
	    @JsonIgnore 
	    @JsonIgnoreProperties({"veiculo", "hibernateLazyInitializer", "handler"})
	    private List<Custo> custos = new ArrayList<>();
	    
	    @Column(name = "custo_combustivel")
	    private Double custoCombustivel;
	     
	    @Column(name = "custo_manutencao")
	    private Double custoManutencao;
	    
	    @Column(name = "custo_pedagios")
	    private Double custoPedagios;
	    
	    @Column(name = "custo_outros")
	    private Double custoOutros;
	    
	    ////////////////////////////////////////////////
	    
	    // OneToMany com Abastecimento
	 @JsonIgnore
     @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	 private List<abastecimentos> abastecimentoss = new ArrayList<>();
 	     
	    // OneToMany com Manutencao
	    @JsonIgnore
        @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<Manutencao> manutencoes = new ArrayList<>();
	    @JsonIgnore
	    // OneToMany com Viagem
	    @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<Viagem> viagens = new ArrayList<>();
	   
	    @JsonIgnore
     // ManyToMany com Motorista  
	    @ManyToMany  
	    @JoinTable(  
	        name = "veiculo_motorista",
	        joinColumns = @JoinColumn(name = "veiculo_id"),
	        inverseJoinColumns = @JoinColumn(name = "motorista_id")
	    )
	    private Set<Motorista> motoristas = new HashSet<>();
	     // Métodos auxiliares
	    public Veiculo(String modelo, String matricula, Integer anoFabricacao,
	               Double capacidadeTanque, Double kilometragemAtual) {
	    this.modelo = modelo;  
	    this.matricula = matricula; 
	    this.anoFabricacao = anoFabricacao;
	    this.capacidadeTanque = capacidadeTanque;
	    this.kilometragemAtual = kilometragemAtual;
	}

	    public void addMotorista(Motorista motorista) {
	        motoristas.add(motorista);
	        motorista.getVeiculos().add(this);
	    } 
	    
	    public void removeMotorista(Motorista motorista) {
	        motoristas.remove(motorista);
	        motorista.getVeiculos().remove(this);
	    }
	                  
	    public void addAbastecimento(abastecimentos abastecimento) {
	        abastecimentoss.add(abastecimento);
	        abastecimento.setVeiculo(this);
	    } 
	    //remover o abastecimentos ainda a ser implementada
	    public void removerAbastecimento(abastecimentos abastecimentos) {
	    	abastecimentoss.remove(abastecimentos);
	    	
	    } 
	     // Método calculado
	   
	    public Double getMediaConsumo() {
	        if (abastecimentoss == null || abastecimentoss.isEmpty()) return 0.0;
	         
	        Double totalLitros = abastecimentoss.stream()
	            .mapToDouble(abastecimentos::getQuantidadeLitros)
	            .sum();
	        
	        Long totalViagens = viagens.stream()
	            .filter(v -> "CONCLUIDA".equals(v.getStatus()))
	            .count();
	            
	        return totalViagens > 0 ? totalLitros / totalViagens : 0.0;
	    }
	    // Método pra calcular custos depois que ele carrega o banco
@PostLoad 
public void calcularCustos() {  
    if (this.custos != null && !this.custos.isEmpty()) {
        this.custoTotal = this.custos.stream()
            .filter(c -> c.getStatus() == StatusCusto.PAGO)
            .mapToDouble(Custo::getValor)
            .sum();
        
        // Calcular por tipo 
        this.custoCombustivel = this.custos.stream()
            .filter(c -> c.getStatus() == StatusCusto.PAGO && 
                       c.getTipo() == TipoCusto.COMBUSTIVEL)
            .mapToDouble(Custo::getValor)
            .sum();
        
        this.custoManutencao = this.custos.stream()
            .filter(c -> c.getStatus() == StatusCusto.PAGO && 
                       (c.getTipo() == TipoCusto.MANUTENCAO_PREVENTIVA || 
                        c.getTipo() == TipoCusto.MANUTENCAO_CORRETIVA))
            .mapToDouble(Custo::getValor)
            .sum(); 
        
        this.custoPedagios = this.custos.stream()
            .filter(c -> c.getStatus() == StatusCusto.PAGO && 
                       c.getTipo() == TipoCusto.PEDAGIO)
            .mapToDouble(Custo::getValor)
            .sum();
        
        this.custoOutros = this.custoTotal - 
            (this.custoCombustivel + this.custoManutencao + this.custoPedagios);
        
        if (this.kilometragemAtual != null && this.kilometragemAtual > 0) {
            this.custoMedioPorKm = this.custoTotal / this.kilometragemAtual;
        }
    }
}
	    
	}
	