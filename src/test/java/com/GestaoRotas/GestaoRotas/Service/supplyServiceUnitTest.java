package com.GestaoRotas.GestaoRotas.Service;



import java.beans.Customizer;
import java.lang.foreign.Linker.Option;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import com.GestaoRotas.GestaoRotas.Custos.Custo;
import com.GestaoRotas.GestaoRotas.DTO.AbastecimentoDTO;
import com.GestaoRotas.GestaoRotas.DTO.RelatorioCombustivelDTO;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Entity.abastecimentos;
import com.GestaoRotas.GestaoRotas.Model.statusAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryAbastecimentos;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;

@ExtendWith(MockitoExtension.class)
public class supplyServiceUnitTest {


    @Mock
    private RepositoryAbastecimentos repositoryAbastecimentos;

    @Mock
    private RepositoryViagem repositorioViagem;

    @Mock
    private RepositoryVeiculo repositorioveiculos;

    @Mock
    private com.GestaoRotas.GestaoRotas.Custos.custoService custoService;

    @InjectMocks
    private ServiceAbastecimentos serviceAbastecimentos;

    private AbastecimentoDTO dto;
    private Veiculo veiculo;
    private Viagem viagem;
    private abastecimentos abastecimento;
    private Custo custo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setModelo("Fusion");
    veiculo.setMatricula("ABC-1234");

    viagem = new Viagem();
    viagem.setId(1L);
    viagem.setStatus("EM_ANDAMENTO");

    abastecimento = new abastecimentos();
    abastecimento.setId(1L);
    abastecimento.setVeiculo(veiculo);
    abastecimento.setViagem(viagem);
    abastecimento.setDataAbastecimento(LocalDate.now());
    abastecimento.setKilometragemVeiculo(15000.0);
    abastecimento.setQuantidadeLitros(50.0);
    abastecimento.setPrecoPorLitro(5.50);
    abastecimento.setTipoCombustivel("GASOLINA");
    abastecimento.setStatusAbastecimento(statusAbastecimentos.REALIZADA);

    custo = new Custo();
    custo.setId(1L);
    custo.setValor(275.0);

    dto = new AbastecimentoDTO();
    dto.setVeiculoId(1L);
    dto.setViagemId(1L);
    dto.setDataAbastecimento(LocalDate.now());
    dto.setKilometragemVeiculo(15000.0);
    dto.setQuantidadeLitros(50.0);
    dto.setPrecoPorLitro(5.50);
    dto.setTipoCombustivel("GASOLINA");
    dto.setStatusAbastecimento(statusAbastecimentos.REALIZADA);
}

@Test
void save_ComVeiculoEViagem_DeveSalvarComSucesso() {

    when(repositorioveiculos.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositorioViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryAbastecimentos.save(any(abastecimentos.class))).thenReturn(abastecimento);
    when(repositoryAbastecimentos.findByIdWithViagem(1L)).thenReturn(Optional.of(abastecimento));
    when(custoService.criarCustoParaAbastecimento(any(abastecimentos.class))).thenReturn(custo);


    Map<String, String> response = serviceAbastecimentos.save(dto);

    // Assert
    assertNotNull(response);
    assertEquals("Abastecimento salvo", response.get("sucesso"));
    assertEquals("1", response.get("abastecimentoId"));
    assertEquals("1", response.get("custoId"));
    
    verify(repositoryAbastecimentos, times(1)).save(any(abastecimentos.class));
    verify(custoService, times(1)).criarCustoParaAbastecimento(any(abastecimentos.class));
}

@Test
void update_ComDadosValidos_DeveAtualizarComSucesso() {
 
    when(repositoryAbastecimentos.findById(1L)).thenReturn(Optional.of(abastecimento));
    when(repositorioveiculos.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositorioViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryAbastecimentos.save(any(abastecimentos.class))).thenReturn(abastecimento);

    when(custoService.actualizarCustoParaAbastecimento(any(abastecimentos.class))).thenReturn(custo);
  
    String resultado = serviceAbastecimentos.update(dto, 1L);

  
    assertEquals("sucesso ao actualizar abastecimento", resultado);
    verify(repositoryAbastecimentos, times(1)).save(any(abastecimentos.class));
    verify(custoService, times(1)).actualizarCustoParaAbastecimento(any(abastecimentos.class));
}

@Test
void update_SemViagem_DeveAtualizarComViagemNull() {
    // Arrange
    dto.setViagemId(null);
    when(repositoryAbastecimentos.findById(1L)).thenReturn(Optional.of(abastecimento));
    when(repositorioveiculos.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositoryAbastecimentos.save(any(abastecimentos.class))).thenReturn(abastecimento);
    
    when(custoService.actualizarCustoParaAbastecimento(any(abastecimentos.class))).thenReturn(custo);

    // Act
    String resultado = serviceAbastecimentos.update(dto, 1L);

    // Assert
    assertEquals("sucesso ao actualizar abastecimento", resultado);
    verify(repositoryAbastecimentos, times(1)).save(any(abastecimentos.class));
}

@Test
void update_DeveAtualizarQuantidadeLitros() {
    // Arrange
    dto.setQuantidadeLitros(75.0);
    when(repositoryAbastecimentos.findById(1L)).thenReturn(Optional.of(abastecimento));
    when(repositorioveiculos.findById(1L)).thenReturn(Optional.of(veiculo));
    when(repositorioViagem.findById(1L)).thenReturn(Optional.of(viagem));
    when(repositoryAbastecimentos.save(any(abastecimentos.class))).thenReturn(abastecimento);
    
    
    when(custoService.actualizarCustoParaAbastecimento(any(abastecimentos.class))).thenReturn(custo);

    // Act
    serviceAbastecimentos.update(dto, 1L);

    // Assert
    verify(repositoryAbastecimentos, times(1)).save(argThat(abastecimentoSalvo -> 
        abastecimentoSalvo.getQuantidadeLitros().equals(75.0)
    ));
}

@Test
void update_ComAbastecimentoNaoEncontrado_DeveLancarExcecao() {

    when(repositoryAbastecimentos.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceAbastecimentos.update(dto, 999L);
    });
    
    assertTrue(exception.getMessage().contains("Abastecimento não encontrado"));
    verify(custoService, never()).actualizarCustoParaAbastecimento(any());
}

@Test
void update_ComVeiculoNaoEncontrado_DeveLancarExcecao() {

    when(repositoryAbastecimentos.findById(1L)).thenReturn(Optional.of(abastecimento));
    when(repositorioveiculos.findById(1L)).thenReturn(Optional.empty());


    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        serviceAbastecimentos.update(dto, 1L);
    });
    
    assertTrue(exception.getMessage().contains("Veiculo não encontrado"));
        verify(custoService, never()).actualizarCustoParaAbastecimento(any());
    }
}