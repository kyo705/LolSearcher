package com.lolsearcher.exception.handler.filter.springsecurity.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomForbiddenEntryPoint extends Http403ForbiddenEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {

        log.debug("해당 클라이언트 : {} 는 접근 권한이 없음", request.getRemoteAddr());

        int statusCode = HttpStatus.FORBIDDEN.value();
        String errorMessage = "접근 권한 없음";

        ErrorResponseBody responseBody = new ErrorResponseBody(statusCode, errorMessage);

        String body = objectMapper.writeValueAsString(responseBody);

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
