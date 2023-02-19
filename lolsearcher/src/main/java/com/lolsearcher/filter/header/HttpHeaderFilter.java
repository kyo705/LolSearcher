package com.lolsearcher.filter.header;

import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpHeaderFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("charset=UTF-8");

		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=300");
		
		chain.doFilter(request, response);
	}

}
