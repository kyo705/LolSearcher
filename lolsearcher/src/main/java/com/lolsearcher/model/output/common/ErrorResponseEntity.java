package com.lolsearcher.model.output.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.lolsearcher.constant.BeanNameConstants.*;

@Configuration
public class ErrorResponseEntity {

    @Bean
    public HttpHeaders headers(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setDate(System.currentTimeMillis());

        return headers;
    }

    @Qualifier(BAD_GATEWAY_ENTITY_NAME)
    @Bean(BAD_GATEWAY_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> badGatewayEntity() {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.BAD_GATEWAY.value())
                                .errorMessage("외부 서버에서 문제가 발생")
                                .build()
                );
    }

    @Qualifier(FORBIDDEN_ENTITY_NAME)
    @Bean(FORBIDDEN_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> forbiddenEntity() {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.FORBIDDEN.value())
                                .errorMessage("해당 클라이언트 접근 권한 없음")
                                .build()
                );
    }

    @Qualifier(BAD_REQUEST_ENTITY_NAME)
    @Bean(BAD_REQUEST_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> badRequestEntity(){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.BAD_REQUEST.value())
                                .errorMessage("잘못된 요청입니다. 다시 한 번 확인해주세요.")
                                .build()
                );
    }

    @Qualifier(NOT_FOUND_ENTITY_NAME)
    @Bean(NOT_FOUND_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> notFoundEntity(){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.NOT_FOUND.value())
                                .errorMessage("요청한 파라미터에 대한 정보 없음")
                                .build()
                );
    }

    @Qualifier(TOO_MANY_REQUESTS_ENTITY_NAME)
    @Bean(TOO_MANY_REQUESTS_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> tooManyRequestsEntity(){
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                                .errorMessage("현재 너무 많은 요청이 들어옴")
                                .build()
                );
    }

    @Qualifier(INTERNAL_SERVER_ERROR_ENTITY_NAME)
    @Bean(INTERNAL_SERVER_ERROR_ENTITY_NAME)
    public ResponseEntity<ErrorResponseBody> internalServerErrorEntity(){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(headers())
                .body(
                        ErrorResponseBody
                                .builder()
                                .errorStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .errorMessage("LOLSEARCHER 서버에서 문제가 발생됨")
                                .build()
                );
    }
}
