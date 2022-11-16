package com.lolsearcher.filter.parameter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ChampionValidationFilter implements Filter {
	private final String CHAMPIONS_NAME_URI = "src/main/resources/static/champion";
	private final String CHAMPION = "champion";
	private final String FAIL_HANDLER_URI = "/invalid";
	
	private Set<String> champions;
	
	public ChampionValidationFilter() {
		champions = new HashSet<String>();
		File championDir = new File(CHAMPIONS_NAME_URI);
		
		for(String championFileName : championDir.list()) {
			String champion = championFileName.substring(0, championFileName.length()-4);
			champions.add(champion);
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String championName = request.getParameter(CHAMPION);
		
		if(champions.contains(championName)) {
			chain.doFilter(request, response);
		}else {
			((HttpServletResponse)response).sendRedirect(FAIL_HANDLER_URI);
		}
	}

}
