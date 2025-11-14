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

@Entity
@Table(name = "veiculos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {  

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String placa;
	private String modelo;
	private Integer ano;
	private String tipo;
	
    @ManyToOne 
    @JoinColumn(name = "marca_id", nullable = false)
	private  Marca marca;
	
    
    
    @ManyToOne 
    @JoinColumn(name= "motorista_id")
    private Motorista motorista;
    
    
       
    @OneToMany(mappedBy = "veiculo")
    private List<Manutencao> manutencoes;
}



