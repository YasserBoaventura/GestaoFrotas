package com.GestaoRotas.GestaoRotas.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Service.ServiceManutencoes;

import java.util.List;

@RestController
@RequestMapping("/manutencoes")
public class ControllerManutencoes {

    private final ServiceManutencoes manutencaoService;
 
	@Autowired
	public ControllerManutencoes(ServiceManutencoes  manutencaoService) {
		this.manutencaoService=manutencaoService;
	}
  @PostMapping("/save")
public ResponseEntity<String> cadastrar(@RequestBody Manutencao manutencao) {
   try {
	   String frase=this.manutencaoService.salvar(manutencao);
	  if(frase.isEmpty()|| frase.isBlank()) {
		  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	  } 
	  else {
	   return new ResponseEntity<>(frase, HttpStatus.OK);
	  }
	   }catch(Exception e) {
		  return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
   }
	    	
}

@GetMapping("/findByIdVeiculo/{veiculoId}")
public ResponseEntity<List<Manutencao>> listarPorVeiculo(@PathVariable long veiculoId) {
try {
	List<Manutencao>  lista=this.manutencaoService.listarPorVeiculo(veiculoId);
	if(lista.isEmpty()) {
		 return new ResponseEntity<>(HttpStatus.NO_CONTENT);	    	
		} else {
	 	return new ResponseEntity<>(lista, HttpStatus.OK);
		}
	    	
	    }catch(Exception e) {
	    	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	    }
	    } 

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            String frase = manutencaoService.deleteById(id);

            if (frase.equals("Manutenção não encontrada")) {
                return new ResponseEntity<>(frase, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(frase, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar manutenção", HttpStatus.BAD_REQUEST);
    }
    }  
    @GetMapping("/findAll")
    public ResponseEntity<List<Manutencao>>  findAll(){
    	 try {
    	List<Manutencao> lista=this.manutencaoService.findAll();
    	if(lista.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}else {
    		return new ResponseEntity<>(lista, HttpStatus.OK);
    	}
    	 }catch(Exception e) {
    		return new ResponseEntity<>(null,  HttpStatus.BAD_REQUEST); 
    	 }
    	 
    }
	 //  Buscar manutenções por tipo
    @GetMapping("/tipo/{tipoManutencao}")
    public ResponseEntity<List<Manutencao>> listarPorTipo(@PathVariable String tipoManutencao) {
        try {
            List<Manutencao> lista = manutencaoService.listarPorTipo(tipoManutencao);
            
            if (lista.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(lista, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/findById/{id}")
    public ResponseEntity <Manutencao> findById(@PathVariable long id){
    	 try {
    	Manutencao  manutencao=this.manutencaoService.findById(id);
    	return new ResponseEntity<>(manutencao, HttpStatus.OK);
    	}catch(Exception e) {
    	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    		   
    	}
    }
    
    
    
    
}

	    
	    
	    
	




