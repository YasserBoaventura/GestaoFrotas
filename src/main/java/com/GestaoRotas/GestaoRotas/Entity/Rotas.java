package com.GestaoRotas.GestaoRotas.Entity;
import java.time.Duration;
import java.util.*;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter
@Setter
@Table(name = "rotas")
public class Rotas {
        @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
 
	    private String origem; 
	    
	    private String destino;
	    
	    private double distancia;
	    
	    private Duration duration;
	    
	    

	    // Relacionamento com motoristas
	    @ManyToMany
	    @JoinTable(
	        name = "rotas_motoristas",
	        joinColumns = @JoinColumn(name = "rota_id"),
	        inverseJoinColumns = @JoinColumn(name = "motorista_id")
	    )
	    private List<Motorista> motoristas;

    
}
