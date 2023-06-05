package com.lolsearcher.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.ban.LoginIpBanService;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
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
import java.util.Map;

import static com.lolsearcher.config.ErrorResponseEntityConfig.INTERNAL_SERVER_ERROR_ENTITY_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class LolSearcherAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	private final LoginIpBanService loginIpBanService;
	private final ObjectMapper objectMapper;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){

		String failureMessage;

		String ipAddress = request.getRemoteAddr();
		loginIpBanService.addBanCount(ipAddress);

		if(loginIpBanService.isExceedBanCount(ipAddress)) {
			failureMessage = "로그인 시도 횟수 초과!! 10분 뒤 다시 시도해주세요";
		}else if (exception instanceof AuthenticationServiceException) {
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

		ResponseEntity<ErrorResponseBody> internalServerErrorEntity = errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);

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
			String body = objectMapper.writeValueAsString(new ErrorResponseBody(HttpStatus.BAD_REQUEST.value(), failureMessage));
			response.getWriter().write(body);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
