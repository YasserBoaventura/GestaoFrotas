package com.GestaoRotas.GestaoRotas.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
	private String nome;
	  
	@Column(name = "numero_carta", unique = true, length = 20)
	@NotBlank(message = "numero de carta é obrigatorio")
	private String numeroCarta;
	  
	@Column(unique = true, length = 100)
    @NotBlank(message = "Email é obrigatório")
	private String email;  
	        
	@Column(length = 20, unique = true) 
	private String telefone;
	  
	@Column(name = "data_nascimento")
	@Past(message = "Data de nascimento deve ser no passado")
	private java.time.LocalDate dataNascimento;
	
	//Precisamos saber quis tipos de veiculos que o mostorista pode utilizar devido ao seu nivel de Habiitacao
	@Column(name="categoria_habilitacao",  length = 45)
    @Pattern(regexp = "A|B|C|D|E", message = "Categoria deve ser A, B, C, D ou E")
	private String  categoriaHabilitacao;
	 
	@Enumerated(EnumType.STRING)
    @JsonProperty("statusMotorista") // This maps JSON "statusMotorista" to Java "status"
    @Column(name="statusMotorista" , nullable = false)
	private statusMotorista status;
	
	// ManyToMany  com Veiculo
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
	//busga o numero de viagens de cada motorista
     @Transient  
    public Long getTotalViagens() {
        return (long) viagens.size();
    }  
	// Método calculado
	public Long getTotalViagensConcluidas() {  
	    return viagens.stream()
	        .filter(v -> "CONCLUIDA".equals(v.getStatus()))
	        .count();

    }
    } 

