package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.search.match.MatchController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.NOT_FOUND_ENTITY_NAME;

@Order(1)
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = MatchController.class)
public class MatchExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidSummonerIdException(Exception e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidPersistenceDataException(Exception e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }
}
