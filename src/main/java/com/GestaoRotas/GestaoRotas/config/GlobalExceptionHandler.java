package com.GestaoRotas.GestaoRotas.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	//TRATAMENTO DE ERROS DE VALIDATIONS
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handle01(MethodArgumentNotValidException ex) {
		Map<String, String> erros = new HashMap<>();
		for (FieldError fildError : ex.getBindingResult().getFieldErrors()) {
			erros.put(fildError.getField(), fildError.getDefaultMessage());
		}
		return new ResponseEntity<Map<String, String>>(erros, HttpStatus.BAD_REQUEST);
	}

	//TRATAMENTO DE ERROS DE VALIDATIONS
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handle02(ConstraintViolationException ex) {
		Map<String, String> erros = new HashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			erros.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return new ResponseEntity<Map<String, String>>(erros, HttpStatus.BAD_REQUEST);
	}

	//TRATAMENTO DOS DEMAIS ERROS DA APLICAÇÃO E DE REGRAS DE NEGÓCIO
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handle03(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	//Tratamentos do Erro throw new IlegalException na aplicacao NB: nomeie com O nome da excecao
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	//erros pra valores inesperados
	@ExceptionHandler(ClassCastException.class)
	public ResponseEntity<String> handleClassCastException(ClassCastException ex){
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); 
	}
	//erros pra entidades nao encontradas
	@ExceptionHandler(EntityNotFoundException.class) 
   public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex){
	   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); 
   }
	//erros de sql
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<Map<String , String>> handleSqlException(SQLException sql){
		Map<String, String> erro  = new HashMap<>();
		erro.put("erro", sql.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);  
	}
	//erros de nullPointer
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex){
		Map<String , String> erro = new HashMap<>();
		erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro); 
	}
} 
 

