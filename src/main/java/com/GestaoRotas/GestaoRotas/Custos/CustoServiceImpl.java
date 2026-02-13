package com.GestaoRotas.GestaoRotas.Custos;

import java.time.LocalDate;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.*;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoListDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoViagemDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoUpdateDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioManutencaoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;


@Primary
@Service
public  interface  CustoServiceImpl {  

	  
	Custo registrarCustoManual(CustoRequestDTO request);  
	
	 String atualizarCusto(Long id, CustoUpdateDTO updateDTO) ; 
	 
	   String excluirCusto(Long id); 
	
	 List<CustoListDTO> listar(); 
	List<CustoDTO> buscarPorPeriodo(LocalDate inicio, LocalDate fim);
  Custo criarCustoParaAbastecimento(abastecimentos abastecimento);
    Custo criarCustoParaManutencao(Manutencao manutencao); 
   Custo criarCustoParaViagem(CustoViagemDTO custoViagemDTO);;
 String actualizarCustoParaViagem(CustoViagemDTO custoViagemDTO, Long id);  
   void atualizarTotaisVeiculo(Long veiculoId); 
  
    DashboardCustosDTO getDashboardCustos(); 
   
  List<Custo>  buscarCustosPorVeiculoPeriodo(Long veiculoId, LocalDate inicio, LocalDate fim); 
  
   Map<String, Double> getCustoMensalUltimos12Meses(); 
   
   RelatorioCustosDetalhadoDTO gerarRelatorioDetalhado(RelatorioFilterDTO filtro); 
	
   void  migrarManutencoesExistentes();
    
   void  enviarAlertaCustoAlto(abastecimentos abastecimento);
      
  void  migrarAbastecimentosExistentes();
   
  void  processarNovaManutencao(Manutencao manutencao);
  
  void processarNovoAbastecimento(abastecimentos abastecimento); 
  
  void processarNovaViagem(Viagem viagem, CustoViagemDTO custoViagemDTO); 

  List<Veiculo> getVeiculosComCustoAcimaDaMedia(); 
}

