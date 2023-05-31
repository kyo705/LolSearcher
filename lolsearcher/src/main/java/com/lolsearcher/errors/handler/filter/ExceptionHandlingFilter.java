package com.lolsearcher.errors.handler.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ExceptionHandlingFilter implements Filter {

    private final ObjectMapper objectMapper;

    public ExceptionHandlingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            chain.doFilter(request, response);
        }catch(IllegalArgumentException e){

            log.error(e.getMessage());
            // 로그인, 회원가입, 서치 권한 없는 경우(403), 회원가입 인증 실패 경우(400)
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String body = objectMapper.writeValueAsString(new ErrorResponseBody(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
            resp.getWriter().write(body);
        }

        // databind exception 발생시 400에러 발생
    }
}
