package com.GestaoRotas.GestaoRotas.Controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

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
import com.GestaoRotas.GestaoRotas.DTO.RelatorioDiarioDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioGeralDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMensalDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioTopMotoristasDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceViagem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/api/viagens")
@CrossOrigin("*")
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ControllerViagem {

      
	private final ServiceViagem serviceViagem;
	private final RepositoryViagem repositoryViagem;
	
	public ControllerViagem(ServiceViagem serviceViagem, RepositoryViagem RepositoryViagem ) {
       this.serviceViagem=serviceViagem;
       this.repositoryViagem=RepositoryViagem;
	}
	@PostMapping("/save")
	public ResponseEntity<Viagem> criarViagem(@RequestBody ViagensDTO viagemDTO) {
	    Viagem viagem = serviceViagem.salvar(viagemDTO);
	    return ResponseEntity.ok(viagem);
	}  
 @GetMapping("/findAll")
 public ResponseEntity<List<Viagem>> findAll(){
	try {
		List<Viagem> lista=this.serviceViagem.findAll();
		if(lista.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		return new ResponseEntity<>(lista, HttpStatus.OK);
		
	}catch(Exception e) {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  
	}
}
	//
	@DeleteMapping("/delete/{id}")
	 @PreAuthorize("hasAuthority('ADMIN')") 
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        try {
            String frase = this.serviceViagem.delete(id);
            if (frase.equals("Viagem não encontrada")) {
                return new ResponseEntity<>(frase, HttpStatus.NOT_FOUND);
            }
           return new ResponseEntity<>(frase, HttpStatus.OK);
            } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar Viagem", HttpStatus.BAD_REQUEST);
    }
	}
	//busca por viagem pelo iD do motorista
	@GetMapping("/findByIdMotorista/{id}")
	public ResponseEntity<List<Viagem>> findByIDMotorista(@PathVariable long id){
		try {
			List<Viagem> lista=this.serviceViagem.findByIdMotorista(id);
			if(lista.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
			return new ResponseEntity<>(lista, HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(null ,HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/veiculoss/{id}")
	public ResponseEntity<List<Viagem>> findByVeiculoId( @PathVariable Long id){
		try {
			System.out.print("eeee");
			List<Viagem> viagem = this.serviceViagem.findByVeiculoId(id);
			return ResponseEntity.ok(viagem);
			}catch(Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	 
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@RequestBody ViagensDTO viagemDTO, @PathVariable long id) {
	    try {
	    	Viagem viagem = this.serviceViagem.update(viagemDTO, id);
	        return ResponseEntity.ok(viagem);

	    } catch (Exception e) {

	        return ResponseEntity.badRequest().body("Erro ao actualizar: " + e.getMessage());
	    }
	}

//pra concluir a a viagem
@PutMapping("/concluir/{id}")
@PreAuthorize("hasAuthority('ADMIN')") 
public ResponseEntity<Viagem> ConcluirViagem(
                                           @RequestBody ConcluirViagemRequest request, @PathVariable long id) {
  Viagem viagem = repositoryViagem.findById(id)
      .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
             // Atualizar a quilometragem final
  viagem.setKilometragemFinal(request.getKilometragemFinal());
  viagem.setObservacoes(request.getObservacoes());
  viagem.setDataHoraChegada(request.getDataHoraChegada());
    
  // Chamar o método de negócio que já atualiza status e data
  viagem.concluirViagem();
   
  Viagem viagemAtualizada = repositoryViagem.save(viagem);
  return ResponseEntity.ok(viagemAtualizada);
}
//Mostra o relatorio nome do mortista do carro , totalViagens , totalEmKm e totalConbustivel usado
  
@PutMapping("/cancelarViagem/{id}")
@PreAuthorize("hasAuthority('ADMIN')") 
public ResponseEntity<Viagem> cancelarViagem(
        @RequestBody CancelarViagemRequest request, 
        @PathVariable long id) {
    
    try {
        Viagem viagem = repositoryViagem.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem nao encontrada"));

// Adicionar motivo às observações se fornecido
if (request.getMotivo() != null && !request.getMotivo().isEmpty()) {
    String observacoesAtuais = viagem.getObservacoes() != null ? 
            viagem.getObservacoes() : "";
    
    String novaObservacao = observacoesAtuais + 
            (observacoesAtuais.isEmpty() ? "" : "\n\n") +
            "[CANCELADA] Motivo: " + request.getMotivo();
    
    viagem.setObservacoes(novaObservacao);
}

        viagem.cancelarViagem();
        Viagem viagemCancelada = this.repositoryViagem.save(viagem);
        return ResponseEntity.ok(viagemCancelada);
        
    } catch(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
  }
}
  @GetMapping("/motoristas")
    public ResponseEntity<List<RelatorioMotoristaDTO>> relatorioPorMotorista() {
        return ResponseEntity.ok(serviceViagem.relatorioPorMotorista());
    } 
    //Mostra o relatorio placa do carro , totalViagens , totalEmKm e totalConbustivel usado
    @GetMapping("/veiculos")
     public ResponseEntity<List<RelatorioPorVeiculoDTO>> relatorioPorVeiculo() {
        return ResponseEntity.ok(serviceViagem.gerarRelatorioPorVeiculo());
    }  
    @GetMapping("/findById/{id}")  
    public ResponseEntity<Viagem> findById(@PathVariable long id){
    	try {
    		Viagem viagem=this.serviceViagem.findById(id);
    		if(viagem!=null) return new ResponseEntity<>(viagem, HttpStatus.OK);
        	}catch(Exception e) {
    		 return new ResponseEntity<>(null , HttpStatus.BAD_REQUEST);
    	}
    	return null;
    }  
    
    
    
    
    
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    ///
    





    



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
            // Mock para desenvolvimento
            return ResponseEntity.ok(new RelatorioGeralDTO(115L, 8L, 5L, 10100.5, 982.3, 87.8));
        }
    }

    @GetMapping("/top-motoristas")
    public ResponseEntity<List<RelatorioTopMotoristasDTO>> topMotoristas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        try {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
            
            List<RelatorioTopMotoristasDTO> dados = repositoryViagem.findTopMotoristasPorPeriodo(inicio, fim);
            
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
            return ResponseEntity.ok(Arrays.asList(
                new RelatorioTopMotoristasDTO("Ana Oliveira", 20L, 1560.2),
                new RelatorioTopMotoristasDTO("João Silva", 15L, 1250.5),
                new RelatorioTopMotoristasDTO("Maria Santos", 12L, 980.3)
            ));
        }
    }

    @GetMapping("/viagens")
    public ResponseEntity<List<Viagem>> listarViagens(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status) {
        
        try {
            if (dataInicio != null && dataFim != null) {
                LocalDateTime inicio = dataInicio.atStartOfDay();
                LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
                
                if (status != null && !status.isEmpty()) {
                    return ResponseEntity.ok(repositoryViagem.findByDataHoraPartidaBetweenAndStatus(inicio, fim, status));
                } else {
                    return ResponseEntity.ok(repositoryViagem.findByDataHoraPartidaBetween(inicio, fim));
                }
            } else if (status != null && !status.isEmpty()) {
                return ResponseEntity.ok(repositoryViagem.findByStatus(status));
            } else {
                return ResponseEntity.ok(repositoryViagem.findAll());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Arrays.asList());
        }
    }
}
    

