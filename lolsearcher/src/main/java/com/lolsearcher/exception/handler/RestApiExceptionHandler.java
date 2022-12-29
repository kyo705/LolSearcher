package com.lolsearcher.exception.handler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


import com.lolsearcher.constant.RestApiConstants;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lolsearcher.controller.RestApiController;

@RestControllerAdvice(assignableTypes = {RestApiController.class})
public class RestApiExceptionHandler {

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<Map<String, String>> EmptyResultDataAccessExceptionHandler(
			EmptyResultDataAccessException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put(RestApiConstants.ERROR_CODE, Integer.toString(HttpStatus.NOT_FOUND.value()));
		body.put(RestApiConstants.ERROR_MESSAGE, HttpStatus.NOT_FOUND.getReasonPhrase());
		
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.headers(headers)
				.body(body);
	}
	
	@ExceptionHandler(MalformedURLException.class)
	public ResponseEntity<Map<String, String>> ExceptionHandler(MalformedURLException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put(RestApiConstants.ERROR_CODE, Integer.toString(HttpStatus.BAD_REQUEST.value()));
		body.put(RestApiConstants.ERROR_MESSAGE, HttpStatus.BAD_REQUEST.getReasonPhrase());
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.headers(headers)
				.body(body);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> ForbiddenHandler(AccessDeniedException e){
		HttpHeaders headers = new HttpHeaders();
		
		Map<String, String> body = new HashMap<>();
		body.put(RestApiConstants.ERROR_CODE, Integer.toString(HttpStatus.FORBIDDEN.value()));
		body.put(RestApiConstants.ERROR_MESSAGE, HttpStatus.FORBIDDEN.getReasonPhrase());
		
		return ResponseEntity
				.status(HttpStatus.FORBIDDEN)
				.headers(headers)
				.body(body);
	}

	@ExceptionHandler(CannotCreateTransactionException.class)
	public ResponseEntity<Map<String, String>> gatewayTimeoutHandler(CannotCreateTransactionException e){

		ResponseEntity<Map<String, String>> responseEntity = null;

		if(e.getCause().getClass().equals(JDBCConnectionException.class)){
			responseEntity = JDBCConnectionExceptionHandle();
		}

		return responseEntity;
	}

	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<Map<String, String>> anyOtherExceptionHandler(){

		HttpHeaders headers = new HttpHeaders();

		Map<String, String> body = new HashMap<>();
		body.put(RestApiConstants.ERROR_CODE, Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		body.put(RestApiConstants.ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.headers(headers)
				.body(body);
	}

	private ResponseEntity<Map<String, String>> JDBCConnectionExceptionHandle() {
		HttpHeaders headers = new HttpHeaders();

		Map<String, String> body = new HashMap<>();
		body.put(RestApiConstants.ERROR_CODE, Integer.toString(HttpStatus.GATEWAY_TIMEOUT.value()));
		body.put(RestApiConstants.ERROR_MESSAGE, HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase());

		return ResponseEntity
				.status(HttpStatus.GATEWAY_TIMEOUT)
				.headers(headers)
				.body(body);
	}
}
