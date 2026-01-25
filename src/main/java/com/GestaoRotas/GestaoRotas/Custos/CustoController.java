package com.GestaoRotas.GestaoRotas.Custos;

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
	    public ResponseEntity<DashboardCustosDTO> dashboard() {
	        return ResponseEntity.ok(custoService.getDashboardCustos());
	    }
	    
	    // Relatório 
	    @PostMapping("/relatorio")
	    public ResponseEntity<RelatorioCustosDetalhadoDTO> relatorio(@RequestBody RelatorioFilterDTO filtro) {
	        return ResponseEntity.ok(custoService.gerarRelatorioDetalhado(filtro));
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
