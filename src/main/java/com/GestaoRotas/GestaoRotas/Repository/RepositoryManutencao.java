package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;

@Repository
public interface RepositoryManutencao  extends JpaRepository<Manutencao, Long>{

    // Buscar todas as manutenções de um veículo
    List<Manutencao> findByVeiculoId(Long veiculoId);
    
    //Busca pelo tipo da manutencao
    List<Manutencao>  findBytipoManutencao (String tipoManutencao);
   
    //relatorio de manuntencoes feitas por cada veiculo
     @Query("SELECT new com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO(" +
    	       "m.veiculo.placa, COUNT(m), SUM(m.custo), AVG(m.custo)) " +
    	       "FROM Manutencao m WHERE m.veiculo IS NOT NULL GROUP BY m.veiculo.placa")
    	List<RelatorioManutencaoDTO> relatorioPorVeiculo();
     
    
     // Alertas – manutenções atrasadas 
     @Query("SELECT m FROM Manutencao m WHERE m.proxima_revisao< CURRENT_DATE")
     List<Manutencao> findManutencoesVencidas();
 
     // Próximas manutenções dentro de 30  dias    
     @Query(value = "SELECT * FROM manutencoes  m WHERE m.proxima_revisao BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY)", nativeQuery = true)
     List<Manutencao> findProximasManutencoes();

        //buscar as manuntencoes da proxima semana
     @Query(value = "SELECT * FROM manutencoes m WHERE m.proxima_revisao BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL 7 DAY", nativeQuery = true)
     List<Manutencao> findManutencoesProximas7Dias();

}