package com.GestaoRotas.GestaoRotas.Entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;

import jakarta.persistence.Column;
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
@Table(name = "manutencoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "proxima_revisao") 
    private LocalDate proxima_revisao;

    private Integer quilometragem;

    @Enumerated(EnumType.STRING)  //Preventiva e Corretiva
    private TipoManutencao tipoManutencao;
   

    @Column(length = 500) 
    private String descricao;
                             
    private Double custo;

    private String oficina;

    // Muitas manutenções estão ligadas a 1 veículo
    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;
    


}
 

