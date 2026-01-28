package com.GestaoRotas.GestaoRotas.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
	    private LocalDate dataAbastecimento;
	     
	    @Column(name = "quantidade_litros")
	    private Double quantidadeLitros;
	    
	    @Column(name = "preco_por_litro")  
	    private Double precoPorLitro;
	    
	    @Column(name = "tipo_combustivel", length = 50)
	    private String tipoCombustivel;
	     
 
	    @Column(name = "status")
	    @Enumerated(EnumType.STRING)  
	    private  statusAbastecimentos  statusAbastecimento;
	    @Column(name = "kilometragem_veiculo")
	    private Double kilometragemVeiculo;
	    
	    // ManyToOne com Veiculo (OBRIGATÓRIO)
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "veiculo_id", nullable = false)
	    @JsonIgnoreProperties({"abastecimentos", "hibernateLazyInitializer", "handler"}) 
	    private Veiculo veiculo;  
	     
	    // ManyToOne com Viagem   
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "viagem_id", nullable = true) 
	    @JsonIgnoreProperties({"abastecimentos", "hibernateLazyInitializer", "handler"}) 
	    private Viagem viagem; 
	    
	    
	    //novos campos para o custo
	    
	    // NOVO: relacionamento com Custo
	    @OneToOne(mappedBy = "abastecimento", cascade = CascadeType.ALL)
	    @JsonIgnoreProperties({"abastecimentos", "hibernateLazyInitializer", "handler"})
	    @JsonBackReference 
	    private Custo custo;
	     
	    // Método para criar custo automaticamente
	    /*
	    @PostPersist
	    public void criarCustoAutomatico() {
	        if (this.custo == null) {  
	            Custo custoCombustivel = new Custo();
	            custoCombustivel.setData(this.dataAbastecimento);
	            custoCombustivel.setDescricao("Abastecimento - " + this.getTipoCombustivel());
	            custoCombustivel.setValor(this.getValorTotal());
	            custoCombustivel.setTipo(TipoCusto.COMBUSTIVEL);
	            custoCombustivel.setStatus(StatusCusto.PAGO);
	            custoCombustivel.setVeiculo(this.getVeiculo());
	            custoCombustivel.setAbastecimento(this);
	            custoCombustivel.setViagem(this.getViagem()); 
	            custoCombustivel.setNumeroDocumento("ABS-" + this.getId());
	            
	            this.custo = custoCombustivel;  
	            // O problema é que aqui NÃO está salvando no banco!
	        }
	    }
	    */
	    /////////////
	 
	    //valor total da favor a pagar
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
	          
	        }
	    }
	      
}
	
