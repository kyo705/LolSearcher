package com.lolsearcher.filter.parameter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

@WebFilter(urlPatterns = {"/summoner","/ingame"})
public class SummonerNameValidationFilter implements Filter {

	@Value("${filter.summoner_name.param}")
	private String param;
	
	@Value("${filter.invalid_handler_uri}")
	private String invalid_handler_uri;
	
	@Value("${filter.max_param_length}")
	private int max_param_length;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String unfiltered_name = request.getParameter(param);
		
		if(unfiltered_name.length()>max_param_length) {
			((HttpServletResponse)response).sendRedirect(invalid_handler_uri);
		}else {
			if(unfiltered_name!=null&&!unfiltered_name.equals("")) {
				String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
				String filtered_name = unfiltered_name.replaceAll(regex, "");
				
				request.setAttribute(param, filtered_name);
				chain.doFilter(request, response);
			}else {
				((HttpServletResponse)response).sendRedirect(invalid_handler_uri);
			}
		}
	}

}
