package com.GestaoRotas.GestaoRotas.Custos;

import org.springframework.stereotype.Service;

import com.GestaoRotas.GestaoRotas.DTO.CustoDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoRequestDTO;
import com.GestaoRotas.GestaoRotas.DTO.CustoUpdateDTO;
import com.GestaoRotas.GestaoRotas.DTO.DashboardCustosDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCustosDetalhadoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioFilterDTO;
import com.GestaoRotas.GestaoRotas.DTO.VeiculoCustoDTO;
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

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.time.*;
import java.time.format.TextStyle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class custoService {

	 
    private final CustoRepository custoRepository;
    private final RepositoryVeiculo veiculoRepository;
    private final RepositoryAbastecimentos abastecimentoRepository;
    private final RepositoryManutencao repositoryManutencao;
    private final RepositoryViagem repositoryViagem; 
  /**
     * Registro manual de qualquer custo
     */
    public Custo registrarCustoManual(CustoRequestDTO request) {
        Veiculo veiculo = veiculoRepository.findById(request.getVeiculoId())
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        
        Custo custo = new Custo();
        custo.setData(request.getData() != null ? request.getData() : LocalDate.now());
        custo.setDescricao(request.getDescricao());
        custo.setValor(request.getValor());
        custo.setTipo(request.getTipo());
        custo.setStatus(request.getStatus() != null ? request.getStatus() : StatusCusto.PAGO);
        custo.setVeiculo(veiculo);
        custo.setObservacoes(request.getObservacoes());
        custo.setNumeroDocumento(request.getNumeroDocumento());
         	
        // Vincular com entidades específicas
        vincularComEntidade(request, custo);
        
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
     
    private TipoCusto determinarTipoManutencao(String tipoManutencao) {
        if (tipoManutencao.contains("PREVENTIVA") || tipoManutencao.contains("REVISÃO")) {
            return TipoCusto.MANUTENCAO_PREVENTIVA;
        }
        return TipoCusto.MANUTENCAO_CORRETIVA;  
    }
    
    @Transactional
    public Custo criarCustoParaViagem(Viagem viagem, TipoCusto tipo, String descricao, Double valor) {
        Custo custo = new Custo();
        custo.setData(LocalDate.now());
        custo.setDescricao(descricao);
        custo.setValor(valor);
        custo.setTipo(tipo);
        custo.setStatus(StatusCusto.PAGO); 
        custo.setVeiculo(viagem.getVeiculo());
        custo.setViagem(viagem);   
        custo.setNumeroDocumento("VIA-" + viagem.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
        
        Custo saved = custoRepository.save(custo);
        atualizarTotaisVeiculo(viagem.getVeiculo().getId());
         
        return saved; 
    }
    
    // ========== ATUALIZAÇÃO DE TOTAIS ==========
    
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
        
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();
        
        System.out.println("DEBUG: Dashboard para " + ano + "-" + mes);
        System.out.println("DEBUG: Hoje: " + hoje);
        
        // 1. Totais do mês
        Double totalAtual = custoRepository.calcularTotalPorPeriodo(ano, mes);
        Double totalAnterior = custoRepository.calcularTotalPorPeriodo(ano, mes - 1);
        
        System.out.println("DEBUG: Total atual: " + totalAtual);
        System.out.println("DEBUG: Total anterior: " + totalAnterior);
        
        dashboard.setTotalMesAtual(totalAtual != null ? totalAtual : 0.0);
        dashboard.setTotalMesAnterior(totalAnterior != null ? totalAnterior : 0.0);
        
        // Calcular variação
        if (dashboard.getTotalMesAnterior() != null && dashboard.getTotalMesAnterior() > 0) {
            Double variacao = ((dashboard.getTotalMesAtual() - dashboard.getTotalMesAnterior()) / 
                             dashboard.getTotalMesAnterior()) * 100;
            dashboard.setVariacaoPercentual(variacao);
        }
        
        // 2. Custo por tipo
        List<Object[]> tipoResultados = custoRepository.calcularTotalPorTipoAgrupado(ano, mes);
        System.out.println("DEBUG: Resultados por tipo: " + (tipoResultados != null ? tipoResultados.size() : 0));
        
        if (tipoResultados != null) {
            for (int i = 0; i < tipoResultados.size(); i++) {
                Object[] obj = tipoResultados.get(i);
                System.out.println("DEBUG: Tipo " + i + ": " + Arrays.toString(obj));
            }
        }
        
        Map<String, Double> custosPorTipo = new HashMap<>();
        if (tipoResultados != null) {
            for (Object[] obj : tipoResultados) {
                if (obj != null && obj.length >= 2) {
                    String tipo = obj[0] != null ? obj[0].toString() : "OUTROS";
                    Double valor = 0.0;
                    if (obj[1] != null) {
                        if (obj[1] instanceof Number) {
                            valor = ((Number) obj[1]).doubleValue();
                        }
                    }
                    custosPorTipo.put(tipo, valor);
                }
            }
        }
        dashboard.setCustosPorTipo(custosPorTipo);
         
        // 3. Veículos mais caros
        List<Object[]> resultados = custoRepository.findTop5VeiculosMaisCaros(ano, mes);
        System.out.println("DEBUG: Veículos mais caros: " + (resultados != null ? resultados.size() : 0));
        
        List<VeiculoCustoDTO> veiculosMaisCaros = new ArrayList<>();
        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                System.out.println("DEBUG: Objeto veículo: " + Arrays.toString(obj));
                if (obj != null) {
                    String matricula = obj[0] != null ? obj[0].toString() : "N/A";
                    Double total = 0.0;
                    if (obj[1] != null) {
                        if (obj[1] instanceof Number) {
                            total = ((Number) obj[1]).doubleValue();
                        } else if (obj[1] instanceof String) {
                            try {
                                total = Double.parseDouble((String) obj[1]);
                            } catch (NumberFormatException e) {
                                total = 0.0;
                            }
                        }
                    }
                    veiculosMaisCaros.add(new VeiculoCustoDTO(matricula, "Modelo", total));
                }
            }
        } else {
            System.out.println("DEBUG: Nenhum resultado encontrado para veículos mais caros");
            // Adicionar dados de exemplo para teste
            veiculosMaisCaros.add(new VeiculoCustoDTO("ABC-123", "Modelo A", 5000.0));
            veiculosMaisCaros.add(new VeiculoCustoDTO("DEF-456", "Modelo B", 3000.0));
        }
        dashboard.setVeiculosMaisCaros(veiculosMaisCaros);
        
        // 4. Últimos custos
        List<Custo> ultimosCustos = custoRepository.findTop10ByOrderByDataDesc();
        System.out.println("DEBUG: Últimos custos: " + (ultimosCustos != null ? ultimosCustos.size() : 0));
        
        if (ultimosCustos != null && !ultimosCustos.isEmpty()) {
            dashboard.setUltimosCustos(ultimosCustos.stream()
                .map(CustoDTO::fromEntity)
                .collect(Collectors.toList()));
        } else {
            dashboard.setUltimosCustos(new ArrayList<>());
            System.out.println("DEBUG: Nenhum custo encontrado no banco");
        }
        
        return dashboard;
    } 
    
    public RelatorioCustosDetalhadoDTO gerarRelatorioDetalhado(RelatorioFilterDTO filtro) {
        RelatorioCustosDetalhadoDTO relatorio = new RelatorioCustosDetalhadoDTO();
        
        // Dados básicos
        relatorio.setPeriodoInicio(filtro.getDataInicio());
        relatorio.setPeriodoFim(filtro.getDataFim());
        
        // Totais
        Double totalPeriodo = custoRepository.calcularTotalPorPeriodoCompleto(
            filtro.getDataInicio(), filtro.getDataFim());
        relatorio.setTotalPeriodo(totalPeriodo);
         
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
        
        relatorio.setCustosDetalhados(custos.stream()
            .map(CustoDTO::fromEntity)
            .collect(Collectors.toList()));
         
        return relatorio;
    }
    
    public List<Custo> buscarCustosPorVeiculo(Long veiculoId, LocalDate inicio, LocalDate fim) {
        if (inicio == null) inicio = LocalDate.now().minusMonths(1);
        if (fim == null) fim = LocalDate.now();
        
        return custoRepository.findByVeiculoIdAndDataBetweenOrderByDataDesc(
            veiculoId, inicio, fim);
    }
    
    public Map<String, Double> getCustoMensalUltimos12Meses() {
        Map<String, Double> resultado = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();
        
        for (int i = 11; i >= 0; i--) {
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
        
        // Atualizar custo médio do veículo
        atualizarTotaisVeiculo(manutencao.getVeiculo().getId());
    }
    
    @Transactional
    public void processarNovaViagem(Viagem viagem) {
        // Criar custos padrão para viagem (pedágios, etc)
        // Isso pode ser expandido conforme necessário
        
        // Exemplo: criar custo para pedágios se houver
        if (viagem.getCustoPedagios() != null && viagem.getCustoPedagios() > 0) {
            criarCustoParaViagem(viagem, TipoCusto.PEDAGIO, 
                "Pedágios - Viagem " + viagem.getId(), viagem.getCustoPedagios());
        }
    } 
    
    // ========== ALERTAS E NOTIFICAÇÕES ==========
    
    private void enviarAlertaCustoAlto(abastecimentos abastecimento) {
        // Implementar lógica de alerta
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
    
    // ========== MÉTODOS DE MANIPULAÇÃO ==========
     
    @Transactional
    public Custo atualizarCusto(Long id, CustoUpdateDTO updateDTO) {
        Custo custo = custoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        if (updateDTO.getDescricao() != null) custo.setDescricao(updateDTO.getDescricao());
        if (updateDTO.getValor() != null) custo.setValor(updateDTO.getValor());
        if (updateDTO.getTipo() != null) custo.setTipo(updateDTO.getTipo());
        if (updateDTO.getStatus() != null) custo.setStatus(updateDTO.getStatus());
        if (updateDTO.getObservacoes() != null) custo.setObservacoes(updateDTO.getObservacoes());
         
        Custo updated = custoRepository.save(custo);
        
        // Recalcular totais do veículo
        if (custo.getVeiculo() != null) {
            atualizarTotaisVeiculo(custo.getVeiculo().getId());
        }
        
        return updated;
    }
    
    @Transactional
    public void excluirCusto(Long id) {
        Custo custo = custoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Custo não encontrado"));
        
        Long veiculoId = custo.getVeiculo() != null ? custo.getVeiculo().getId() : null;
        
        custoRepository.delete(custo);
        
        // Recalcular totais se tinha veículo
        if (veiculoId != null) {
            atualizarTotaisVeiculo(veiculoId);
        }
    }
}
