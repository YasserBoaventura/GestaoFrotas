package com.GestaoRotas.GestaoRotas.Service;


import com.GestaoRotas.GestaoRotas.Entity.Manutencao;
import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Entity.Viagem;
import com.GestaoRotas.GestaoRotas.Model.statusManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryManutencao;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryViagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class veicleServiceTest {
	 
    @Mock 
    private RepositoryVeiculo repositoryVeiculo;

    @Mock
    private RepositoryManutencao repositoryManutencao;

    @Mock
    private RepositoryViagem repositoryViagem;

    @InjectMocks
    private ServiceVeiculo serviceVeiculo;

    private Veiculo veiculo;
    private Marca marca;
    private Manutencao manutencao;
    private Viagem viagem;

    @BeforeEach
    void setUp() {
        marca = new Marca();
        marca.setId(1L);
        marca.setNome("Toyota");

        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setModelo("Corolla");
        veiculo.setMatricula("AB-12-34");
        veiculo.setAnoFabricacao(2020);
        veiculo.setCapacidadeTanque(50.0);
        veiculo.setKilometragemAtual(15000.0);
        veiculo.setStatus("DISPONIVEL");
        veiculo.setMarca(marca);
        veiculo.setEmailResponsavel("teste@teste.com");

        manutencao = new Manutencao();
        manutencao.setId(1L);
        manutencao.setVeiculo(veiculo);
        manutencao.setStatus(statusManutencao.AGENDADA);
        manutencao.setDataManutencao(LocalDate.now().plusDays(5));
        manutencao.setProximaManutencaoKm(20000.0);

        viagem = new Viagem();
        viagem.setId(1L);
        viagem.setVeiculo(veiculo);
        viagem.setStatus("CONCLUIDA");
    }

    @Test
    void salvar_DeveSalvarVeiculoComSucesso() {
        // Arrange
        when(repositoryVeiculo.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        String resultado = serviceVeiculo.salvar(veiculo);

       
        assertEquals("Veiculo Salvo com sucesso", resultado);
        verify(repositoryVeiculo, times(1)).save(veiculo);
    }

    @Test
    void update_DeveAtualizarVeiculoComSucesso() {
        // Arrange
        when(repositoryVeiculo.save(any(Veiculo.class))).thenReturn(veiculo);

        // Act
        String resultado = serviceVeiculo.update(veiculo, 1L);

        // Assert
        assertEquals("veiculo actualizado com sucesso", resultado);
        verify(repositoryVeiculo, times(1)).save(veiculo);
        assertEquals(1L, veiculo.getId());
    }

    @Test
    void deletar_DeveDeletarVeiculoComSucesso() {
        // Arrange
        doNothing().when(repositoryVeiculo).deleteById(1L);

        // Act
        String resultado = serviceVeiculo.deletar(1L);

        // Assert
        assertEquals("Veiculo deletado com sucess", resultado);
        verify(repositoryVeiculo, times(1)).deleteById(1L);
    }

    @Test
    void findAll_DeveRetornarListaDeVeiculos() {
        // Arrange
        List<Veiculo> veiculos = Arrays.asList(veiculo, new Veiculo());
        when(repositoryVeiculo.findAll()).thenReturn(veiculos);

        // Act
        List<Veiculo> resultado = serviceVeiculo.findAll();

        // Assert
        assertEquals(2, resultado.size());
        verify(repositoryVeiculo, times(1)).findAll();
    }

    @Test
    void atualizarStatusVeiculo_QuandoVeiculoEmViagem_DeveAtualizarParaEM_VIAGEM() {
        // Arrange
        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findByVeiculoIdAndStatus(1L, "EM_ANDAMENTO"))
            .thenReturn(Arrays.asList(viagem));

        // Act
        serviceVeiculo.atualizarStatusVeiculo(1L);

        // Assert
        assertEquals("EM_VIAGEM", veiculo.getStatus());
        assertNotNull(veiculo.getDataAtualizacaoStatus());
        verify(repositoryVeiculo, times(1)).save(veiculo);
    }

    @Test
    void atualizarStatusVeiculo_QuandoVeiculoEmManutencao_DeveAtualizarParaEM_MANUTENCAO() {
        manutencao.setStatus(statusManutencao.EM_ANDAMENTO);
        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findByVeiculoIdAndStatus(1L, "EM_ANDAMENTO"))
            .thenReturn(Collections.emptyList());
        when(repositoryManutencao.findByVeiculoId(1L))
            .thenReturn(Arrays.asList(manutencao));

        // Act 
	        serviceVeiculo.atualizarStatusVeiculo(1L);
 
	        // Assert
        assertEquals("EM_MANUTENCAO", veiculo.getStatus());
  
    verify(repositoryVeiculo, times(1)).save(veiculo);
}

    @Test
    void atualizarStatusVeiculo_QuandoTemManutencaoVencida_DeveAtualizarParaMANUTENCAO_VENCIDA() {
        // Arrange
        manutencao.setDataManutencao(LocalDate.now().minusDays(5));
        manutencao.setDataConclusao(null);
        
        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findByVeiculoIdAndStatus(1L, "EM_ANDAMENTO"))
            .thenReturn(Collections.emptyList());
        when(repositoryManutencao.findByVeiculoId(1L))
            .thenReturn(Arrays.asList(manutencao));

        // Act
        serviceVeiculo.atualizarStatusVeiculo(1L);

        // Assert
        assertEquals("MANUTENCAO_VENCIDA", veiculo.getStatus());
        verify(repositoryVeiculo, times(1)).save(veiculo);
    }

	    @Test
	    void atualizarStatusVeiculo_QuandoVeiculoDisponivel_DeveManterStatusDISPONIVEL() {
        // Arrange
        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
        when(repositoryViagem.findByVeiculoIdAndStatus(1L, "EM_ANDAMENTO"))
            .thenReturn(Collections.emptyList());
        when(repositoryManutencao.findByVeiculoId(1L))
            .thenReturn(Collections.emptyList());

        // Act
        serviceVeiculo.atualizarStatusVeiculo(1L);

        // Assert
        assertEquals("DISPONIVEL", veiculo.getStatus());

        verify(repositoryVeiculo, never()).save(any(Veiculo.class));
    }

	    @Test
	    void atualizarKilometragem_DeveAtualizarKmEStatus() {
	        // Arrange
	        when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo));
	        when(repositoryViagem.findByVeiculoIdAndStatus(1L, "EM_ANDAMENTO"))
	            .thenReturn(Collections.emptyList());
	        when(repositoryManutencao.findByVeiculoId(1L))
	            .thenReturn(Collections.emptyList());

	        // Act
	        serviceVeiculo.atualizarKilometragem(1L, 20000.0);

	        // Assert
	        assertEquals(20000.0, veiculo.getKilometragemAtual());
	        assertEquals("DISPONIVEL", veiculo.getStatus());
	        verify(repositoryVeiculo, times(1)).save(veiculo);
	    }

	    // Métodos auxiliares para testar métodos privados via reflexão
	    private boolean invokePrivateEstaEmViagem(Long veiculoId) {
	        try {
	            java.lang.reflect.Method method = ServiceVeiculo.class.getDeclaredMethod("estaEmViagem", Long.class);
	            method.setAccessible(true);
	            return (boolean) method.invoke(serviceVeiculo, veiculoId);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private boolean invokePrivateEstaEmManutencaoAtiva(Long veiculoId) {
	        try {
	            java.lang.reflect.Method method = ServiceVeiculo.class.getDeclaredMethod("estaEmManutencaoAtiva", Long.class);
	            method.setAccessible(true);
	            return (boolean) method.invoke(serviceVeiculo, veiculoId);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private boolean invokePrivateTemManutencaoVencida(Long veiculoId) {
        try {
            java.lang.reflect.Method method = ServiceVeiculo.class.getDeclaredMethod("temManutencaoVencida", Long.class);
            method.setAccessible(true);
            return (boolean) method.invoke(serviceVeiculo, veiculoId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean invokePrivateTemManutencaoProxima(Long veiculoId, int dias) {
        try {
            java.lang.reflect.Method method = ServiceVeiculo.class.getDeclaredMethod("temManutencaoProxima", Long.class, int.class);
            method.setAccessible(true);
            return (boolean) method.invoke(serviceVeiculo, veiculoId, dias);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}