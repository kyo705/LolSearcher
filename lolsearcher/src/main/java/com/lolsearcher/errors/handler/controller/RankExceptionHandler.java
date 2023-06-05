package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import com.lolsearcher.errors.exception.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.errors.exception.rank.NonUniqueRankTypeException;
import com.lolsearcher.search.rank.RankController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.INTERNAL_SERVER_ERROR_ENTITY_NAME;
import static com.lolsearcher.config.ErrorResponseEntityConfig.NOT_FOUND_ENTITY_NAME;

@Order(1)
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {RankController.class})
public class RankExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({IncorrectSummonerRankSizeException.class, NonUniqueRankTypeException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidDBException(Exception e){

        log.error(e.getMessage());

        return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidSummonerIdException(Exception e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
    }
}
