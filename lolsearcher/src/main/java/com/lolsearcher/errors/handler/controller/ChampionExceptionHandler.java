package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.errors.exception.common.NoExistDataException;
import com.lolsearcher.errors.exception.search.champion.InvalidChampionIdException;
import com.lolsearcher.search.champion.ChampionController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.BAD_REQUEST_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME;

@Order(2)
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {ChampionController.class})
public class ChampionExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({NoExistDataException.class})
    public ResponseEntity<ErrorResponseBody> handleNoExistDataException(NoExistDataException e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }

    @ExceptionHandler({InvalidChampionIdException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidChampionIdException(InvalidChampionIdException e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
    }


}
