package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class SummonerNameValidationFilter implements Filter {
	private String param = "name";
	private String failHandlerUri = "/invalid";
	private int maxParamLength = 50;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String unfiltered_name = request.getParameter(param);
		
		if(unfiltered_name.length()>maxParamLength) {
			((HttpServletResponse)response).sendRedirect(failHandlerUri);
		}else {
			if(unfiltered_name!=null&&!unfiltered_name.equals("")) {
				String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
				String filtered_name = unfiltered_name.replaceAll(regex, "");
				
				request.setAttribute(param, filtered_name);
				chain.doFilter(request, response);
			}else {
				((HttpServletResponse)response).sendRedirect(failHandlerUri);
			}
		}
	}

}
