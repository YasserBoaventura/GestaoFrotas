package com.GestaoRotas.GestaoRotas.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.*;

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
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String >> handleRuntimeException(RuntimeException ex){
		Map<String, String > erro = new HashMap<>();
		erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}  
	@ExceptionHandler(ClassNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUserNameNotFoutException(ClassNotFoundException e){
		Map<String ,String> erro = new HashMap<>();
		erro.put("erro",e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro); 
 	}
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex){
	Map<String , String > erro = new HashMap<>();
	erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro); 
	}
	@ExceptionHandler(DisabledException.class) 
	public ResponseEntity<Map<String, String>> handleDisableException(DisabledException ex){
		Map<String, String> erro = new HashMap<>();
		erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	@ExceptionHandler(LockedException.class)
	public ResponseEntity<Map<String ,String>> handleLockedException(LockedException ex){
		Map<String, String> erro = new HashMap<>();
		erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String , String>> handleResponseStatusException(ResponseStatusException ex){
		Map<String ,String> erro = new HashMap<>();
		erro.put("erro", ex.getMessage()); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro); 
	} 
	@ExceptionHandler(InvalidClassException.class) 
	public ResponseEntity<Map<String , String>> handleIvalidClassException(InvalidClassException ex){
		Map<String,String> erro = new HashMap<>();
		erro.put("erro", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro); 
	} 
	@ExceptionHandler(UsernameNotFoundException.class) 
	public ResponseEntity<Map<String ,String>>  handleUsernameNotFoundException(UsernameNotFoundException ex){
		Map<String, String>  erro = new HashMap<>();
		erro.put("erro" , ex.getMessage()); 
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
		}
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
       Map<String,String> response = new HashMap<>();
        response.put("erro", ex.getMessage());
           return ResponseEntity
                .badRequest() // HTTP 400
                .body(response);  
    } 
    @ExceptionHandler(NumberFormatException.class) 
    public ResponseEntity<Map<String , String>> handleNumberFormatException(NumberFormatException ex){
      Map<String ,String> response = new HashMap<>(); 
      response.put("erro",ex.getMessage()); 
    	return ResponseEntity.badRequest().body(response);   
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleFileNotFoundException(FileNotFoundException ex){
    	Map<String,String> response = new HashMap<>(); 
    	response.put("erro", ex.getMessage()); 
    	return ResponseEntity.badRequest().body(response); 
    } 
    @ExceptionHandler(IOException.class) 
    public ResponseEntity<Map<String,String>> handleIOException(IOException ex){
    	Map<String,String> response = new HashMap<>(); 
    	response.put("erro", ex.getMessage()); 
    	return ResponseEntity.badRequest().body(response); 
    }  
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)  
    public ResponseEntity<Map<String,String>> handleArrayIndexBoundsException(ArrayIndexOutOfBoundsException ex){
    	Map<String, String> response = new HashMap<>(); 
    	response.put("erro", ex.getMessage());  
    	return ResponseEntity.badRequest().body(response); 
    }
    @ExceptionHandler(BusinessRoles.class) 
    public ResponseEntity<Map<String, String>> handleBusinessRoles(BusinessRoles ex){
    	Map<String, String> response = new HashMap<>();
    	response.put("erro", ex.getMessage());  
    	return ResponseEntity.badRequest().body(response); 
    }
  }

 
 

