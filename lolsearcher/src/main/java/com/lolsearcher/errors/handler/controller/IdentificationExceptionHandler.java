package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.errors.exception.user.InvalidUserRoleException;
import com.lolsearcher.errors.exception.user.NotExistingUserException;
import com.lolsearcher.user.identification.IdentificationController;
import com.lolsearcher.user.identification.IdentificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static com.lolsearcher.errors.ErrorConstant.BAD_GATEWAY_ENTITY_NAME;
import static com.lolsearcher.errors.ErrorConstant.INTERNAL_SERVER_ERROR_ENTITY_NAME;

@Order(1)
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = IdentificationController.class)
public class IdentificationExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;
    private final IdentificationRepository identificationRepository;

    @ExceptionHandler({WebClientResponseException.class})
    public ResponseEntity<ErrorResponseBody> handleWebClientResponseException(WebClientResponseException ex){

        log.error(ex.getMessage());

        return responseEntities.get(BAD_GATEWAY_ENTITY_NAME);
    }

    @ExceptionHandler({NotExistingUserException.class})
    public ResponseEntity<ErrorResponseBody> handleEmptyResultDataAccessException(NotExistingUserException ex){

        log.error(ex.getMessage());
        identificationRepository.delete(ex.getUserId());
        log.info(String.format("invalid userId : %s is deleted in identification repository", ex.getUserId()));

        return responseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }

    @ExceptionHandler({InvalidUserRoleException.class})
    public ResponseEntity<ErrorResponseBody> handleIllegalAccessException(InvalidUserRoleException ex){

        log.error(ex.getMessage());
        identificationRepository.delete(ex.getUserId());
        log.info(String.format("invalid userId : %s is deleted in identification repository", ex.getUserId()));

        return responseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }
}
