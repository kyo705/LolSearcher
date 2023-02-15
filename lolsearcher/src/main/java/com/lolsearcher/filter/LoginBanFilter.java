package com.lolsearcher.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lolsearcher.constant.RedisCacheConstants.LOGIN_BAN_KEY;
import static com.lolsearcher.constant.UriConstants.LOGIN_URI;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class LoginBanFilter implements Filter {

	private final CacheManager cacheManager;
	private final ResponseEntity<ErrorResponseBody> forbiddenResponseEntity;
	private final ObjectMapper objectMapper;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String ipAddress = request.getRemoteAddr();
		String uri = ((HttpServletRequest)request).getRequestURI();

		if(!uri.equals(LOGIN_URI) || requireNonNull(cacheManager.getCache(LOGIN_BAN_KEY)).get(ipAddress) == null) {
			chain.doFilter(request, response);
		}else{
			log.info("IP : {} 클라이언트는 로그인 권한이 없음", ipAddress);
			rejectRequest((HttpServletResponse) response);
		}
	}

	private void rejectRequest(HttpServletResponse response) {

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
