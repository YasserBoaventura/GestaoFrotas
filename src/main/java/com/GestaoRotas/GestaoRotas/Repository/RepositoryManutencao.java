package com.GestaoRotas.GestaoRotas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;

public interface RepositoryManutencao  extends JpaRepository<Manutencao, Long>{

    // Buscar todas as manutenções de um veículo
    List<Manutencao> findByVeiculoId(Long veiculoId);
    
    //Busca pelo tipo da manutencao
    List<Manutencao>  findBytipoManutencao (String tipoManutencao);

}