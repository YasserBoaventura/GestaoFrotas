package com.GestaoRotas.GestaoRotas.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@Table(name = "marca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Marca {

	

		
	    @Id
	    @GeneratedValue(strategy=GenerationType.IDENTITY)
	 	private Long id;
		private String nome;
		
		
		@OneToMany(mappedBy = "marca")
		private List<Veiculo> veiculo;
       		
	

}
