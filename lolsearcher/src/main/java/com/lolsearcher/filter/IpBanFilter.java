package com.lolsearcher.filter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@WebFilter(urlPatterns = "/*")
@Component
public class IpBanFilter implements Filter {

	private Map<String, Long> banList;
	private static final long banDuration = 24*60*60*1000l;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		banList = new ConcurrentHashMap<>();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String request_ip = request.getRemoteAddr();
		
		String uri = ((HttpServletRequest)request).getRequestURI();
		
		if(uri.equals("/rejected")) {
			chain.doFilter(request, response);
		}else {
			if(!banList.containsKey(request_ip)) {
				chain.doFilter(request, response);
			}else {
				long ban_time = banList.get(request_ip);
				if(System.currentTimeMillis() - ban_time >= banDuration) {
					banList.remove(request_ip);
					chain.doFilter(request, response);
				}else {
					//벤 페이지로 이동
					((HttpServletResponse)response).sendRedirect("/rejected");
				}
			}
		}
	}
	
	public void addBanList(String ip) {
		this.banList.put(ip, System.currentTimeMillis());
	}

}
