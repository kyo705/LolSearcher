package com.lolsearcher.filter.ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.FORBIDDEN_ENTITY_NAME;
import static com.lolsearcher.constant.LolSearcherConstants.SEARCH_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheNameConstants.LOGIN_BAN;

@Slf4j
public class SearchBanFilter implements Filter {

	private final Cache cache;
	private final Map<String, ResponseEntity<ErrorResponseBody>> responseEntities;
	private final ObjectMapper objectMapper;

	public SearchBanFilter(CacheManager cacheManager,
						  Map<String, ResponseEntity<ErrorResponseBody>> responseEntities,
						  ObjectMapper objectMapper){

		if(cacheManager.getCache(LOGIN_BAN) == null){
			log.error("서치 관련 캐시가 존재하지 않음");
			throw new IllegalArgumentException();
		}
		this.cache = cacheManager.getCache(LOGIN_BAN);
		this.responseEntities = responseEntities;
		this.objectMapper = objectMapper;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String requestIp = request.getRemoteAddr();

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
