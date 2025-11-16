package com.GestaoRotas.GestaoRotas.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "marca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Marca {

//------..>
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@Column(nullable = false, unique = true, length = 100)
	private String nome;

	@Column(name = "pais_origem", length = 50)
	private String paisOrigem;
	
	@OneToMany(mappedBy = "marca", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    private List<Veiculo> veiculos = new ArrayList<>();
	    
	  
  
    // Métodos 
public void addVeiculo(Veiculo veiculo) {
    veiculos.add(veiculo);
    veiculo.setMarca(this);
  }

public void removeVeiculo(Veiculo veiculo) {
    veiculos.remove(veiculo);
    veiculo.setMarca(null);

   }


}