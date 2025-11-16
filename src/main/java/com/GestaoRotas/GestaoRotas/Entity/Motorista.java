package com.GestaoRotas.GestaoRotas.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "motoristas") 
public class Motorista {
	    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(nullable = false, length = 100)
        private String nome;
        
        @Column(name = "numero_carta", unique = true, length = 20)
        private String numeroCarta;
        
        @Column(unique = true, length = 100)
        private String email;
        
        @Column(length = 20)
        private String telefone;
        
        @Column(name = "data_nascimento")
        private java.time.LocalDate dataNascimento;
        
        // ManyToMany com Veiculo
        @ManyToMany(mappedBy = "motoristas")
        private Set<Veiculo> veiculos = new HashSet<>();
        
        // OneToMany com Viagem 
        @OneToMany(mappedBy = "motorista", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private List<Viagem> viagens = new ArrayList<>();
        
      
        // Métodos auxiliares
        public void addViagem(Viagem viagem) {
            viagens.add(viagem);
            viagem.setMotorista(this);
        }
        
        // Método calculado
        public Long getTotalViagensConcluidas() {  
            return viagens.stream()
                .filter(v -> "CONCLUIDA".equals(v.getStatus()))
                .count();
        }
    }

