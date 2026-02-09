package com.GestaoRotas.GestaoRotas.CustoDTO;

import java.time.LocalDate;

import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
public class CustoRequestDTO { 

	    private Long veiculoId;
	
	    private LocalDate data;
	    
	    @NotBlank
	    @Size(min = 3, max = 200)
	    private String descricao;
	    
	    @NotNull
	    @Min(0)
	    @Column(nullable = false)
	    private Double valor;
	    
	    @NotNull 
	    private TipoCusto tipo;
	    
	    private StatusCusto status;
	    
	    private Long abastecimentoId;
	    private Long manutencaoId;
	    private Long viagemId;
	    
	    @Size(max = 500)
	    private String observacoes;
	    
	    private String numeroDocumento;


}
