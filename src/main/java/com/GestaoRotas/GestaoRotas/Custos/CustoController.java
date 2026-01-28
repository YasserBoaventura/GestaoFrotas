package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.*;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;

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
	    @PostMapping
	    public ResponseEntity<CustoDTO> criar(@Valid @RequestBody CustoRequestDTO request) {
	        Custo custo = custoService.registrarCustoManual(request);
	        return ResponseEntity.ok(CustoDTO.fromEntity(custo));
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
	            System.out.println("=== RECEBENDO REQUISIÇÃO DE RELATÓRIO ===");
	            System.out.println("Filtro recebido no controller:");
	            System.out.println("  dataInicio: " + filtro.getDataInicio());
	            System.out.println("  dataFim: " + filtro.getDataFim());
	            System.out.println("  veiculoId: " + filtro.getVeiculoId());
	            System.out.println("  tipoCusto: " + filtro.getTipoCusto());
	            System.out.println("  statusCusto: " + filtro.getStatusCusto());
	            
	            RelatorioCustosDetalhadoDTO relatorio = custoService.gerarRelatorioDetalhado(filtro);
	            return ResponseEntity.ok(relatorio);
	            
	        } catch (Exception e) {
	            System.out.println("=== ERRO NO CONTROLLER ===");
	            System.out.println("Mensagem: " + e.getMessage());
	            System.out.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
	            e.printStackTrace(); // Isso vai mostrar o stack trace completo
	             
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
	 
	 @GetMapping("/findAll")  
	 public ResponseEntity<?> findAll(){
		 try {
	 return ResponseEntity.status(HttpStatus.ACCEPTED).body(custoService.listar());  
		 }catch(Exception e) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erro ao listar");
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
	        
	        List<Custo> custos = custoService.buscarCustosPorVeiculo(veiculoId, dataInicio, dataFim);
	        return ResponseEntity.ok(custos.stream()
	            .map(CustoDTO::fromEntity)
	            .collect(Collectors.toList()));
	    }
}
