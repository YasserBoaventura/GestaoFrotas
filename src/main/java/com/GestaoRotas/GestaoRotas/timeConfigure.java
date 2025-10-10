package com.GestaoRotas.GestaoRotas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//Classe que faz a serializacao e desecerializacao



@Configuration
public class timeConfigure {

	   @Bean
	    public ObjectMapper objectMapper() {
		   ObjectMapper mapper = new ObjectMapper();
	        mapper.registerModule(new JavaTimeModule());
	        return mapper;
	    }  
}  
