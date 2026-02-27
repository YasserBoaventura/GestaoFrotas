package com.GestaoRotas.GestaoRotas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioMotoristaDTO {
 
    private String nomeMotorista;
    private String telefone;
    private String status;
    private Long totalViagens;
    private Double totalQuilometragem;
    private Double totalCombustivel;
    //media combustivel
    private Double mediaCombustivel; 
    
  
 

}
