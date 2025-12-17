package com.GestaoRotas.GestaoRotas.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

		    lista.add(veiculo);
		    
		    when(repositoryVeiculo.findAll()).thenReturn(lista);
		    
		    //para teste save
		    when(repositoryVeiculo.save(veiculo)).thenReturn(veiculo);
		    
		   
		
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
		    assertEquals(HttpStatus.OK, response.getStatusCode());
	       assertNotNull(response);
	   }
}
