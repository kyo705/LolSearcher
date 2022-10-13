package com.lolsearcher.exception;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

	@ExceptionHandler(value = NoResultException.class)
	public ResponseEntity<Map<String, String>> NoResultExceptionHandler(NoResultException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> map = new HashMap<>();
		map.put("error code", "404");
		map.put("error message", "data not found");
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.headers(headers)
				.body(map);
	}
	
	@ExceptionHandler(value = MalformedURLException.class)
	public ResponseEntity<Map<String, String>> ExceptionHandler(MalformedURLException e){
		HttpHeaders headers = new HttpHeaders();
		System.out.println(e.getLocalizedMessage());
		Map<String, String> map = new HashMap<>();
		map.put("error code", "400");
		map.put("error message", "bad request");
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.headers(headers)
				.body(map);
	}
	
	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> ForbiddenHandler(AccessDeniedException e){
		HttpHeaders headers = new HttpHeaders();
		System.out.println(e.getLocalizedMessage());
		Map<String, String> map = new HashMap<>();
		map.put("error code", "403");
		map.put("error message", "unauthorized");
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.headers(headers)
				.body(map);
	}
	
	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> ForbiddenHandler(DataIntegrityViolationException e){
		HttpHeaders headers = new HttpHeaders();
		System.out.println(e.getLocalizedMessage());
		Map<String, String> map = new HashMap<>();
		map.put("error code", "201");
		map.put("error message", "created");
		
		return ResponseEntity.status(HttpStatus.CREATED)
				.headers(headers)
				.body(map);
	}
}
