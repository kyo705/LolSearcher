package com.lolsearcher.exception.handler.search;

import com.lolsearcher.controller.search.summoner.SummonerController;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.exception.exception.summoner.NotExistedSummonerInGameServerException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = SummonerController.class)
public class SummonerExceptionHandler {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

    @ExceptionHandler({NotExistedSummonerInDBException.class})
    public ResponseEntity<ErrorResponseBody> handleNoExistSummonerInDBException(NotExistedSummonerInDBException ex){

        String summonerName = ex.getSummonerName();
        log.info("닉네임 '{}' 유저 정보를 갱신 요청하도록 URI 리다이렉트.", summonerName);

        return errorResponseEntities.get(TEMPORARY_REDIRECT_ENTITY_NAME);
    }

    @ExceptionHandler({NotExistedSummonerInGameServerException.class})
    public ResponseEntity<ErrorResponseBody> handleNoExistSummonerInGameServerException(NotExistedSummonerInGameServerException ex){

        String summonerName = ex.getSummonerName();
        log.error("닉네임 '{}' 은 현재 게임 환경에서 존재하지 않는 닉네임입니다.", summonerName);

        return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
    }

    @ExceptionHandler({WebClientResponseException.class})
    public ResponseEntity<ErrorResponseBody> handleWebClientResponseException(WebClientResponseException ex){

        log.error(ex.getMessage());

        return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
    }
}
