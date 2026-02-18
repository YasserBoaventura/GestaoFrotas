package com.GestaoRotas.GestaoRotas.CustoDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.*;
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustoListDTO {
    private Long id;
    private LocalDate data;
    private String descricao;
    private Double valor;
    private String tipo;
    private String status;
    private Long veiculoId;
    private String veiculoMatricula;
    private String origem;
    
    // Construtor para JPQL
    public CustoListDTO(Long id, LocalDate data, String descricao, Double valor, 
                       com.GestaoRotas.GestaoRotas.Model.TipoCusto tipo, 
                       com.GestaoRotas.GestaoRotas.Model.StatusCusto status, 
                       Long veiculoId, String veiculoMatricula, String origem) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo != null ? tipo.name() : null;
        this.status = status != null ? status.name() : null;
        this.veiculoId = veiculoId;
        this.veiculoMatricula = veiculoMatricula;
        this.origem = origem;
    }

}
