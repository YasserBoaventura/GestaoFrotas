package com.GestaoRotas.GestaoRotas.Entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "abastecimentos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class abastecimentos {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private LocalDate data;

	    private Double quantidade; // litros abastecidos

	    private Double valorTotal; // valor pago no abastecimento

	    private Double precoPorLitro; // opcional, pode ser calculado: valorTotal / quantidade

	    @ManyToOne
	    @JoinColumn(name = "veiculo_id", nullable = false)
	    private Veiculo veiculo;
}
