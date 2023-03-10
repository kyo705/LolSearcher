package com.lolsearcher.exception.handler.filter.springsecurity.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.response.front.user.ResponseSuccessDto;
import com.lolsearcher.service.user.join.JoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JoinAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JoinService joinService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("이메일 본인 인증이 성공했습니다.");

        LolSearcherUser user = (LolSearcherUser) authentication.getPrincipal();
        joinService.joinUser(user);

        log.info("회원 : {} DB에 저장 성공", user.getEmail());


        ResponseSuccessDto responseSuccessDto = new ResponseSuccessDto("회원가입 성공!!");
        String body = objectMapper.writeValueAsString(responseSuccessDto);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
