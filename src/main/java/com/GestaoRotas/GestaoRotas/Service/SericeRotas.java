package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;

@Service
public class SericeRotas {

	 
	private final RepositoryRotas repositoryRotas;

	public SericeRotas(RepositoryRotas repositoryRotas) {
		this.repositoryRotas=repositoryRotas;
	}
	public String save(Rotas rotas ) {
	 repositoryRotas.save(rotas);
	  return "salvo com sucesso";
	}
	public String deleteById(long id) {
		repositoryRotas.deleteById(id);
		return "Excluído!', 'A rota foi excluída.', 'success";
	}
	
    public Rotas update(Rotas rotas, Long id) {
        // Busca a rota existente
        Rotas rotaExistente =  repositoryRotas.findById(id)
            .orElseThrow(() -> new RuntimeException("Rota não encontrada com id: " + id));
            
        // Atualiza apenas os campos permitidos
        rotaExistente.setOrigem(rotas.getOrigem());
        rotaExistente.setDestino(rotas.getDestino());
        rotaExistente.setDistanciaKm(rotas.getDistanciaKm());
        rotaExistente.setTempoEstimadoHoras(rotas.getTempoEstimadoHoras());
        rotaExistente.setDescricao(rotas.getDescricao());
        
        return  repositoryRotas.save(rotaExistente);
    }
	  public List<Rotas> findAll() {
	   List<Rotas> lista=this.repositoryRotas.findAll();
		return lista;  
	  }
	public Rotas findById(long id) {
	  return this.repositoryRotas.findById(id).get();
	  }
}
