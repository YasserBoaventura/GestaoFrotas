package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.TipoManutencao;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class manuntecaoDTO {
	private Long id;
    private LocalDate dataManutencao;
    @Enumerated(EnumType.STRING)
    private TipoManutencao tipoManutencao; // "PREVENTIVA", "CORRETIVA", "TROCA_OLEO", "REVISAO"
 
    @Enumerated(EnumType.STRING)
    private statusManutencao status; 
    private String descricao;
    private Double custo;  
    private Double kilometragemVeiculo;
    private Double proximaManutencaoKm;
    private LocalDate proximaManutencaoData;
    private Long veiculo_id; 
    
   
	

}
