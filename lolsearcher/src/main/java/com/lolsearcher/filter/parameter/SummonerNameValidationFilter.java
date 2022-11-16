package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class SummonerNameValidationFilter implements Filter {
	private final int MAX_NAME_LENGTH = 50;
	private final String NAME = "name";
	private final String FAIL_HANDLER_URI = "/invalid";
	private final String REGEX = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String unfiltered_name = request.getParameter(NAME);
		
		if(unfiltered_name==null || unfiltered_name.equals("")) {
			((HttpServletResponse)response).sendRedirect(FAIL_HANDLER_URI);
		}else if(unfiltered_name.length() > MAX_NAME_LENGTH) {
			((HttpServletResponse)response).sendRedirect(FAIL_HANDLER_URI);
		}else {
			String filtered_name = unfiltered_name.replaceAll(REGEX, "");
			
			request.setAttribute(NAME, filtered_name);
			chain.doFilter(request, response);
		}
	}
}
