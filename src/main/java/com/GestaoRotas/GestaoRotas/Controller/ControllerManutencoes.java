package com.GestaoRotas.GestaoRotas.Controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceManutencoes;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/manutencoes")
public class ControllerManutencoes {

    private final ServiceManutencoes manutencaoService;
    
   
 
	public ControllerManutencoes(ServiceManutencoes  manutencaoService) {
		this.manutencaoService=manutencaoService;
	}
  @PostMapping("/save")
public ResponseEntity<Manutencao> cadastrar(@RequestBody manuntecaoDTO manutencaoDTO) {
   try {
	   Manutencao manutencao=this.manutencaoService.salvar(manutencaoDTO);
      return  ResponseEntity.ok(manutencao);
	 }catch(Exception e) {
		   e.printStackTrace();
		  return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
   }
	    	
}
  @PutMapping("/update/{id}")
  public ResponseEntity<Manutencao>update(@RequestBody manuntecaoDTO manutencaoDTO, @PathVariable long id){
	  try{
 Manutencao  manutencao=this.manutencaoService.update(manutencaoDTO, id);
		  return  ResponseEntity.ok(manutencao);
	  }catch(Exception e) {
	  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

@DeleteMapping("/delete/{id}")
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
    public ResponseEntity<Manutencao> findById(@PathVariable long id){
    	 try {  
    	Manutencao  manutencao=this.manutencaoService.findById(id);
    	return new ResponseEntity<>(manutencao, HttpStatus.OK);
    	}catch(Exception e) {
    	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    		}          
    }  
    //ver relatorio de manutencoes por veiculo

    @GetMapping("/relatorio/veiculos")
    public ResponseEntity<List<RelatorioManutencaoDTO>> relatorioPorVeiculo() {
        return ResponseEntity.ok(manutencaoService.gerarRelatorioPorVeiculo());
    }                 
    
    @GetMapping("/gerarAltertas")
    public ResponseEntity<List<String>> getAlertas() {
        List<String> alertas = manutencaoService.gerarAlertas();
        return ResponseEntity.ok(alertas);
    }
    @GetMapping("/alertas/simplificado")  //os dois geram alertas mais esse simplificado
    public ResponseEntity<List<String>> getAlertasSimplificado() {
        List<String> alertas = manutencaoService.gerarAlertasSimplificado();
        return ResponseEntity.ok(alertas);
    }
    
    //novas  consultas
    @GetMapping("/vencidas")
    public ResponseEntity<List<Manutencao>> vencidas() {
        return ResponseEntity.ok(manutencaoService.buscarVencidas());
    }

    @GetMapping("/proximas")
    public ResponseEntity<List<Manutencao>> proximas30Dias() {
        return ResponseEntity.ok(manutencaoService.buscarProximas30Dias());
    }

    @GetMapping("/proximas/7dias")
    public ResponseEntity<List<Manutencao>> proximas7Dias() {
        return ResponseEntity.ok(manutencaoService.buscarProximas7Dias());
    }
    
}

	    
	    
	    
	




