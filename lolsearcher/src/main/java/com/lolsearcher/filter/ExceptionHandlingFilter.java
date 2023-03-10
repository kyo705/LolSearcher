package com.lolsearcher.filter;

import com.lolsearcher.exception.exception.LolSearcherException;

import javax.servlet.*;
import java.io.IOException;

public class ExceptionHandlingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            chain.doFilter(request, response);
        }catch(LolSearcherException e){
            // 로그인, 회원가입, 서치 권한 없는 경우(403), 회원가입 인증 실패 경우(400)
        }

        // databind exception 발생시 400에러 발생
    }
}
