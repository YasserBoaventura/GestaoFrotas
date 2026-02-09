package com.GestaoRotas.GestaoRotas.Custos;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.CustoDTO.CustoDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoListDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.CustoViagemDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.CustoDTO.VeiculoCustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoUpdateDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.StatusCusto;
import com.GestaoRotas.GestaoRotas.Model.TipoCusto;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import com.GestaoRotas.GestaoRotas.Service.ServiceAbastecimentos;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;
import com.GestaoRotas.GestaoRotas.config.BusinessException;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import java.time.*;
import java.time.format.TextStyle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class custoService  implements CustoServiceImpl {
 
	  
    private final CustoRepository custoRepository;
    private final RepositoryVeiculo veiculoRepository;
    private final RepositoryAbastecimentos abastecimentoRepository;
    private final RepositoryManutencao repositoryManutencao;
    private final RepositoryViagem repositoryViagem; 
  /** 
     * Registro manual de qualquer custo
     */
    public Custo registrarCustoManual(CustoRequestDTO request) {
        // Buscar veículo (obrigatório)
        Veiculo veiculo = veiculoRepository.findById(request.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        
        var custo = new Custo();   
        
        custo.setData(request.getData() != null ? request.getData() : LocalDate.now());
        custo.setDescricao(request.getDescricao());
        custo.setValor(request.getValor());
        custo.setTipo(request.getTipo());
        custo.setStatus(request.getStatus() != null ? request.getStatus() : StatusCusto.PAGO);
        custo.setVeiculo(veiculo);
        
        // Buscar viagem apenas se o ID não for nulo
        if (request.getViagemId() != null) {
            Viagem viagem = repositoryViagem.findById(request.getViagemId())
                .orElse(null); // Retorna null se não encontrar
            custo.setViagem(viagem);
        }
        
        // Buscar manutenção apenas se o ID não for nulo
        if (request.getManutencaoId() != null) {
            Manutencao manutencao = repositoryManutencao.findById(request.getManutencaoId())
                .orElse(null);
            custo.setManutencao(manutencao);
        }
        // Buscar abastecimento apenas se o ID não for nulo
        if (request.getAbastecimentoId() != null) {
            abastecimentos abastecimento = abastecimentoRepository.findById(request.getAbastecimentoId())
                .orElse(null);
            custo.setAbastecimento(abastecimento);
        }
        
        custo.setObservacoes(request.getObservacoes()); 
        custo.setNumeroDocumento("CM-"  + "-" + UUID.randomUUID().toString().substring(0, 8));
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(veiculo.getId());
        
        return saved;
    }
    
    private void vincularComEntidade(CustoRequestDTO request, Custo custo) {
        if (request.getAbastecimentoId() != null) {
            abastecimentos abast = abastecimentoRepository.findById(request.getAbastecimentoId())
                .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado"));
            custo.setAbastecimento(abast);
        }
        
        if (request.getManutencaoId() != null) {
            Manutencao manut =  repositoryManutencao.findById(request.getManutencaoId())
                .orElseThrow(() -> new RuntimeException("Manutenção não encontrada"));
            custo.setManutencao(manut); 
        }
          
        if (request.getViagemId() != null) {
            Viagem viagem = repositoryViagem.findById(request.getViagemId())
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
            custo.setViagem(viagem);
        }
    }
    
    // ========== CRIAÇÃO AUTOMÁTICA ==========
    
    @Transactional
    public Custo criarCustoParaAbastecimento(abastecimentos abastecimento) {
        // Verificar se já existe custo para este abastecimento
        if (custoRepository.existsByAbastecimentoId(abastecimento.getId())) {
            throw new RuntimeException("Custo já existe para este abastecimento");
        }  
         Custo custo = new Custo();
        custo.setData(abastecimento.getDataAbastecimento());
        custo.setDescricao("Abastecimento - " + abastecimento.getTipoCombustivel());
        custo.setValor(abastecimento.getValorTotal());
        custo.setTipo(TipoCusto.COMBUSTIVEL); 
        custo.setStatus(StatusCusto.PAGO);
        custo.setVeiculo(abastecimento.getVeiculo()); 
        custo.setViagem(abastecimento.getViagem()); 
        custo.setAbastecimento(abastecimento);
        custo.setNumeroDocumento("ABS-" + abastecimento.getId());
          
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(abastecimento.getVeiculo().getId());
        
        return saved; 
    }  
    
    public Custo actualizarCustoParaAbastecimento(abastecimentos abastecimento) {
        // verificar se o abastecimento existe
    	abastecimentos abastecimentoExistente = abastecimentoRepository.findById(abastecimento.getId()).orElseThrow(() -> new RuntimeException("abastecimento nao encontrado")); 
        Custo custo = custoRepository.findByAbastecimentoId(abastecimento.getId()).orElseThrow(() -> new RuntimeException("Custo nao encontrado")); 
        custo.setData(abastecimento.getDataAbastecimento());
        custo.setDescricao("Abastecimento - " + abastecimento.getTipoCombustivel());
        custo.setValor(abastecimento.getValorTotal());
        custo.setTipo(TipoCusto.COMBUSTIVEL); 
        custo.setStatus(StatusCusto.PAGO);
        custo.setVeiculo(abastecimento.getVeiculo()); 
        custo.setViagem(abastecimento.getViagem()); 
        custo.setAbastecimento(abastecimento);
        custo.setNumeroDocumento("ABS-" + abastecimento.getId());
          
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(abastecimento.getVeiculo().getId());
        
        return saved; 
    }  
    
    
    @Transactional
    public Custo criarCustoParaManutencao(Manutencao manutencao) {
        Custo custo = new Custo(); 
        custo.setData(manutencao.getDataManutencao()); 
        custo.setDescricao("Manutenção - " + manutencao.getTipoManutencao());
        custo.setValor(manutencao.getCusto()); 
        custo.setTipo(determinarTipoManutencao(manutencao.getTipoManutencao().toString()));
        custo.setStatus(StatusCusto.PAGO);
        custo.setVeiculo(manutencao.getVeiculo()); 
        custo.setManutencao(manutencao);
        custo.setNumeroDocumento("MAN-" + manutencao.getId());
        custo.setObservacoes(manutencao.getDescricao());
         
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(manutencao.getVeiculo().getId());
        
        return saved;
    }
      
    //esse metodo vai ser utilizar pra actualizar 
    @Transactional   
    public Custo actualizarCustoManutencao(Manutencao manutencao) {
    Manutencao manutencaoExistente = repositoryManutencao.findById(manutencao.getId()).orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));
    Custo custo = custoRepository.findByManutencaoId(manutencao.getId()).orElseThrow(() -> new RuntimeException("custo nao encontrado"));  
        custo.setData(manutencao.getDataManutencao()); 
        custo.setDescricao("Manutenção - " + manutencao.getTipoManutencao());
        custo.setValor(manutencao.getCusto()); 
        custo.setTipo(determinarTipoManutencao(manutencao.getTipoManutencao().toString()));
        custo.setStatus(StatusCusto.PAGO);  
        custo.setVeiculo(manutencao.getVeiculo()); 
        custo.setManutencao(manutencao);
        custo.setNumeroDocumento("MAN-" + manutencao.getId());
        custo.setObservacoes(manutencao.getDescricao());
          
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(manutencaoExistente.getId()); 
           
        return saved;
        
    }
       private TipoCusto determinarTipoManutencao(String tipoManutencao) {
        if (tipoManutencao.contains("PREVENTIVA") || tipoManutencao.contains("REVISÃO")) {
            return TipoCusto.MANUTENCAO_PREVENTIVA;
        }
        return TipoCusto.MANUTENCAO_CORRETIVA;  
    } 
 @Transactional  
