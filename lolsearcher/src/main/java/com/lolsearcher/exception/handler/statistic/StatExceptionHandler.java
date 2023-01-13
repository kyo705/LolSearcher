package com.lolsearcher.exception.handler.statistic;

import com.lolsearcher.controller.mostchamp.MostChampController;
import com.lolsearcher.controller.statistic.ChampionController;
import com.lolsearcher.model.output.common.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.QueryTimeoutException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.BAD_GATEWAY_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.BAD_REQUEST_ENTITY_NAME;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = {MostChampController.class, ChampionController.class})
public class StatExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({DataIntegrityViolationException.class, QueryTimeoutException.class})
    public ResponseEntity<ErrorResponseBody> getResponseError(Exception e) {

        log.error("DB에 대한 문제 발생");
        log.error(e.getMessage());
        return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseBody> getResponseError(MethodArgumentNotValidException e) {

        log.error("잘못된 파라미터 요청");
        log.error(e.getMessage());

        return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
    }
}
