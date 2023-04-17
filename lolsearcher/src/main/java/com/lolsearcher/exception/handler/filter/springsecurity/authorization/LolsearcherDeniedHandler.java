package com.lolsearcher.exception.handler.filter.springsecurity.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class LolsearcherDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		int statusCode = HttpStatus.FORBIDDEN.value();
		String errorMessage = "접근 권한 없음";

		ErrorResponseBody responseBody = new ErrorResponseBody(statusCode, errorMessage);

		String body = objectMapper.writeValueAsString(responseBody);

		response.setStatus(statusCode);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(body);
	}
}