public Custo criarCustoParaViagem(CustoViagemDTO custoViagemDTO) {
	try {
    Custo custo = new Custo();
    custo.setData(LocalDate.now());
    custo.setDescricao(custoViagemDTO.getDescricao());
    custo.setValor(custoViagemDTO.getValor());  
    custo.setTipo(custoViagemDTO.getTipo());    
    custo.setStatus(StatusCusto.PAGO); 
    custo.setObservacoes(custoViagemDTO.getObservacoes()); 
    //pegando o veiculo 
    Veiculo veiculo = veiculoRepository.findById(custoViagemDTO.getVeiculoId()).orElseThrow(() -> new RuntimeException("Veiculo nao encontrado")); 
    // pegando a viagem
    Viagem viagem = repositoryViagem.findById(custoViagemDTO.getViagemId()).orElseThrow(() -> new RuntimeException("Viagem nao encontrada")); 
    custo.setVeiculo(veiculo);  
    custo.setViagem(viagem);   
    custo.setNumeroDocumento("VIA-" + viagem.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
    Custo saved = custoRepository.save(custo);
    atualizarTotaisVeiculo(viagem.getVeiculo().getId());
     
    return saved; 
	}catch(Exception e) {
		System.err.println("erro ao criar custo pra: " +e.getCause().getMessage().toString());
    	return null;
    	}
  }
@Transactional 
public String actualizarCustoParaViagem(CustoViagemDTO custoViagemDTO, Long id) {
 	try {
 		Custo custoActualizado =  custoRepository.findById(id).orElseThrow(() -> new RuntimeException("Custo pra viagem nao encontrada")); 
 		 custoActualizado.setData(LocalDate.now());
 		 custoActualizado.setDescricao(custoViagemDTO.getDescricao());
 		 custoActualizado.setValor(custoViagemDTO.getValor());  
 		 custoActualizado.setTipo(custoViagemDTO.getTipo());    
 		 custoActualizado.setStatus(StatusCusto.PAGO); 
 		 custoActualizado.setObservacoes(custoViagemDTO.getObservacoes()); 
 	     custoActualizado.setDataActualizacao(LocalDateTime.now());
  	    //pegando o veiculo
 	    Veiculo veiculo = veiculoRepository.findById(custoViagemDTO.getVeiculoId()).orElseThrow(() -> new RuntimeException("Veiculo nao encontrado")); 
 	    // pegando a viagem
 	    Viagem viagem = repositoryViagem.findById(custoViagemDTO.getViagemId()).orElseThrow(() -> new RuntimeException("Viagem nao encontrada")); 
 	 custoActualizado.setVeiculo(veiculo);     
 	 custoActualizado.setViagem(viagem);   
 	 custoActualizado.setNumeroDocumento("VIA-" + viagem.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
 	    Custo saved = custoRepository.save(custoActualizado);
 	    atualizarTotaisVeiculo(viagem.getVeiculo().getId());
 	     return "custo pra viagem actualizado com sucesso"; 
 	}catch(Exception e) {
 	System.err.println("erro actualizar custo para viagem : "+ e.getCause().getMessage().toString()); 
 		 String err = "erro ao actualizar custo pra viagem "+e.getMessage().toString();
	 return  err;  }
 }  // ========== ATUALIZAÇÃO DE TOTAIS ==========
    
    @Transactional
    public void atualizarTotaisVeiculo(Long veiculoId) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        
        // Calcular totais    
        Map<String, Object> totais = custoRepository.calcularTotaisPorVeiculo(veiculoId);
        
        veiculo.setCustoTotal((Double) totais.get("total"));
        veiculo.setCustoCombustivel((Double) totais.get("combustivel"));
        veiculo.setCustoManutencao((Double) totais.get("manutencao"));
         
        if (veiculo.getKilometragemAtual() != null && veiculo.getKilometragemAtual() > 0) {
            veiculo.setCustoMedioPorKm((Double) totais.get("total") / veiculo.getKilometragemAtual());
        }         
        veiculo.setUltimaAtualizacaoCusto(LocalDateTime.now());
        veiculoRepository.save(veiculo);
    } 
      
    @Transactional
    public void atualizarTotaisTodosVeiculos() {
        List<Veiculo> veiculos = veiculoRepository.findAll();
        veiculos.forEach(v -> atualizarTotaisVeiculo(v.getId()));
    }
    
    // ========== CONSULTAS E RELATÓRIOS ==========
    
public DashboardCustosDTO getDashboardCustos() {
    DashboardCustosDTO dashboard = new DashboardCustosDTO();
    dashboard.setMensagem("Dashboard carregado com sucesso");
    
    LocalDate hoje = LocalDate.now();
    int ano = hoje.getYear();
    int mes = hoje.getMonthValue();
    
   System.out.println("Dashboard para {}-{}"+ ano + mes);
    
try {  

    Double totalAtual = custoRepository.calcularTotalPorPeriodo(ano, mes);
    Double totalAnterior = custoRepository.calcularTotalPorPeriodo(ano, mes - 1);
    
    dashboard.setTotalMesAtual(totalAtual != null ? totalAtual : 0.0);
    dashboard.setTotalMesAnterior(totalAnterior != null ? totalAnterior : 0.0);
     
    // Calcular variação só se tiver dados anteriores
    if (dashboard.getTotalMesAnterior() != null && 
        dashboard.getTotalMesAnterior() > 0 && 
        dashboard.getTotalMesAtual() != null) {
        
        Double variacao = ((dashboard.getTotalMesAtual() - dashboard.getTotalMesAnterior()) / 
                         dashboard.getTotalMesAnterior()) * 100;
        dashboard.setVariacaoPercentual(variacao);
    } else {
        dashboard.setVariacaoPercentual(0.0);
    } 
            
        //custo por tipo
        Map<String, Double> custosPorTipo = new HashMap<>();
        List<Object[]> tipoResultados = custoRepository.calcularTotalPorTipoAgrupado(ano, mes);
        
        if (tipoResultados != null && !tipoResultados.isEmpty()) {
            for (Object[] obj : tipoResultados) {
                if (obj != null && obj.length >= 2) {
                    String tipo = obj[0] != null ? obj[0].toString() : "OUTROS";
                    Double valor = obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0;
                    custosPorTipo.put(tipo, valor);
                }
            } 
        
        }
        dashboard.setCustosPorTipo(custosPorTipo);
        
        // veiculos
        List<VeiculoCustoDTO> veiculosMaisCaros = new ArrayList<>();
        List<VeiculoCustoDTO> resultados = custoRepository.findTop5VeiculosMaisCaros(ano, mes);
           
    if (resultados != null && !resultados.isEmpty()) {
   
        for (VeiculoCustoDTO obj : resultados) {
         	  
            if (obj != null ) { 
                String matricula = obj.getMatricula() != null ? obj.getMatricula().toString() : "N/A";
                String modelo =   obj.getModelo() != null ? obj.getModelo().toString() : "Desconhecido";
                Double total =    obj.getTotalCusto() != null ?  obj.getTotalCusto() : 0.0;
                
                veiculosMaisCaros.add(new VeiculoCustoDTO(matricula, modelo, total));
            }
        }
        } 
            dashboard.setVeiculosMaisCaros(veiculosMaisCaros);
            
            // 4. Últimos custos
            List<Custo> ultimosCustos = custoRepository.findTop10ByOrderByDataDesc();
            if (ultimosCustos != null && !ultimosCustos.isEmpty()) {
                dashboard.setUltimosCustos(ultimosCustos.stream()
                    .map(CustoDTO::fromEntity)
                    .collect(Collectors.toList()));
            } else {
                dashboard.setUltimosCustos(new ArrayList<>());
            }
             
        } catch (Exception e) {
        	System.err.println("Erro ao gerar dashboard: {}"+ e.getMessage());      
            dashboard.setMensagem("Erro ao carregar dashboard: " + e.getMessage());
        } 
        
        return dashboard;
    } 
public Double valorTotalCustos() {
	return custoRepository.valorTotalCustos(); 
}
public List<Custo> buscarCustosPorVeiculoPeriodo(Long veiculoId, LocalDate inicio, LocalDate fim) {
        if (inicio == null) inicio = LocalDate.now().minusMonths(1);
        if (fim == null) fim = LocalDate.now();
        
        return custoRepository.findByVeiculoIdAndDataBetweenOrderByDataDesc(
            veiculoId, inicio, fim);
    }
      
    public Map<String, Double> getCustoMensalUltimos12Meses() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();
           
        for(int i = 11; i >= 0; i--) {
            LocalDate data = hoje.minusMonths(i);
            String mesAno = data.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt")) + 
                          "/" + data.getYear();
                     
            Double total = custoRepository.calcularTotalPorPeriodo(
                data.getYear(), data.getMonthValue());
            
            resultado.put(mesAno, total != null ? total : 0.0);
        }
           	
        
        return resultado; 
    }
    
    // ========== MIGRAÇÃO DE DADOS EXISTENTES ==========
    
    @Transactional
