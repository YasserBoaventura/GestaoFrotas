package com.GestaoRotas.GestaoRotas.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
	    
	    @Column(name = "data_abastecimento")
	    private LocalDateTime dataAbastecimento;
	    
	    @Column(name = "quantidade_litros")
	    private Double quantidadeLitros;
	    
	    @Column(name = "preco_por_litro") 
	    private Double precoPorLitro;
	    
	    @Column(name = "tipo_combustivel", length = 50)
	    private String tipoCombustivel;
	    
	    @Column(name = "kilometragem_veiculo")
	    private Double kilometragemVeiculo;
	    
	    // ManyToOne com Veiculo (OBRIGATÓRIO)
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "veiculo_id", nullable = false)
	    @JsonIgnoreProperties({"abastecimentos", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private Veiculo veiculo;
	    
	    // ManyToOne com Viagem (OPCIONAL)
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "viagem_id", nullable = true)
	    @JsonIgnoreProperties({"abastecimentos", "hibernateLazyInitializer", "handler"}) // ← CORREÇÃO
	    private Viagem viagem; 
	                          

	    //  MÉTODO CALCULADO (não armazenado)  //valor total da favor a pagar
	    public Double getValorTotal() {
	        if (quantidadeLitros != null && precoPorLitro != null) {
	            return quantidadeLitros * precoPorLitro;
	        }
	        return 0.0; 
	    } 
	      
	    // Método auxiliar
	    @PrePersist
	    public void prePersist() {
	        if (dataAbastecimento == null) {
	            dataAbastecimento = LocalDateTime.now();
	        }
	    }
	      
}
	
