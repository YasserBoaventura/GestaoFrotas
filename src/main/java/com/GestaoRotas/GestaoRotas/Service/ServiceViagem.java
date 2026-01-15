package com.GestaoRotas.GestaoRotas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;

import com.GestaoRotas.GestaoRotas.DTO.CancelarViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.ConcluirViagemRequest;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioMotoristaDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioPorVeiculoDTO;
import com.GestaoRotas.GestaoRotas.DTO.ViagensDTO;
import com.GestaoRotas.GestaoRotas.Entity.Motorista;
import com.GestaoRotas.GestaoRotas.Entity.Rotas;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Model.statusMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryMotorista;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryRotas;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor 
public class ServiceViagem {

   
    private final RepositoryViagem repositoryViagem;
    private final RepositoryMotorista motoristaRepository;
    private final RepositoryVeiculo veiculoRepository;
    private final RepositoryRotas rotaRepository;
    
    
public String update(ViagensDTO viagemDTO, long id) {

    Viagem viagem =  repositoryViagem.findById(id)
            .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));

    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
 
    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
            .orElseThrow(() ->  new RuntimeException("Rota não encontrada"));
    if (!validarMotorista(motorista)) {
    	
        new RuntimeException("Motorista não está disponível para viagem (Status: " + motorista.getStatus() + ")");
        return "Motorista não está disponível para viagem (Status: \"" + motorista.getStatus() + ")";
    }  
    if(validarVeiculo(veiculo)) { 
    	new RuntimeException("Veiculo nao disponivel");
    	return "veiculo nao disponivel para a viagem Status: \""+ veiculo.getStatus();
    }
   
    viagem.setMotorista(motorista);
    viagem.setVeiculo(veiculo);   
    viagem.setRota(rota);
    viagem.setDataHoraPartida(viagemDTO.getDataHoraPartida());
    viagem.setDataHoraChegada(viagemDTO.getDataHoraChegada());
    viagem.setStatus(viagemDTO.getStatus());
    viagem.setKilometragemInicial(viagemDTO.getKilometragemInicial());
    viagem.setKilometragemFinal(viagemDTO.getKilometragemFinal());
    viagem.setObservacoes(viagemDTO.getObservacoes());


    repositoryViagem.save(viagem);
    return  "viagem atualizada com sucesso!";
}
//inicializar viagem
	public Map<String ,String> iniciarViagem(Long id){
		Map<String,String> response = new HashMap<>();
		try {
		Viagem viagem = this.repositoryViagem.findById(id).orElseThrow(()-> new RuntimeException("viagem nao existente"));
	   viagem.iniciarViagem(); 
	   //para a atualizacao  do motorista 
	     Motorista  motorista = viagem.getMotorista();
	   
	     motorista.setStatus(statusMotorista.EM_VIAGEM);
	     motoristaRepository.save(motorista);
	    //Actualiza o veiculo mara em Viagem
	     repositoryViagem.save(viagem);
	   //para a actualizacao do veiculo 
	   Veiculo veiculo = viagem.getVeiculo();
	   if(veiculo!= null) {
	   veiculo.setStatus("EM_VIAGEM");
	   veiculo.setDataAtualizacaoStatus(LocalDateTime.now());
	   veiculoRepository.save(veiculo);
	     
	    //atualizacao do 
	  }  
	response.put("message", "viagem inicializada com sucesso");
	   return response; 
	 }catch(Exception e) {
		   response.put("error", e.getMessage()); 
	return response; 
	
	}
}
	
     public Map<String, String> ConcluirViagem(
		           ConcluirViagemRequest request, long id) {
				try { 
				Map<String, String> sucess = new HashMap<>();
		Viagem viagem = repositoryViagem.findById(id)
		.orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
		// Atualizar a quilometragem final
		viagem.setKilometragemFinal(request.getKilometragemFinal());
		viagem.setObservacoes(request.getObservacoes()); 
		viagem.setDataHoraChegada(request.getDataHoraChegada());
		
		// Chamar o método de negócio que já atualiza status e data
		viagem.concluirViagem();
		
		Viagem viagemAtualizada = repositoryViagem.save(viagem);
		//atualizacao do motorista
		
		Motorista motorista = viagem.getMotorista();
		if(motorista!=null) {
		motorista.setStatus(statusMotorista.DISPONIVEL);
		motoristaRepository.save(motorista);
		}
		//  atualize o estado do veiculo quando a viage estiver concluida
		Veiculo veiculo = viagem.getVeiculo();
		if(veiculo != null) {
		veiculo.setStatus("DISPONIVEL");
		veiculoRepository.save(veiculo);
		}
		sucess.put("sucesso", "viagem concluirda com sucesso"); 
		return sucess;
	}catch(Exception e) {
					Map<String ,String> erro = new HashMap<>();
					erro.put("erro","erro ao tentar concluir viagem");
					return erro;
				}
		
		
		}
	
	//cancelar viagem
     @Transactional
	public  Map<String, String>  cancelarViagem(
	         CancelarViagemRequest request, 
	       long id) {
		Map<String, String> sucess = new HashMap<>();
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
	        
	        //atualize o veiculo para disponivel se a  viagem for cancelada
	        Veiculo veiculo = viagem.getVeiculo();
	        if(veiculo!= null) {
	        veiculo.setStatus("DISPONIVEL");
	          }
	        sucess.put("sucesso", "canselada com sucesso");
	        veiculoRepository.save(veiculo); 
	        return sucess;
	       } catch(Exception e) { 
	   Map<String ,String> erro = new HashMap<>();
	   erro.put("erro", "erro ao cancelar viagem");
	   return erro;
	   
	  }
	} 
	@Transactional    
	public String salvar(ViagensDTO viagemDTO) {
	    Viagem viagem = new Viagem();
	    // Buscar motorista, veiculo e rota pelos IDs  
	    Motorista motorista = motoristaRepository.findById(viagemDTO.getMotoristaId())
	        .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
	    Veiculo veiculo = veiculoRepository.findById(viagemDTO.getVeiculoId())
	        .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
	    Rotas rota = rotaRepository.findById(viagemDTO.getRotaId())
	        .orElseThrow(() -> new RuntimeException("Rota não encontrada"));
	    //validar o motorista 
	    if (!validarMotorista(motorista)) {
	    	  new RuntimeException("Motorista não está disponível para viagem (Status: " + motorista.getStatus() + ")");
	          return "Motorista não está disponível para viagem (Status: \"" + motorista.getStatus() + ")";
        }
        if(validarVeiculo(veiculo)) {
        	new RuntimeException("Veiculo nao disponivel");
        	return "veiculo nao disponivel para a viagem Status: \""+ veiculo.getStatus();
        }  
	    viagem.setMotorista(motorista);
	    viagem.setVeiculo(veiculo);   
	    viagem.setRota(rota);  
	    viagem.setDataHoraPartida(viagemDTO.getDataHoraPartida());
	    viagem.setDataHoraChegada(viagemDTO.getDataHoraChegada());
	    viagem.setStatus(viagemDTO.getStatus());
	    viagem.setKilometragemInicial(viagemDTO.getKilometragemInicial());
	    viagem.setKilometragemFinal(viagemDTO.getKilometragemFinal());
	    viagem.setObservacoes(viagemDTO.getObservacoes());
	    viagem.setData(LocalDateTime.now());
		
	    repositoryViagem.save(viagem);
	    return "viagem salva com sucesso";
	}
	// campos para validar o estato do motorista antes de ser
	//Associando a uma viagem
	 private boolean validarMotorista(Motorista motorista) {
    // Verifica se o motorista está ativo e disponível
    if (motorista.getStatus() == null) {
        return false;  
    }
    
    String status = motorista.getStatus().toString();
    
    // Lista de status que impedem o motorista de fazer viagens
    List<String> statusBloqueados = Arrays.asList(
        "FERIAS", 
        "AFASTADO",
        "INATIVO",
        "BLOQUEADO"
    );
	        
	        // Verifica se o status do motorista está na lista de bloqueados
	        return !statusBloqueados.contains(status);
	    }
	    // Método de validação do veículo CORRIGIDO
 private boolean validarVeiculo(Veiculo veiculo) {
	    if (veiculo == null) {  
	        throw new RuntimeException("Veículo não pode ser nulo");
	    }
	    
	    if (veiculo.getStatus() == null) {
	        return true; // não disponível
	    }
	    
	    String status = veiculo.getStatus();
	    
	    // Lista de status que impedem o veículo de ser usado
	    List<String> statusIndisponiveis = Arrays.asList(
	        "EM_MANUTENCAO", 
	        "MANUTENCAO_VENCIDA",
	        "MANUTENCAO_PROXIMA",
	        "EM_VIAGEM",
	        "INATIVO",
	        "BLOQUEADO"
	    );
	    
	    return statusIndisponiveis.contains(status);
	}
public List<Viagem> findAll(){
    return this.repositoryViagem.findAll();
    }
//Counting by status
public Long getContByStatus(String status) { 
	return repositoryViagem.countByStatus(status);
}
	public String delete(long id) {
		this.repositoryViagem.deleteById(id);
		return "deletado com sucesso";
	} 
	public List<Viagem> findByIdMotorista(long id){
		return this.repositoryViagem.findByMotoristaId(id);
	} 
	//Mostra o motorista totalViagens , totalEmKm e totalConbustivel usado
public List<RelatorioMotoristaDTO> relatorioPorMotorista() {
        return repositoryViagem.relatorioPorMotorista();
    }
   //Mostra o plca do carro , totalViagens , totalEmKm e totalConbustivel usado
   public List<RelatorioPorVeiculoDTO> gerarRelatorioPorVeiculo() {
        return repositoryViagem.relatorioPorVeiculo();  
    } 
    public  List<Viagem>  findByVeiculoId(long id) {
     return this.repositoryViagem.findByVeiculoId(id);
        }
    //Busca pelo o id da viagem   
    public Viagem findById(long id) {
    	return this.repositoryViagem.findById(id).orElseThrow(()-> new RuntimeException("Viagem nao encontrada"));
    }	
}
