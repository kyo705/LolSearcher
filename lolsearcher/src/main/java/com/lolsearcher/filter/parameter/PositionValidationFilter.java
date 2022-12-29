package com.lolsearcher.filter.parameter;

import com.lolsearcher.constant.PositionConstants;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class PositionValidationFilter implements Filter {

	private final String POSITION = "position";
	private final String FAIL_HANDLER_URI = "/invalid";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String unfiltered_name = request.getParameter(POSITION);
		if(unfiltered_name==null) {
			chain.doFilter(request, response);

		}else if(
				unfiltered_name.equals(PositionConstants.TOP)||
				unfiltered_name.equals(PositionConstants.JUNGLE)||
				unfiltered_name.equals(PositionConstants.MIDDLE)||
				unfiltered_name.equals(PositionConstants.BOTTOM)||
				unfiltered_name.equals(PositionConstants.UTILITY)
		) {
			chain.doFilter(request, response);
		}else {
			((HttpServletResponse)response).sendRedirect(FAIL_HANDLER_URI);
		}
	}

}
