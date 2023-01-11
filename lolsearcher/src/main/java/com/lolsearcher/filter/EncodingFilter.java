package com.lolsearcher.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//실행 전 필터 수행 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("charset=UTF-8");
		
		
		chain.doFilter(request, response);
		
		//실행 후 필터 수행
		
	}

}
