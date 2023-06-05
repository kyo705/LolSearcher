package com.lolsearcher.ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.config.ErrorResponseEntityConfig.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.ban.BanConstant.SEARCH_BAN;
import static com.lolsearcher.ban.BanConstant.SEARCH_BAN_COUNT;
import static com.lolsearcher.config.ErrorResponseEntityConfig.FORBIDDEN_ENTITY_NAME;

@RequiredArgsConstructor
@Slf4j
public class SearchBanFilter implements Filter {

	private final CacheManager cacheManager;
	private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;
	private final ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String requestIp = request.getRemoteAddr();
		Cache cache = cacheManager.getCache(SEARCH_BAN);

		assert cache != null;
		if(cache.get(requestIp) == null || cache.get(requestIp,Integer.class) < SEARCH_BAN_COUNT) {
			chain.doFilter(request, response);
			return;
		}
		rejectRequest((HttpServletResponse) response);
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
