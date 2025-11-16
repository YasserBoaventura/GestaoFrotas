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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	    
	    
	    // ManyToOne com Marca
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "marca_id", nullable = false)
	    private Marca marca;
	    
	    // OneToMany com Abastecimento
	    @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<abastecimentos> abastecimentoss = new ArrayList<>();
	    
	    // OneToMany com Manutencao
	    @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<Manutencao> manutencoes = new ArrayList<>();
	    
	    // OneToMany com Viagem
	    @OneToMany(mappedBy = "veiculo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<Viagem> viagens = new ArrayList<>();
	    
	    // ManyToMany com Motorista
	    @ManyToMany
	    @JoinTable(
	        name = "veiculo_motorista",
	        joinColumns = @JoinColumn(name = "veiculo_id"),
	        inverseJoinColumns = @JoinColumn(name = "motorista_id")
	    )
	    private Set<Motorista> motoristas = new HashSet<>();
	     // Métodos auxiliares
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
	    
	    
	}
	