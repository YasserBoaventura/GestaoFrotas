 package com.GestaoRotas.GestaoRotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class GestaoRotasApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(GestaoRotasApplication.class, args);
	}

}
