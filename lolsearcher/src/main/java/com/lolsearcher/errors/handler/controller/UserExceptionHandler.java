package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import com.lolsearcher.user.UserController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.CONFLICT_ENTITY_NAME;

@Order(1)
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;

	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<ErrorResponseBody> handleConflictError(Exception e) {

		log.info(e.getMessage());

		return responseEntities.get(CONFLICT_ENTITY_NAME);
	}
}
