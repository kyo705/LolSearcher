package com.lolsearcher.exception.handler.filter.springsecurity.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.user.login.IdentificationAuthenticationException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JoinAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.info("이메일 본인 인증이 실패했습니다.");

        if(exception instanceof IdentificationAuthenticationException){

            int statusCode = ((IdentificationAuthenticationException)exception).getStatusCode();
            String errorMessage = exception.getMessage();

            ErrorResponseBody responseBody = new ErrorResponseBody(statusCode, errorMessage);

            String body = objectMapper.writeValueAsString(responseBody);

            response.setStatus(statusCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(body);

            return;
        }

    }
}
