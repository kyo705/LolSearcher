package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/summoner","/ingame"})
public class SummonerNameValidationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String param = "name";
		
		String unfiltered_name = request.getParameter(param);
		
		if(unfiltered_name!=null&&!unfiltered_name.equals("")) {
			
			String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
			String filtered_name = unfiltered_name.replaceAll(regex, "");
			
			request.setAttribute(param, filtered_name);
			chain.doFilter(request, response);
		}else {
			String url = "/invalid";
			((HttpServletResponse)response).sendRedirect(url);
		}
	}

}
