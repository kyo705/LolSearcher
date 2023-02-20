package com.lolsearcher.model.response.error;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.constant.UriConstants.SUMMONER_RENEW_REQUEST_URI;

@Configuration
public class ErrorResponseEntity {

    @Qualifier(REDIRECT_ENTITY_NAME)
    @Bean(REDIRECT_ENTITY_NAME)
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

    private HttpHeaders createDefaultHeader(){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setDate(System.currentTimeMillis());

        return headers;
    }
}
