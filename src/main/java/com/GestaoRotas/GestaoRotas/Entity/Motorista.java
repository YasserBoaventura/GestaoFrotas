package com.GestaoRotas.GestaoRotas.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	        
	@Column(length = 20, unique = true) 
	private String telefone;
	  
	@Column(name = "data_nascimento")
	private java.time.LocalDate dataNascimento;
	
	//Precisamos saber quis tipos de veiculos que o mostorista pode utilizar devido ao seu nivel de Habiitacao
	@Column(name="categoria_habilitacao",  length = 45)
	private String  categoriaHabilitacao;
	 
	@Enumerated(EnumType.STRING)
    @JsonProperty("statusMotorista") // This maps JSON "statusMotorista" to Java "status"
    @Column(name="status" , nullable = false)
	private statusMotorista status;
	
	
	// ManyToMany com Veiculo
	@ManyToMany(mappedBy = "motoristas")
	 @JsonIgnore
	private Set<Veiculo> veiculos = new HashSet<>();
	
	// OneToMany com Viagem 
	 @JsonIgnore
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

