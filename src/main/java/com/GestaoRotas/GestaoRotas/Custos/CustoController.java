package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/custo")
@CrossOrigin("*") 
@RequiredArgsConstructor
public class CustoController {
	 
	    private final custoService custoService;
	     
	    // Registro manual 
@PostMapping("/criarCusto")
    public ResponseEntity<CustoDTO> criar(@RequestBody @Valid CustoRequestDTO request) {
       var custo = new Custo();  
       BeanUtils.copyProperties(request, custo); 
      custo = custoService.registrarCustoManual(request);
        return ResponseEntity.ok(CustoDTO.fromEntity(custo));
    }    
   @PutMapping("/update/{id}")       
 public ResponseEntity<Custo> atualizarCusto(@PathVariable Long id, @RequestBody CustoUpdateDTO updateDTO){
	 try {
		  return ResponseEntity.ok(custoService.atualizarCusto(id, updateDTO)); 
	 }catch(Exception e) {
		 e.getCause().getMessage(); 
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  
	 }
 }
   @DeleteMapping("/delete/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id){
	  try {
		  return ResponseEntity.ok(custoService.excluirCusto(id)); 
	  }catch(Exception e) {
		  e.getCause().getMessage(); 
		return ResponseEntity.badRequest().body("erro ao excluir custo"); 	  
	  }
  }  
  // Dashboard 
@GetMapping("/dashboard")
    public ResponseEntity<DashboardCustosDTO> getDashboard() {
        DashboardCustosDTO dashboard = custoService.getDashboardCustos();
        return ResponseEntity.ok(dashboard);
    }  
     
 @PostMapping("/relatorio")         
public ResponseEntity<?> relatorio(@RequestBody RelatorioFilterDTO filtro) {
    try {
       RelatorioCustosDetalhadoDTO relatorio = custoService.gerarRelatorioDetalhado(filtro);
        return ResponseEntity.ok(relatorio);
       } catch (Exception e) {
       Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("erro", e.getMessage());
 errorResponse.put("causa", e.getCause() != null ? e.getCause().getMessage() : null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
 @GetMapping("/numeroCustos")
 public ResponseEntity<Long> numeroCustos(){
	 return ResponseEntity.ok(custoService.numeroCustos()); 
 }
 @GetMapping("/numeroPorStatus/{status}")
 public ResponseEntity<Integer> numeroCustoPorStatus(@PathVariable StatusCusto status) {
   return ResponseEntity.ok(custoService.numeroCustoPorStatus(status)); 
 }    
 @GetMapping("/numeroPorTipo/{tipo}")
 public ResponseEntity<Integer> numeroCustoPorTipo(@PathVariable TipoCusto tipo) {
   return ResponseEntity.ok(custoService.numeroCustoPorTipo(tipo)); 
 }    
 @GetMapping("/findAll")  
 public ResponseEntity<?> findAll(){
	 try { 
 return ResponseEntity.status(HttpStatus.ACCEPTED).body(custoService.listar());  
	 }catch(Exception e) {
		 e.getCause().getMessage(); 
		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erro ao listar");
 }
	 }
 @GetMapping("/veiculosCustosAcimaMedia")
  public ResponseEntity<List<Veiculo>> getVeiculosComCustoAcimaDaMedia(){
	  try {
		  return ResponseEntity.status(HttpStatus.OK).body(custoService.getVeiculosComCustoAcimaDaMedia()); 
	  }catch(Exception e) {
		  e.getCause().getMessage(); 
		  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); 
	  }   
  }
 
 @GetMapping("/custoMesalUltimos12Meses")
 public ResponseEntity<Map<?, ?>> getCustoMensalUltimos12Meses(){
	 try {
		 return ResponseEntity.ok(custoService.getCustoMensalUltimos12Meses());
		 }catch(Exception e) {
			 Map<String , String> erro = new HashMap<>();
			 erro.put("erro", e.getCause().getMessage()); 
		 return ResponseEntity.badRequest().body(erro); 
	 }
 }
 // Por veículo
@GetMapping("/veiculo/{veiculoId}")
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
