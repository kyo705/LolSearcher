package com.lolsearcher.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import com.lolsearcher.service.BanService;

@WebFilter(urlPatterns = "/*")
public class LolsearcherFilter implements Filter{

	private final ApplicationContext ac;
	
	LolsearcherFilter(ApplicationContext ac){
		this.ac = ac;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//실행 전 필터 수행 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("charset=UTF-8");
		//System.out.println(((HttpServletRequest)request));
		
		/*BanService banService = ac.getBean(BanService.class);
		String ip = request.getRemoteAddr();
		
		if(banService.findId(ip)) {
			((HttpServletResponse)response).sendRedirect("/banned");
		}else {
			chain.doFilter(request, response);
		}*/
		
		chain.doFilter(request, response);
		
		//실행 후 필터 수행
		
	}

}
