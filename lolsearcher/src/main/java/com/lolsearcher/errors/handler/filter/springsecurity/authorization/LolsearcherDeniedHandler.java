package com.lolsearcher.errors.handler.filter.springsecurity.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.errors.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.FORBIDDEN_ENTITY_NAME;

@RequiredArgsConstructor
@Component
public class LolsearcherDeniedHandler implements AccessDeniedHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		int statusCode = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getStatusCodeValue();
		ErrorResponseBody responseBody = errorResponseEntities.get(FORBIDDEN_ENTITY_NAME).getBody();
		String body = objectMapper.writeValueAsString(responseBody);

		response.setStatus(statusCode);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(body);
	}
}
