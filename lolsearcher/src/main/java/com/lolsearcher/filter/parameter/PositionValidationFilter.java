package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class PositionValidationFilter implements Filter {
	private final String TOP = "TOP";
	private final String JUNGLE = "JUNGLE";
	private final String MIDDLE = "MIDDLE";
	private final String BOTTOM = "BOTTOM";
	private final String UNTILITY = "UTILITY";

	private final String POSITION = "position";
	private final String FAIL_HANDLER_URI = "/invalid";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String unfiltered_name = request.getParameter(POSITION);
		if(unfiltered_name==null) {
			chain.doFilter(request, response);
		}else if(unfiltered_name.equals(TOP)||
			unfiltered_name.equals(JUNGLE)||
			unfiltered_name.equals(MIDDLE)||
			unfiltered_name.equals(BOTTOM)||
			unfiltered_name.equals(UNTILITY)) {
			chain.doFilter(request, response);
		}else {
			((HttpServletResponse)response).sendRedirect(FAIL_HANDLER_URI);
		}
	}

}
