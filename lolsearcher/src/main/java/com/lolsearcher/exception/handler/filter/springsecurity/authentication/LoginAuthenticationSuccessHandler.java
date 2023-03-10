package com.lolsearcher.exception.handler.filter.springsecurity.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.constant.enumeration.LoginSecurityPolicyStatus;
import com.lolsearcher.constant.enumeration.NotificationDevice;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
import com.lolsearcher.model.response.front.user.ResponseSuccessDto;
import com.lolsearcher.service.notification.NotificationService;
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
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr();
        String sessionId = request.getSession().getId(); /* session fixation 공격 방지 전략 로직 수행 후 얻는 session id */

        checkUserLoginSecurityLevel(authentication, sessionId, ipAddress);
        handleSuccessResponse(response);
    }

    private void checkUserLoginSecurityLevel(Authentication authentication, String sessionId, String ipAddress) {

        LolsearcherUserDetails details = (LolsearcherUserDetails) authentication.getPrincipal();

        int securityLevel = details.getSecurityLevel();
        String email = details.getUsername();

        if(securityLevel >= LoginSecurityPolicyStatus.ALARM.getLevel()){
            notificationService.sendLoginMessage(NotificationDevice.E_MAIL, email, sessionId, ipAddress);
        }
    }

    private void handleSuccessResponse(HttpServletResponse response) throws IOException {

        log.info("로그인 성공");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseSuccessDto responseSuccessDto = new ResponseSuccessDto("로그인 성공!!");

        String body = objectMapper.writeValueAsString(responseSuccessDto);
        response.getWriter().write(body);
    }
}
