package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.*;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoViagemDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;

import com.GestaoRotas.GestaoRotas.DTO.CustoUpdateDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("api/custo")
@CrossOrigin("*")  
@RequiredArgsConstructor 
public class CustoController { 
	  
   private final custoService custoService;
	      
	    // Registro manual
@PostMapping("/criarCusto")   
@PreAuthorize("hasAuthority('ADMIN')") 
public ResponseEntity<CustoDTO> criar(@RequestBody @Valid CustoRequestDTO request) {
    try { 
        Custo custo = custoService.registrarCustoManual(request);
        return ResponseEntity.ok(CustoDTO.fromEntity(custo));
    } catch (Exception e) { 
        return ResponseEntity.badRequest().build(); 
    }   
}         
 @PutMapping("/update/{id}")  
 @PreAuthorize("hasAuthority('ADMIN')")
 public ResponseEntity<String> atualizarCusto(@PathVariable Long id, @RequestBody @Valid CustoUpdateDTO updateDTO){
	 try {
		  return ResponseEntity.ok(custoService.atualizarCusto(id, updateDTO)); 
	 }catch(Exception e) {   
		 e.getCause().getMessage(); 
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  
	  } 
 }
 @DeleteMapping("/delete/{id}") 
 @PreAuthorize("hasAuthority('ADMIN')")     
  public ResponseEntity<String> delete(@PathVariable Long id){ 
    return ResponseEntity.ok(custoService.excluirCusto(id));

  }
   @PostMapping("/criarCustoViagem")
   @PreAuthorize("hasAuthority('ADMIN')")     
    public ResponseEntity<Custo> criarCustoParaViagem(@RequestBody @Valid CustoViagemDTO custoViagemDTO){
      return ResponseEntity.ok(custoService.criarCustoParaViagem(custoViagemDTO)); 

    }  
 @PutMapping("/actualizarCustoParaViagem/{id}")
 @PreAuthorize("hasAuthority('ADMIN')")     
   public ResponseEntity<String>actualizaCustoParaViagem(@RequestBody @Valid CustoViagemDTO custoViagemDTO,@PathVariable Long id){
    return ResponseEntity.ok(custoService.actualizarCustoParaViagem(custoViagemDTO, id));

   }
   //Dashboard    
@GetMapping("/dashboard")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<DashboardCustosDTO> getDashboard() {
        return ResponseEntity.ok(custoService.getDashboardCustos());
    }   
// listar por data inicio e fim apenas
@GetMapping("/relatorio-por-periodo")      
@PreAuthorize("hasAuthority('ADMIN')")     
public ResponseEntity<List<CustoDTO>> relatorioPorPeriodo( 
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
   return ResponseEntity.ok(custoService.buscarPorPeriodo(inicio, fim)); 
}    
 @PostMapping("/relatorio")                         
 @PreAuthorize("hasAuthority('ADMIN')")       
 public ResponseEntity<?> relatorio(@RequestBody @Valid RelatorioFilterDTO filtro) {
        return ResponseEntity.ok(custoService.gerarRelatorioDetalhado(filtro));
    }

    @GetMapping("/numeroCustos")
@PreAuthorize("hasAuthority('ADMIN')")      
 public ResponseEntity<Integer> numeroCustos(){
	 return ResponseEntity.ok(custoService.numeroCustos()); 
 }

 @GetMapping("/valorTotal") 
 @PreAuthorize("hasAuthority('ADMIN')")     
 public ResponseEntity<Double>valorTotalCustos(){
 	return ResponseEntity.ok(custoService.valorTotalCustos());
 }  
 
 @GetMapping("/numeroPorStatus/{status}")
 @PreAuthorize("hasAuthority('ADMIN')")     
 public ResponseEntity<Optional<Integer>> numeroCustoPorStatus(@PathVariable StatusCusto status) {
   return ResponseEntity.ok(custoService.numeroCustoPorStatus(status)); 
 }     
 @GetMapping("/numeroPorTipo/{tipo}") 
 @PreAuthorize("hasAuthority('ADMIN')")     
 public ResponseEntity<Optional<Integer>> numeroCustoPorTipo(@PathVariable TipoCusto tipo) {
   return ResponseEntity.ok(custoService.numeroCustoPorTipo(tipo)); 
 }       
 @GetMapping("/findAll")    
 @PreAuthorize("hasAuthority('ADMIN')")     
 public ResponseEntity<?> findAll(){ 
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(custoService.listar());

}
 @GetMapping("/veiculosCustosAcimaMedia")
 @PreAuthorize("hasAuthority('ADMIN')")     
  public ResponseEntity<List<Veiculo>> getVeiculosComCustoAcimaDaMedia(){
    return ResponseEntity.status(HttpStatus.OK).body(custoService.getVeiculosComCustoAcimaDaMedia());

  } 
 @GetMapping("/custoMesalUltimos12Meses")
 @PreAuthorize("hasAuthority('ADMIN')")     
 public ResponseEntity<Map<?, ?>> getCustoMensalUltimos12Meses(){
    return ResponseEntity.ok(custoService.getCustoMensalUltimos12Meses());

 }
 // relatorio por data inicio e fim e veiculo
@GetMapping("/veiculo/{veiculoId}")  
@PreAuthorize("hasAuthority('ADMIN')")      
public ResponseEntity<List<CustoDTO>> porVeiculo(
        @PathVariable Long veiculoId,
        @RequestParam(required = false) String inicio,
        @RequestParam(required = false) String fim) {
      
    LocalDate dataInicio = inicio != null ? LocalDate.parse(inicio) : null;
    LocalDate dataFim = fim != null ? LocalDate.parse(fim) : null;
    
    List<Custo> custos = custoService.buscarCustosPorVeiculoPeriodo(veiculoId, dataInicio, dataFim);
    return ResponseEntity.ok(custos.stream()
        .map(CustoDTO::fromEntity)
        .collect(Collectors.toList())); 
}   
}
