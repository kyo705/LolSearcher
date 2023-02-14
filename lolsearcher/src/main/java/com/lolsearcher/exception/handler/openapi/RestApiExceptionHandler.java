package com.lolsearcher.exception.handler.openapi;

import com.lolsearcher.controller.opnapi.RestApiController;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.MalformedURLException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {RestApiController.class})
public class RestApiExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<ErrorResponseBody> EmptyResultDataAccessExceptionHandler(EmptyResultDataAccessException e){

		log.error(e.getMessage());

		return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
	}
	
	@ExceptionHandler(MalformedURLException.class)
	public ResponseEntity<ErrorResponseBody> ExceptionHandler(MalformedURLException e){

		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseBody> ForbiddenHandler(AccessDeniedException e){

		log.error(e.getMessage());

		return errorResponseEntities.get(FORBIDDEN_ENTITY_NAME);
	}

	@ExceptionHandler(QueryTimeoutException.class)
	public ResponseEntity<ErrorResponseBody> gatewayTimeoutHandler(QueryTimeoutException e){

		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseBody> anyOtherExceptionHandler(RuntimeException e){

		log.error(e.getMessage());

		return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
	}
}
