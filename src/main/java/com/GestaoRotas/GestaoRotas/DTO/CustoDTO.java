package com.GestaoRotas.GestaoRotas.DTO;
import java.time.*;

import com.GestaoRotas.GestaoRotas.Custos.Custo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustoDTO { 
  
	private Long id;
    private LocalDate data;
    private String descricao;
    private Double valor;
    private String tipo;
    private String status;
    private Long veiculoId;
    private String veiculoMatricula;
    private String observacoes;
    private String numeroDocumento;
     
public static CustoDTO fromEntity(Custo custo) {
    CustoDTO dto = new CustoDTO();
    dto.setId(custo.getId());
    dto.setData(custo.getData());
    dto.setDescricao(custo.getDescricao());
    dto.setValor(custo.getValor());
    dto.setTipo(custo.getTipo().name());
    dto.setStatus(custo.getStatus().name());
    dto.setObservacoes(custo.getObservacoes());
    dto.setNumeroDocumento(custo.getNumeroDocumento());
    
    if (custo.getVeiculo() != null) {
        dto.setVeiculoId(custo.getVeiculo().getId());
        dto.setVeiculoMatricula(custo.getVeiculo().getMatricula());
    }
   return dto;
    }

}
