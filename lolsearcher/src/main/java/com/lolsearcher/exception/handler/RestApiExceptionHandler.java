package com.lolsearcher.exception.handler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lolsearcher.controller.RestApiController;

@RestControllerAdvice(assignableTypes = RestApiController.class)
public class RestApiExceptionHandler {

	@ExceptionHandler(value = EmptyResultDataAccessException.class)
	private ResponseEntity<Map<String, String>> EmptyResultDataAccessExceptionHandler(
			EmptyResultDataAccessException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put("error code", Integer.toString(HttpStatus.NOT_FOUND.value()));
		body.put("error message", HttpStatus.NOT_FOUND.getReasonPhrase());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.headers(headers)
				.body(body);
	}
	
	@ExceptionHandler(value = MalformedURLException.class)
	private ResponseEntity<Map<String, String>> ExceptionHandler(MalformedURLException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put("error code", Integer.toString(HttpStatus.BAD_REQUEST.value()));
		body.put("error message", HttpStatus.BAD_REQUEST.getReasonPhrase());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.headers(headers)
				.body(body);
	}
	
	@ExceptionHandler(value = AccessDeniedException.class)
	private ResponseEntity<Map<String, String>> ForbiddenHandler(AccessDeniedException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put("error code", Integer.toString(HttpStatus.FORBIDDEN.value()));
		body.put("error message", HttpStatus.FORBIDDEN.getReasonPhrase());
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.headers(headers)
				.body(body);
	}
	
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	private ResponseEntity<Map<String, String>> ForbiddenHandler(DataIntegrityViolationException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put("error code", Integer.toString(HttpStatus.CREATED.value()));
		body.put("error message", HttpStatus.CREATED.getReasonPhrase());
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.headers(headers)
				.body(body);
	}
	
	//DB 커넥션 timeout -> 408 error
	//DB 에러 -> 500 error
}
