package com.GestaoRotas.GestaoRotas.Controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.CancelarViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioDiarioDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioGeralDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMensalDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioTopMotoristasDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceViagem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/viagens")
@CrossOrigin("*")
@JsonIgnoreProperties(ignoreUnknown = true) 
@RequiredArgsConstructor
public class ControllerViagem {  

       
	private final ServiceViagem serviceViagem;
	private final RepositoryViagem repositoryViagem;
	
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/save")
	public ResponseEntity<String> criarViagem(@RequestBody ViagensDTO viagemDTO) {
        return ResponseEntity.ok(serviceViagem.salvar(viagemDTO));
	   } 
 @GetMapping("/findAll")
 @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
 public ResponseEntity<List<Viagem>> findAll() {
        return ResponseEntity.ok(serviceViagem.findAll());
 }

	@DeleteMapping("/delete/{id}")
	 @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        return  ResponseEntity.status(HttpStatus.OK).body(serviceViagem.delete(id));

	}

	@GetMapping("/findByIdMotorista/{id}")
	public ResponseEntity<List<Viagem>> findByIDMotorista(@PathVariable long id){
        return ResponseEntity.ok(serviceViagem.findByIdMotorista(id));
    }
	
	@GetMapping("/veiculoss/{id}")
	public ResponseEntity<List<Viagem>> findByVeiculoId( @PathVariable Long id){
			return ResponseEntity.ok(serviceViagem.findByVeiculoId(id));
	}  
 @PutMapping("/update/{id}")
 @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
public ResponseEntity<String> update(@RequestBody ViagensDTO viagemDTO, @PathVariable long id) {
     return ResponseEntity.ok(serviceViagem.update(viagemDTO, id));

}

	@PutMapping("/concluir/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
	public ResponseEntity<Map<String, String>> ConcluirViagem(
  @RequestBody ConcluirViagemRequest request, @PathVariable long id) {
	 return ResponseEntity.ok(serviceViagem.ConcluirViagem(request, id)); 
	} 
	   
@PutMapping("/cancelarViagem/{id}")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
public ResponseEntity<Map<String, String>> cancelarViagem(
        @RequestBody CancelarViagemRequest request, 
        @PathVariable long id) { 
return ResponseEntity.ok(serviceViagem.cancelarViagem(request, id));
  }  
@PutMapping("/inicializarViagem/{id}")  
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")   
public ResponseEntity<Map<String , String>> iniciarViagem(@PathVariable Long id){
  return ResponseEntity.ok(serviceViagem.iniciarViagem(id)); 
}
@GetMapping("/countByStatus/{status}")      
public ResponseEntity<Long> countByStatus(@PathVariable String status){
	return ResponseEntity.ok(serviceViagem.getContByStatus(status));
}     

@GetMapping("/motoristas") 
@PreAuthorize("hasAuthority('ADMIN')")   
public ResponseEntity<List<RelatorioMotoristaDTO>> relatorioPorMotorista() {
        return ResponseEntity.ok(serviceViagem.relatorioPorMotorista());   
    } 

    @GetMapping("/veiculos")  
     public ResponseEntity<List<RelatorioPorVeiculoDTO>> relatorioPorVeiculo() {
        return ResponseEntity.ok(serviceViagem.gerarRelatorioPorVeiculo());
    }   
    @GetMapping("/findById/{id}")  
    public ResponseEntity<Viagem> findById(@PathVariable long id){
	 return ResponseEntity.status(HttpStatus.OK).body(serviceViagem.findById(id));
}   
 @GetMapping("/relatorio-periodo-por-motorista")
 @PreAuthorize("hasAuthority('ADMIN')")     
public ResponseEntity<List<RelatorioMotoristaDTO>> relatorioPorPeriodoMotorista(
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
   return ResponseEntity.ok(serviceViagem.relatorioPorMotoristaPeriodo(inicio, fim)); 
}       
@GetMapping("/relatorio-periodo-por-veiculo")
@PreAuthorize("hasAuthority('ADMIN')")     
public ResponseEntity<List<RelatorioPorVeiculoDTO>> relatorioPorPeriodoVeiculo(
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
	  return ResponseEntity.ok(serviceViagem.relatorioPorVeiculoPeriodo(inicio, fim));
}
 
     @GetMapping("/geral")
    public ResponseEntity<RelatorioGeralDTO> relatorioGeral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
            
            RelatorioGeralDTO dados = repositoryViagem.relatorioGeralPorPeriodo(inicio, fim);
            return ResponseEntity.ok(dados);
        } catch (Exception e) {
              //mock no catch
            return ResponseEntity.ok(new RelatorioGeralDTO(115L, 8L, 5L, 10100.5, 982.3, 87.8));
        }
    }
    //mockes

    @GetMapping("/top-motoristas")
    public ResponseEntity<List<RelatorioTopMotoristasDTO>> topMotoristas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        try {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
            
            List<RelatorioTopMotoristasDTO> dados = repositoryViagem.findTopMotoristasPorPeriodo(inicio, fim);
            //moque pra mostrar something.
            if (dados.isEmpty()) {
                dados = Arrays.asList(
                    new RelatorioTopMotoristasDTO("Ana Oliveira", 20L, 1560.2),
                    new RelatorioTopMotoristasDTO("João Silva", 15L, 1250.5),
                    new RelatorioTopMotoristasDTO("Maria Santos", 12L, 980.3),
                    new RelatorioTopMotoristasDTO("Pedro Costa", 8L, 745.8),
                    new RelatorioTopMotoristasDTO("Carlos Mendes", 5L, 420.5)
                );
            }
            
            return ResponseEntity.ok(dados);
        } catch (Exception e) {
        	//mock no cacth 
            return ResponseEntity.ok(Arrays.asList(
                new RelatorioTopMotoristasDTO("Ana Oliveira", 20L, 1560.2),
                new RelatorioTopMotoristasDTO("João Silva", 15L, 1250.5),
                new RelatorioTopMotoristasDTO("Maria Santos", 12L, 980.3)
            ));
        }
    }

}
    

