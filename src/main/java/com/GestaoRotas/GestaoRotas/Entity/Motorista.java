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

	    private String nome;
         
	    private String cnh;  

	    private String categoria; // Categoria da CNH

	    private String contato;

	    // Relação com Veiculos (um motorista pode ter vários veículos)
	    @OneToMany(mappedBy = "motorista")
	    @JsonIgnore
	    private List<Veiculo> veiculos;

	    // Relação com Rotas (um motorista pode estar em várias rotas)
	    @ManyToMany(mappedBy = "motoristas")
	    private List<Rotas> rotas;

	    @OneToMany(mappedBy = "")
	    private List<Viagem> viagens;
}
