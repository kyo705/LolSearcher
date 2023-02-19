package com.lolsearcher.auth.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.service.ban.LoginIpBanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserLoginFailHandler implements AuthenticationFailureHandler {

	private final ResponseEntity<ErrorResponseBody> internalServerErrorEntity;
	private final LoginIpBanService loginIpBanService;
	private final ObjectMapper objectMapper;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){

		String failureMessage;
		String ipAddress = request.getRemoteAddr();

		if(loginIpBanService.isExceedBanCount(ipAddress)) {
			loginIpBanService.registerBanList(ipAddress);

			failureMessage = "로그인 시도 횟수 초과!! 10분 뒤 다시 시도해주세요";
			handleExpectedException(response, failureMessage);
			return;
		}
		if (exception instanceof AuthenticationServiceException) {
			failureMessage = "존재하지 않는 사용자입니다.";
		} else if(exception instanceof BadCredentialsException) {
			failureMessage = "아이디 또는 비밀번호가 틀립니다.";
		} else if(exception instanceof LockedException) {
			failureMessage = "잠긴 계정입니다..";
		} else if(exception instanceof DisabledException) {
			failureMessage = "비활성화된 계정입니다..";
		} else if(exception instanceof AccountExpiredException) {
			failureMessage = "만료된 계정입니다..";
		} else if(exception instanceof CredentialsExpiredException) {
			failureMessage = "비밀번호가 만료되었습니다.";
		} else {
			log.error(exception.getMessage());
			handleUnexpectedException(response);
			return;
		}
		handleExpectedException(response, failureMessage);
	}

	private void handleUnexpectedException(HttpServletResponse response) {

		response.setStatus(internalServerErrorEntity.getStatusCodeValue());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			String body = objectMapper.writeValueAsString(internalServerErrorEntity.getBody());
			response.getWriter().write(body);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void handleExpectedException(HttpServletResponse response, String failureMessage){

		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			response.getWriter().write(failureMessage);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
