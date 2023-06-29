package com.lolsearcher.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.notification.NotificationService;
import com.lolsearcher.notification.RequestLoginNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.lolsearcher.notification.NotificationConstant.LOGIN_ALARM_SUBJECT;
import static com.lolsearcher.notification.NotificationDevice.E_MAIL;
import static com.lolsearcher.user.LoginSecurityState.ALARM;
import static com.lolsearcher.utils.ResponseDtoFactory.getUserDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class LolSearcherAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Value("${lolsearcher.notification.auth}")
    private Long MASTER_USER_ID;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String ipAddress = request.getRemoteAddr();
        String sessionId = request.getSession().getId(); /* session fixation 공격 방지 전략 로직 수행 후 얻는 session id */

        LolsearcherUserDetails details = (LolsearcherUserDetails) authentication.getPrincipal();

        checkUserLoginSecurity(details, sessionId, ipAddress);
        handleSuccessResponse(details, response);
    }

    private void checkUserLoginSecurity(LolsearcherUserDetails details, String sessionId, String ipAddress) {

        int securityLevel = details.getLoginSecurity().getLevel();

        if(securityLevel >= ALARM.getLevel()) {
            RequestLoginNotificationDto contents = new RequestLoginNotificationDto(sessionId, ipAddress);

            notificationService.sendNotificationMessage(E_MAIL, MASTER_USER_ID, List.of(details.getId()), LOGIN_ALARM_SUBJECT, contents);
        }
    }

    private void handleSuccessResponse(LolsearcherUserDetails details, HttpServletResponse response) throws IOException {

        log.info("로그인 성공");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String body = objectMapper.writeValueAsString(getUserDto(details));
        response.getWriter().write(body);
    }
}