public void migrarAbastecimentosExistentes() {
    List<abastecimentos> abastecimentos = abastecimentoRepository.findAll();
      
    int contador = 0;
    for (abastecimentos abast : abastecimentos) {
        try {
            // Verificar se já tem custo
            if (!custoRepository.existsByAbastecimentoId(abast.getId())) {
                criarCustoParaAbastecimento(abast);
                contador++;
            }
        } catch (Exception e) {
            System.err.println("Erro ao migrar abastecimento " + abast.getId() + ": " + e.getMessage());
        }  
    }
     
    System.out.println("Migração concluída: " + contador + " abastecimentos migrados");
}
    //verifica por periodo apenas
  public List<CustoDTO> buscarPorPeriodo(LocalDate inicio, LocalDate fim){
    	  return custoRepository.buscarPorPeriodoDTO(inicio, fim); 
     } 
      //findAll
    public List<CustoListDTO> listar() {
      return custoRepository.findAllAsDTO();  
    }  
     public RelatorioCustosDetalhadoDTO gerarRelatorioDetalhado(RelatorioFilterDTO filtro) {
        RelatorioCustosDetalhadoDTO relatorio = new RelatorioCustosDetalhadoDTO();
           
        //filtros do relatorio por localDate  
        relatorio.setPeriodoInicio(filtro.getDataInicio());
        relatorio.setPeriodoFim(filtro.getDataFim());
                
        // Totais     
        Double totalPeriodo = custoRepository.calcularTotalPorPeriodoCompleto(
            filtro.getDataInicio(), filtro.getDataFim());
           relatorio.setTotalPeriodo(totalPeriodo);
            System.out.println("totais fim em e inicio: "+ totalPeriodo); 
             
            //quantidade custos por periodo
            Integer quantidadeCusto = custoRepository.numeroTotalCustoPorPeriodo(filtro.getDataInicio(), filtro.getDataFim()); 
            relatorio.setQuantidadeCustos(quantidadeCusto); 
           System.out.println("quantidade custos: "+quantidadeCusto);
             
           
        // Por veículo 
        List<Object[]> porVeiculo = custoRepository.calcularTotalPorVeiculoPeriodo(
            filtro.getDataInicio(), filtro.getDataFim());
        
        Map<String, Double> mapaVeiculos = new HashMap<>();
        for (Object[] obj : porVeiculo) {
            mapaVeiculos.put((String) obj[0], (Double) obj[1]);
        }
        relatorio.setTotalPorVeiculo(mapaVeiculos);
        
        // Por tipo 
        relatorio.setTotalPorTipo(custoRepository.calcularTotalPorTipoPeriodo(
            filtro.getDataInicio(), filtro.getDataFim()));
        
        // Lista detalhada de custos
        List<Custo> custos = custoRepository.findByPeriodo(
            filtro.getDataInicio(), filtro.getDataFim(), filtro.getVeiculoId());
        
             // total  porcento por custo //por implementar
        Map<String, Double> totalPorcentoPorCusto = new HashMap<>(); 
        //-----
         
        LocalDate  inicio = filtro.getDataInicioTop5VeiculosMaisCarro();
        LocalDate  fim = filtro.getDataFimTop5VeiculosMaisCarro(); 
        List<VeiculoCustoDTO> top5VeiculosMaisCarros = custoRepository.findTop5VeiculosMaisCarosPorPeriodo(inicio,fim);
       relatorio.setTop5VeiculosMaisCaros(top5VeiculosMaisCarros);
        
        //top5 os custos  mais altos
        Pageable pageable = PageRequest.of(0, 5);
            List<CustoDetalhadoDTO> top5CustosMaisAltos = custoRepository.findTop5CustosMaisAltos(pageable);
        relatorio.setTop5CustosMaisAltos(top5CustosMaisAltos);
      
        relatorio.setCustosDetalhados(custos.stream()
            .map(CustoDTO::fromEntity)
            .collect(Collectors.toList()));
         
        return relatorio;
    }
     public Optional<Long> numeroCustos () {
	    	return custoRepository.countAll();
	    } 
	       
       public  Optional<Integer> numeroCustoPorStatus(StatusCusto status) {
    	   return custoRepository.countByStatus(status);  
       }  
       public Optional<Integer> numeroCustoPorTipo(TipoCusto tipo) {
    	   return custoRepository.countByTipo(tipo); 
       }
       
 // Método auxiliar para converter
    private List<?> converterParaListaTotalPorTipo(List<Object[]> dados) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Object[] obj : dados) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", obj[0]);
            item.put("total", obj[1]);
            resultado.add(item);
        }
        return resultado;
    }

    @Transactional
    public void migrarManutencoesExistentes() {
        List<Manutencao> manutencoes = repositoryManutencao.findAll();
         
        int contador = 0;
    for (Manutencao manut : manutencoes) {
        try {
            if (!custoRepository.existsByManutencaoId(manut.getId())) {
                criarCustoParaManutencao(manut);
                contador++;
            }
        } catch (Exception e) {
            System.err.println("Erro ao migrar manutenção " + manut.getId() + ": " + e.getMessage());
        }
    }  
        
        System.out.println("Migração concluída: " + contador + " manutenções migradas");
    }
    
    // ========== INTEGRAÇÃO COM SERVICES EXISTENTES ==========
      
    @Transactional
    public void processarNovoAbastecimento(abastecimentos abastecimento) {
        criarCustoParaAbastecimento(abastecimento);
        
        // Notificar se necessário
        if (abastecimento.getValorTotal() > 1000) {
            enviarAlertaCustoAlto(abastecimento);
        }
    }
    
    @Transactional
    public void processarNovaManutencao(Manutencao manutencao) {
        criarCustoParaManutencao(manutencao);
         
        atualizarTotaisVeiculo(manutencao.getVeiculo().getId());
    }
     
    @Transactional
    public void processarNovaViagem(Viagem viagem, CustoViagemDTO custoViagemDTO) {
        // Criar custos padrão para viagem 
        
        // Exemplo: criar custo para pedágios se houver
        if (viagem.getCustoPedagios() != null && viagem.getCustoPedagios() > 0) {
            criarCustoParaViagem(custoViagemDTO); 
        }
    } 
    
    // ========== ALERTAS E NOTIFICAÇÕES ==========
    
    public void enviarAlertaCustoAlto(abastecimentos abastecimento) {
           
        String mensagem = String.format(
            "ALERTA: Abastecimento de alto valor - Veículo: %s, Valor: R$ %.2f",
            abastecimento.getVeiculo().getMatricula(),
            abastecimento.getValorTotal()
        );
          
        // Log ou enviar email/notificação
        System.out.println(mensagem);
    }
    
    public List<Veiculo> getVeiculosComCustoAcimaDaMedia() {
        Double mediaGeral = custoRepository.calcularMediaCustoPorVeiculo();
        
        if (mediaGeral == null) return Collections.emptyList();
         
        return veiculoRepository.findAll().stream()
            .filter(v -> v.getCustoTotal() != null && v.getCustoTotal() > mediaGeral)
            .collect(Collectors.toList());
    } 
      
    // ========== MÉTODOS DE MANIPULAÇÃO ======= ===
     
    @Transactional 
    public String atualizarCusto(Long id, CustoUpdateDTO updateDTO) {
        Custo custo = custoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        if (updateDTO.getDescricao() != null) custo.setDescricao(updateDTO.getDescricao());
        if (updateDTO.getValor() != null) custo.setValor(updateDTO.getValor());
        if (updateDTO.getTipo() != null) custo.setTipo(updateDTO.getTipo());
        if (updateDTO.getStatus() != null) custo.setStatus(updateDTO.getStatus());
        if (updateDTO.getObservacoes() != null) custo.setObservacoes(updateDTO.getObservacoes());
        custo.setDataActualizacao(LocalDateTime.now()); 
        Custo updated = custoRepository.save(custo);
        
        // Recalcular totais do veículo
        if (custo.getVeiculo() != null) {
            atualizarTotaisVeiculo(custo.getVeiculo().getId());
        }
        
        return "custo actualizado com sucesso!";
    }
     
    public String excluirCusto(Long id) {
        Custo custo = custoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        Long veiculoId = custo.getVeiculo() != null ? custo.getVeiculo().getId() : null;
        
        custoRepository.delete(custo);
         
        // Recalcular totais se tinha veículo 
        if (veiculoId != null) {
            atualizarTotaisVeiculo(veiculoId);
        }
        return "custo excluido com sucesso"; 
    }
}
