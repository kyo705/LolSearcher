package com.lolsearcher.filter.ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.FORBIDDEN_ENTITY_NAME;
import static com.lolsearcher.constant.LolSearcherConstants.LOGIN_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheNameConstants.LOGIN_BAN;
import static com.lolsearcher.constant.UriConstants.LOGIN_URI;

@RequiredArgsConstructor
@Slf4j
public class LoginBanFilter implements Filter {

	private final CacheManager cacheManager;
	private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;
	private final ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String ipAddress = request.getRemoteAddr();
		String uri = ((HttpServletRequest)request).getRequestURI();

		isAuthorization(ipAddress);
		if(!uri.equals(LOGIN_URI) || isAuthorization(ipAddress)) {
			chain.doFilter(request, response);
		}else{
			log.info("IP : {} 클라이언트는 로그인 권한이 없음", ipAddress);
			rejectRequest((HttpServletResponse) response);
		}
	}

	private boolean isAuthorization(String ipAddress) {

		Cache cache = cacheManager.getCache(LOGIN_BAN);
		assert cache != null;

		return cache.get(ipAddress) == null || cache.get(ipAddress, Integer.class) < LOGIN_BAN_COUNT;
	}

	private void rejectRequest(HttpServletResponse response) {

		ResponseEntity<ErrorResponseBody> forbiddenResponseEntity = responseEntities.get(FORBIDDEN_ENTITY_NAME);

		response.setStatus(forbiddenResponseEntity.getStatusCodeValue());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		try {
			String body = objectMapper.writeValueAsString(forbiddenResponseEntity.getBody());
			response.getWriter().write(body);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
