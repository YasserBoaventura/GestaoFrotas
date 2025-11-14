package com.GestaoRotas.GestaoRotas.Entity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="viagens")
public class Viagem {

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;
    private Double combustivelUsado;
    private Double quilometragem;
 
    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
       
    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista; 
 
    @ManyToOne
    @JoinColumn(name = "rota_id", nullable = false)
    private Rotas rota;

    
}                                  
