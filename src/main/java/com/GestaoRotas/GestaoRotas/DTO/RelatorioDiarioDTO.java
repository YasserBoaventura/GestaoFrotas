package com.GestaoRotas.GestaoRotas.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
public class RelatorioDiarioDTO {
    
	private Object dataHoraPartida;
    private Long quantidadeViagens;
  private  Double kilometragemInicial;
   private Double quantidadeLitros;
    
    public RelatorioDiarioDTO(Object data,
            Long totalViagens,
            Double totalKm,
            Double totalLitros) {
if (data instanceof java.sql.Date d) {
this.dataHoraPartida = d.toLocalDate();
} else if (data instanceof LocalDate ld) {
this.dataHoraPartida = ld;
}

this.quantidadeViagens = totalViagens;
this.kilometragemInicial = totalKm;
this.quantidadeLitros = totalLitros;
}

 
}
