package com.lolsearcher.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IpBanFilter implements Filter {

	private final String filteredUri = "/rejected";
	private final Map<String, Long> banList = new ConcurrentHashMap<>();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String request_ip = request.getRemoteAddr();
		
		String uri = ((HttpServletRequest)request).getRequestURI();
		
		if(uri.equals(filteredUri) || !banList.containsKey(request_ip)) {
			chain.doFilter(request, response);
		}else {
			//벤 페이지로 이동
			((HttpServletResponse)response).sendRedirect("/rejected");
		}
	}
	
	public void addBanList(String ip) {
		this.banList.put(ip, System.currentTimeMillis());
	}

	public void removeBanList(String ip) {
		this.banList.remove(ip);
	}

}
