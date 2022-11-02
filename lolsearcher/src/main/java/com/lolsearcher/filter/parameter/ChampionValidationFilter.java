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

	private Set<String> champions;
	private String championFolderUri = "src/main/resources/static/champion";
	private String param = "champion";
	private String failHandlerUri = "/invalid";
	
	public ChampionValidationFilter() {
		champions = new HashSet<String>();
		File championDir = new File(championFolderUri);
		
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
			((HttpServletResponse)response).sendRedirect(failHandlerUri);
		}
	}

}
