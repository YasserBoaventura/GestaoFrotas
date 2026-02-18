package com.GestaoRotas.GestaoRotas.DTO;

import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustoUpdateDTO {
    
	private String descricao;
	private Double valor;
	private TipoCusto tipo;
	private StatusCusto status;
	private String observacoes;
     
}
