package com.lolsearcher.exception.handler.filter.springsecurity.authentication;

import com.lolsearcher.exception.exception.user.login.IdentificationAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class SecondLevelLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        if(exception instanceof IdentificationAuthenticationException){

            String errorMessage =  exception.getMessage();
            handleExpectedException(response, errorMessage);
            return;
        }
    }

    private void handleExpectedException(HttpServletResponse response, String failureMessage) throws IOException {

        log.info(failureMessage);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.getWriter().write(failureMessage);
    }
}
