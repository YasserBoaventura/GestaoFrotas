package com.GestaoRotas.GestaoRotas.Controllers;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.*;
import com.GestaoRotas.GestaoRotas.Controller.ControllerVeiculo;
import com.GestaoRotas.GestaoRotas.Entity.Marca;
import com.GestaoRotas.GestaoRotas.Entity.Veiculo;
import com.GestaoRotas.GestaoRotas.Repository.RepositoryVeiculo;
import com.GestaoRotas.GestaoRotas.Service.ServiceVeiculo;
 @SpringBootTest
public class ControllerVeiculoTest {

	      @Autowired
	      ControllerVeiculo controllerVeiculo; 
	      @Mock
	      ServiceVeiculo serviceVeiculo;
	  
	      @MockitoBean
	      RepositoryVeiculo repositoryVeiculo;
	   
	   @BeforeEach
	    void setup() {  
		   List<Veiculo> lista = new ArrayList<>();
            Marca marca = new Marca(1L,"BMW");
            
            
		    Veiculo veiculo = new Veiculo();
		    veiculo.setMarca(marca);
		    veiculo.setModelo("Toyota Corolla");
		    veiculo.setMatricula("ABC-123-MP");
		    veiculo.setAnoFabricacao(2020);
		    veiculo.setCapacidadeTanque(50.0);
		    veiculo.setKilometragemAtual(120000.0);
		    
		    List<Veiculo> lista1= new  ArrayList<>();
		    Veiculo veiculo2 = new Veiculo();
		    veiculo.setId(1L);
		    veiculo.setMarca(marca);
		    veiculo.setModelo("Toyota Corolla");
		    veiculo.setMatricula("ABC-123-MP");
		    veiculo.setAnoFabricacao(2020);
		    veiculo.setCapacidadeTanque(50.0);
		    veiculo.setKilometragemAtual(120000.0);
        
		    lista1.add(veiculo2);
		    
		    when(repositoryVeiculo.findAll()).thenReturn(lista);
		    
		    //para teste save
		    when(repositoryVeiculo.save(veiculo)).thenReturn(veiculo);
		    
		   when(repositoryVeiculo.findById(1L)).thenReturn(Optional.of(veiculo2));
		  
		    long u =0;
		   doNothing().when(repositoryVeiculo).deleteById(1L);
		
	 }
	   @Test 
	  void TestSave() {
		   Veiculo veiculo = new Veiculo(
			        "Toyota Hilux",
			        "MPA-456-BC",
			        2019,
			        80.0,
			        95000.0
			    );
		  ResponseEntity<Map<String, String>> response = controllerVeiculo.salvar(veiculo);
		 Map<String, String> map= new HashMap<>() ;
		 map.put("message", "Veículo cadastrado com sucesso");
		   // assertEquals(map, response.getBody());
		    assertNotNull(response);
		    assertEquals(HttpStatus.CREATED, response.getStatusCode());
	  }
	   @Test
	   void TestFindAll() {
		    
		    ResponseEntity<List<Veiculo>> response = controllerVeiculo.findAll();
	   //no content porque no setup retorna uma lista nao adicionada no ArrayList
		   assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	       assertNotNull(response);
	   }
	   @Test
	   void TestFinfByID() {
		   ResponseEntity<Veiculo> response = controllerVeiculo.findById(1L);
		   assertEquals( response.getStatusCode(), HttpStatus.OK);
		   assertNotNull(response);
		   
	   }
	   @Test
	   void TestDelete() {
		   ResponseEntity<String> response = controllerVeiculo.delete(-0L);
		   assertEquals(HttpStatus.OK, response.getStatusCode());
		   
	   }
  void TestDelete_Exception() {
    // Arrange
    doThrow(new RuntimeException("Erro ao deletar veículo"))
        .when(serviceVeiculo).deletar(1L);
    
    // Act & Assert
    assertThrows(RuntimeException.class, () -> {
        controllerVeiculo.delete(-1L);
    });
}}