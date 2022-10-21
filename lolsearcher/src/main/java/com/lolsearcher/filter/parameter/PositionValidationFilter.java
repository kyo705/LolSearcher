package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/champions")
public class PositionValidationFilter implements Filter {

	private final String top = "TOP";
	private final String jungle = "JUNGLE";
	private final String middle = "MIDDLE";
	private final String bottom = "BOTTOM";
	private final String utility = "UTILITY";
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String param = "position";
		
		String unfiltered_name = request.getParameter(param);
		if(unfiltered_name==null) {
			chain.doFilter(request, response);
		}else if(unfiltered_name.equals(top)||
			unfiltered_name.equals(jungle)||
			unfiltered_name.equals(middle)||
			unfiltered_name.equals(bottom)||
			unfiltered_name.equals(utility)) {
			chain.doFilter(request, response);
		}else {
			String url = "/invalid";
			((HttpServletResponse)response).sendRedirect(url);
		}
	}

}
