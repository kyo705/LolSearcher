package com.lolsearcher.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class LoginBanFilter implements Filter {

	private String filteredUri = "/login";
	private Map<String, Long> banList;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		banList = new ConcurrentHashMap<>();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String request_ip = request.getRemoteAddr();
		String uri = ((HttpServletRequest)request).getRequestURI();
		if(!banList.containsKey(request_ip)||!uri.equals(filteredUri)) {
			chain.doFilter(request, response);
		}else {
			//벤 페이지로 이동
			request.setAttribute("loginFailMessage", "로그인 시도 횟수 초과!! 10분 뒤 다시 시도해주세요");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/loginForm");
			dispatcher.forward(request, response);
		}
	}

	public void addBanList(String user_ip) {
		banList.put(user_ip, System.currentTimeMillis());
	}

	public void removeBanList(String user_ip) {
		banList.remove(user_ip);
	}
}
