package com.lolsearcher.exception.handler.join;

import com.lolsearcher.controller.user.JoinController;
import com.lolsearcher.exception.exception.join.ExistedUserException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = JoinController.class)
public class JoinExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseBody> handleInvalidArgumentException(MethodArgumentNotValidException e) {

		log.error("잘못된 파라미터 요청" + e.getMessage());

		return responseEntities.get(BAD_REQUEST_ENTITY_NAME);
	}

	@ExceptionHandler(ExistedUserException.class)
	public ResponseEntity<ErrorResponseBody> handleConflictError(ExistedUserException e) {

		log.info(e.getMessage());

		return responseEntities.get(CONFLICT_ENTITY_NAME);
	}

	@ExceptionHandler(JpaSystemException.class)
	public ResponseEntity<ErrorResponseBody> handleJpaError(JpaSystemException e) {

		log.error(e.getMessage());

		return responseEntities.get(BAD_GATEWAY_ENTITY_NAME);
	}

	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseBody> handleAnyOtherError(Exception e) {

		log.error(e.getMessage());

		return responseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }
}
