package com.lolsearcher.filter.parameter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

@WebFilter(urlPatterns = "/champions/detail")
public class ChampionValidationFilter implements Filter {

	private Set<String> champions;
	
	@Value("${filter.champion.url}")
	private String championsDirUrl;
	
	@Value("${filter.champion.param}")
	private String param;
	
	@Value("${filter.invalid_handler_uri}")
	private String invalid_handler_uri;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		champions = new HashSet<String>();
		
		//챔피언 리스트 가져옴
		File championDir = new File(championsDirUrl);
		
		for(String championFileName : championDir.list()) {
			String champion = championFileName.substring(0, championFileName.length()-4);
			champions.add(champion);
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String unfiltered_name = request.getParameter(param);
		
		if(champions.contains(unfiltered_name)) {
			chain.doFilter(request, response);
		}else {
			((HttpServletResponse)response).sendRedirect(invalid_handler_uri);
		}
	}

}
