package com.lolsearcher.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@Configuration
public class ErrorResponseEntityConfig {

    public static final String TEMPORARY_REDIRECT_ENTITY_NAME = "temporaryRedirectEntity";      /* 307 */
    public static final String BAD_REQUEST_ENTITY_NAME = "badRequestErrorEntity";               /* 400 */
    public static final String FORBIDDEN_ENTITY_NAME = "forbiddenEntity";                       /* 403 */
    public static final String NOT_FOUND_ENTITY_NAME = "notFoundEntity";                        /* 404 */
    public static final String CONFLICT_ENTITY_NAME = "conflictErrorEntity";                    /* 409 */
    public static final String TOO_MANY_REQUESTS_ENTITY_NAME = "tooManyRequestsEntity";         /* 429 */
    public static final String INTERNAL_SERVER_ERROR_ENTITY_NAME = "internalServerErrorEntity"; /* 500 */
    public static final String BAD_GATEWAY_ENTITY_NAME = "badGatewayEntity";                    /* 502 */
    public static final String TIME_OUT_ENTITY_NAME = "gatewayTimeoutEntity";                   /* 504 */
    public static final String SUMMONER_RENEW_REQUEST_URI = "localhost:80/renew/summoner";

    @Qualifier(TEMPORARY_REDIRECT_ENTITY_NAME)
    @Bean(TEMPORARY_REDIRECT_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> redirectEntity() {

        HttpHeaders headers = createDefaultHeader();
        headers.setLocation(URI.create(SUMMONER_RENEW_REQUEST_URI));

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.TEMPORARY_REDIRECT.value())
                .errorMessage("갱신 요청 URI로 리다이렉트")
                .build();

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .headers(headers)
                .body(body);
    }

    @Qualifier(BAD_REQUEST_ENTITY_NAME)
    @Bean(BAD_REQUEST_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> badRequestEntity(){

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("잘못된 요청입니다. 다시 한 번 확인해주세요.")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(FORBIDDEN_ENTITY_NAME)
    @Bean(FORBIDDEN_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> forbiddenEntity() {

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.FORBIDDEN.value())
                .errorMessage("해당 클라이언트 접근 권한 없음")
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(NOT_FOUND_ENTITY_NAME)
    @Bean(NOT_FOUND_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> notFoundEntity(){

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.NOT_FOUND.value())
                .errorMessage("요청한 파라미터에 대한 정보 없음")
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(CONFLICT_ENTITY_NAME)
    @Bean(CONFLICT_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> conflictEntity() {

        HttpHeaders headers = createDefaultHeader();

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.CONFLICT.value())
                .errorMessage("이미 존재하는 데이터입니다.")
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .headers(headers)
                .body(body);
    }

    @Qualifier(TOO_MANY_REQUESTS_ENTITY_NAME)
    @Bean(TOO_MANY_REQUESTS_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> tooManyRequestsEntity(){

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .errorMessage("현재 너무 많은 요청이 들어옴")
                .build();

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(INTERNAL_SERVER_ERROR_ENTITY_NAME)
    @Bean(INTERNAL_SERVER_ERROR_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> internalServerErrorEntity(){

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("LOLSEARCHER 서버에서 문제가 발생됨")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(BAD_GATEWAY_ENTITY_NAME)
    @Bean(BAD_GATEWAY_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> badGatewayEntity() {

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.BAD_GATEWAY.value())
                .errorMessage("외부 서버에서 문제가 발생")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .headers(createDefaultHeader())
                .body(body);
    }

    @Qualifier(TIME_OUT_ENTITY_NAME)
    @Bean(TIME_OUT_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> gatewayTimeoutEntity() {

        ErrorResponseBody body = ErrorResponseBody.builder()
                .errorStatusCode(HttpStatus.GATEWAY_TIMEOUT.value())
                .errorMessage("외부 서버 응답 시간 초과")
                .build();

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .headers(createDefaultHeader())
                .body(body);
    }

    private HttpHeaders createDefaultHeader(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setDate(System.currentTimeMillis());

        return headers;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ErrorResponseBody {

        private int errorStatusCode;
        private String errorMessage;

    }
}
