package com.lolsearcher.exception.handler.filter.springsecurity.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.user.login.NotYetSecondLevelLoginException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
import com.lolsearcher.service.ban.LoginIpBanService;
import com.lolsearcher.service.user.identification.JWTIdentificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.INTERNAL_SERVER_ERROR_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.TEMPORARY_REDIRECT_ENTITY_NAME;
import static com.lolsearcher.constant.UriConstants.SECOND_LEVEL_LOGIN_FORM_URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	private final LoginIpBanService loginIpBanService;
	private final JWTIdentificationService identificationService;
	private final ObjectMapper objectMapper;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){

		//2차 로그인 필요 시
		if(exception instanceof NotYetSecondLevelLoginException){
			Authentication authentication = ((NotYetSecondLevelLoginException) exception).getAuthentication();
			Assert.notNull(authentication, "Authentication 객체는 반드시 존재해야합니다.");

			LolsearcherUserDetails userDetails = (LolsearcherUserDetails) authentication.getPrincipal();
			Assert.notNull(userDetails, "Authentication 안에 LolsearcherUserDetails 객체는 반드시 존재해야합니다.");

			String jwt = (String) identificationService.createAndSendCertification(authentication, userDetails.getUsername());

			handleTemporarySuccess(response, jwt);
			return;
		}

		//1차 로그인 실패 시
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

	private void handleTemporarySuccess(HttpServletResponse response, String jwt) {

		ResponseEntity<ErrorResponseBody> temporaryRedirectResponseEntity = errorResponseEntities.get(TEMPORARY_REDIRECT_ENTITY_NAME);

		response.setHeader(HttpHeaders.AUTHORIZATION, jwt);
		response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader(HttpHeaders.LOCATION, SECOND_LEVEL_LOGIN_FORM_URI);

		try {
			String body = objectMapper.writeValueAsString(temporaryRedirectResponseEntity.getBody());
			response.getWriter().write(body);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
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
			response.getWriter().write(failureMessage);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
