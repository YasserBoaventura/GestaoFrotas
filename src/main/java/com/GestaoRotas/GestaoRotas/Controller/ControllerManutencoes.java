package com.GestaoRotas.GestaoRotas.Controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.DTO.concluirManutencaoRequest;

import com.GestaoRotas.GestaoRotas.DTO.manuntecaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Service.ServiceManutencoes;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController 
@RequestMapping("/api/manutencoes") 
@RequiredArgsConstructor
@CrossOrigin("*")
public class ControllerManutencoes {

    private final ServiceManutencoes manutencaoService;

  @PostMapping("/save")
public ResponseEntity<String> cadastrar(@RequestBody manuntecaoDTO manutencaoDTO) {
   try {
     return  ResponseEntity.ok(manutencaoService.salvar(manutencaoDTO));
	 }catch(Exception e) {
		   e.printStackTrace();
		  return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
   }
	    	
}
  @PutMapping("/update/{id}")
  public ResponseEntity<String>update(@PathVariable long id ,@RequestBody manuntecaoDTO manutencaoDTO ){
	  try{
     return ResponseEntity.ok(manutencaoService.update(manutencaoDTO, id));
	  }catch(Exception e) {
	  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}  
  } 

@GetMapping("/findByIdVeiculo/{veiculoId}")
public ResponseEntity<List<Manutencao>> listarPorVeiculo(@PathVariable long veiculoId) {
try {
	return ResponseEntity.ok(manutencaoService.listarPorVeiculo(veiculoId)); 
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
@PutMapping("/iniciarManutencao/{id}")
public ResponseEntity<Map<String , String>> iniciarManutencao(@PathVariable Long id){
	try {  
    Map<String,String> response =  manutencaoService.iniciarManutencao(id);
	      return ResponseEntity.status(HttpStatus.CREATED).body(response);
	      }catch(Exception e) { 
		Map<String, String> erro = new HashMap<>();
		 erro.put("message", "erro ao inicializar a manutencao");
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
		      
		}
}
@PutMapping("/concluirManutencao/{id}")
public ResponseEntity<Map<String , String>> concluirManutencao(@RequestBody String observacoes, @PathVariable Long id){
	try { 
   return ResponseEntity.ok(manutencaoService.concluirManutencao(id, observacoes));
      }catch(Exception e) {
	Map<String, String> erro = new HashMap<>();
	 erro.put("erro","erro ao concluir manutencao");
	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	    }
}
@PutMapping("/cancelarManutencao/{id}") 
public ResponseEntity<Map<String, String>> cancelarManutencao(@RequestBody String observacoes, @PathVariable Long id ){
 try {   
   return ResponseEntity.ok(manutencaoService.cancelarManutencao(id, observacoes)); 
		}catch(IllegalArgumentException e) {
		Map<String ,String> erro = new HashMap<>();  
		e.getMessage();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
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
     return  ResponseEntity.ok(manutencaoService.findById(id));
    	}catch(Exception e) {
    	return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    	}
    }  
    //ver relatorio de  manutencoes por veiculo
    @PreAuthorize("hasAuthority('ADMIN')")  
    @GetMapping("/por-veiculo") 
    public ResponseEntity<List<RelatorioManutencaoDTO>> relatorioPorVeiculo() {
        return ResponseEntity.ok(manutencaoService.gerarRelatorioPorVeiculo());
    }                       
    @GetMapping("/relatorio-por-periodo") 
    @PreAuthorize("hasAuthority('ADMIN')")  
   public ResponseEntity<List<RelatorioManutencaoDTO>> relatorioPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        System.out.println("Recebendo requisição com datas: " + inicio + " até " + fim); // Para debug
        return ResponseEntity.ok(manutencaoService.relatorioPorPeriodo(inicio, fim)); 
    }           
    @PreAuthorize("hasAuthority('ADMIN')")     
    @GetMapping("/gerarAltertas")
    public ResponseEntity<List<String>> getAlertas() { 
        return ResponseEntity.ok(manutencaoService.gerarAlertas());
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

	    
	    
	    
	




