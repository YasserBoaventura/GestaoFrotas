package com.GestaoRotas.GestaoRotas.Entity;
import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity 
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rota")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rotas {   
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String origem;
    
    @Column(nullable = false, length = 100)
    private String destino;
    
    @Column(name = "distancia_km")
    private Double distanciaKm;
    
    @Column(name = "tempo_estimado_horas")
    private Double tempoEstimadoHoras;
    
    private String descricao;
    
    // OneToMany com Viagem 
    @JsonIgnore
    @OneToMany(mappedBy = "rota", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Viagem> viagens = new ArrayList<>();

  // Métodos auxiliares
    public void addViagem(Viagem viagem) {
        viagens.add(viagem);
        viagem.setRota(this); 
    }
    @Column(name = "totalViagens")
    private Long TotalViagens = getTotalViagens();
    
    // Método calculado
    public Long getTotalViagens() { 
        return (long) viagens.size();
         
    } 
    
}