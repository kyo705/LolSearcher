package com.lolsearcher.filter.ban;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lolsearcher.constant.RedisCacheConstants.SEARCH_BAN_KEY;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class SearchBanFilter implements Filter {

	private final CacheManager cacheManager;
	private final ResponseEntity<ErrorResponseBody> forbiddenResponseEntity;
	private final ObjectMapper objectMapper;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String requestIp = request.getRemoteAddr();

		if(requireNonNull(cacheManager.getCache(SEARCH_BAN_KEY)).get(requestIp) == null) {
			chain.doFilter(request, response);
			return;
		}
		rejectRequest((HttpServletResponse) response);
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
