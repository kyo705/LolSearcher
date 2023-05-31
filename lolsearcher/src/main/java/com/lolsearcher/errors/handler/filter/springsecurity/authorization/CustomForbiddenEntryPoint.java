package com.lolsearcher.errors.handler.filter.springsecurity.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.FORBIDDEN_ENTITY_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomForbiddenEntryPoint extends Http403ForbiddenEntryPoint {

    private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
            throws IOException {

        log.debug("해당 클라이언트 : {} 는 접근 권한이 없음", request.getRemoteAddr());

        int statusCode = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getStatusCodeValue();
        String body = objectMapper.writeValueAsString(errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getBody());

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
