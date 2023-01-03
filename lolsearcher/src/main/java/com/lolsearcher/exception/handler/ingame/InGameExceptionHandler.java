package com.lolsearcher.exception.handler.ingame;

import com.lolsearcher.controller.ingame.InGameController;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.exception.summoner.NoExistSummonerException;
import com.lolsearcher.model.response.common.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.constant.BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = InGameController.class)
public class InGameExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({NoInGameException.class})
    public ResponseEntity<ErrorResponseBody> getNoInGameError(NoInGameException e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
    }

    @ExceptionHandler({NoExistSummonerException.class})
    public ResponseEntity<ErrorResponseBody> getNoSummonerDataError(NoExistSummonerException e) {

        log.error(e.getMessage());

        return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
    }

    @ExceptionHandler({WebClientResponseException.class})
    public ResponseEntity<ErrorResponseBody> getRiotGamesServerError(WebClientResponseException e) {

        if (e.getStatusCode() == HttpStatus.BAD_GATEWAY ||
                e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ||
                e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
                e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
        ) {
            log.error("라이엇 게임 서버에서 에러가 발생");
            log.error(e.getMessage());
            return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
        }
        if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {

            log.error("너무 많은 API 요청이 들어옴");
            return errorResponseEntities.get(TOO_MANY_REQUESTS_ENTITY_NAME);
        }

        log.error("해당 서버에서 RIOT GAMES API 설정이 잘못됨");
        return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
    }
}
