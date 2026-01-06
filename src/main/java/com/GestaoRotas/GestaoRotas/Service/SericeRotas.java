package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SericeRotas {

	  
	private final RepositoryRotas repositoryRotas;

	
	public String save(Rotas rotas ) {
	 repositoryRotas.save(rotas);
	  return "salvo com sucesso";
	}
	public String deleteById(long id) {
		repositoryRotas.deleteById(id);
		return "Excluído!', 'A rota foi excluída.', 'success";
	}
	public String update(Rotas rotas, Long id) {
        // Busca a rota existente
        Rotas rotaExistente =  repositoryRotas.findById(id)
            .orElseThrow(() -> new RuntimeException("Rota não encontrada com id: " + id));
            
        // Atualiza apenas os campos permitidos
        rotaExistente.setOrigem(rotas.getOrigem());
        rotaExistente.setDestino(rotas.getDestino());
        rotaExistente.setDistanciaKm(rotas.getDistanciaKm());
        rotaExistente.setTempoEstimadoHoras(rotas.getTempoEstimadoHoras());
        rotaExistente.setDescricao(rotas.getDescricao());
    repositoryRotas.save(rotaExistente);
   return "Motorista salvo com sucesso";
    }
	  public List<Rotas> findAll() {
	return repositoryRotas.findAll();   
	  }
	public Rotas findById(long id) {
	  return this.repositoryRotas.findById(id).get();
	  }
}
