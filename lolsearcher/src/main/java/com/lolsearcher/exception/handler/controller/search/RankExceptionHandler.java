package com.lolsearcher.exception.handler.controller.search;

import com.lolsearcher.constant.BeanNameConstants;
import com.lolsearcher.controller.search.rank.RankController;
import com.lolsearcher.exception.exception.search.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.exception.exception.search.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {RankController.class})
public class RankExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({IncorrectSummonerRankSizeException.class, NonUniqueRankTypeException.class})
    public ResponseEntity<ErrorResponseBody> handleInvalidDBException(Exception e){

        log.error(e.getMessage());
        return errorResponseEntities.get(BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }
}
