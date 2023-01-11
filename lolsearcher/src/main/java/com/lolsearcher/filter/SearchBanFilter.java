package com.lolsearcher.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.lolsearcher.constant.CacheConstants.SEARCH_BAN_KEY;
import static com.lolsearcher.constant.UriConstants.NON_AUTHORITY_URI;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class SearchBanFilter implements Filter {

	private final CacheManager cacheManager;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String requestIp = request.getRemoteAddr();
		
		String requestUri = ((HttpServletRequest)request).getRequestURI();


		if(requireNonNull(cacheManager.getCache(SEARCH_BAN_KEY)).get(requestIp) == null || requestUri.equals(NON_AUTHORITY_URI)) {
			chain.doFilter(request, response);
		}else {
			//벤 페이지로 이동
			((HttpServletResponse)response).sendRedirect(NON_AUTHORITY_URI);
		}
	}

}
