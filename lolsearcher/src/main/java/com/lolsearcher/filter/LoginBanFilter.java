package com.lolsearcher.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.lolsearcher.constant.RedisCacheConstants.LOGIN_BAN_KEY;
import static com.lolsearcher.constant.UriConstants.LOGIN_FORM_URI;
import static com.lolsearcher.constant.UriConstants.LOGIN_URI;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class LoginBanFilter implements Filter {

	private final CacheManager cacheManager;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String request_ip = request.getRemoteAddr();
		String uri = ((HttpServletRequest)request).getRequestURI();

		if(requireNonNull(cacheManager.getCache(LOGIN_BAN_KEY)).get(request_ip) == null || !uri.equals(LOGIN_URI)) {
			chain.doFilter(request, response);
		}else {
			//벤 페이지로 이동
			request.setAttribute("loginFailMessage", "로그인 시도 횟수 초과!! 10분 뒤 다시 시도해주세요");
			RequestDispatcher dispatcher = request.getRequestDispatcher(LOGIN_FORM_URI);
			dispatcher.forward(request, response);
		}
	}
}
