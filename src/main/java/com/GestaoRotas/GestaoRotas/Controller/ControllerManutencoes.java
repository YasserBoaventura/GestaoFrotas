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

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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
  @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
public ResponseEntity<String> cadastrar(@RequestBody manuntecaoDTO manutencaoDTO) {
return ResponseEntity.ok(manutencaoService.salvar(manutencaoDTO));
  }

  @PutMapping("/update/{id}")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
public ResponseEntity<String> update(@PathVariable Long id, @RequestBody manuntecaoDTO dto) {
    return ResponseEntity.ok(manutencaoService.update(dto, id));
}
@GetMapping("/findByIdVeiculo/{veiculoId}")
public ResponseEntity<List<Manutencao>> listarPorVeiculo(@PathVariable long veiculoId) {
      return ResponseEntity.ok(manutencaoService.listarPorVeiculo(veiculoId));
  }
@DeleteMapping("/delete/{id}")
@PreAuthorize("hasAuthority('ADMIN')")  
   public ResponseEntity<String> excluir(@PathVariable Long id) {
      String frase = manutencaoService.deleteById(id);
   if (frase.equals("Manutenção não encontrada")) {
       return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(frase);
   }
      return ResponseEntity.ok(frase);
    }
@PutMapping("/iniciarManutencao/{id}")
public ResponseEntity<Map<String , String>> iniciarManutencao(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(manutencaoService.iniciarManutencao(id));
}
@PutMapping("/concluirManutencao/{id}")
public ResponseEntity<Map<String , String>> concluirManutencao(@RequestBody String observacoes, @PathVariable Long id){
   return ResponseEntity.ok(manutencaoService.concluirManutencao(id, observacoes));

}
@PutMapping("/cancelarManutencao/{id}") 
public ResponseEntity<Map<String, String>> cancelarManutencao(@RequestBody String observacoes, @PathVariable Long id ){
      return ResponseEntity.ok(manutencaoService.cancelarManutencao(id, observacoes));

}
 @GetMapping("/findAll")
 @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
 public ResponseEntity<List<Manutencao>>  findAll(){
      return  ResponseEntity.ok(manutencaoService.findAll());
  }
	 //  Buscar manutenções por tipo
    @GetMapping("/tipo/{tipoManutencao}")
    public ResponseEntity<List<Manutencao>> listarPorTipo(@PathVariable String tipoManutencao) {
              return  ResponseEntity.ok(manutencaoService.listarPorTipo(tipoManutencao));
    }
     @GetMapping("/findById/{id}") 
    public ResponseEntity<Manutencao> findById(@PathVariable long id){
     return  ResponseEntity.ok(manutencaoService.findById(id));
    }  

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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/gerarAltertas")
   public ResponseEntity<List<String>> getAlertas() {  
      return ResponseEntity.ok(manutencaoService.gerarAlertas());
  }
       
    @GetMapping("/alertas/simplificado")
    public ResponseEntity<List<String>> getAlertasSimplificado() {
        List<String> alertas = manutencaoService.gerarAlertasSimplificado();
        return ResponseEntity.ok(alertas);
    }

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

	    
	    
	    
	




