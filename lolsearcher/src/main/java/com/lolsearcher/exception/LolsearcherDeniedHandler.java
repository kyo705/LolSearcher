package com.lolsearcher.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class LolsearcherDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		System.out.println(request.getServletPath());
		String url = request.getServletPath();
		
		if(url.subSequence(1, 4).equals("api")) {
			request.getRequestDispatcher("/api/error/forbidden").forward(request, response);
		}
		//다른 경로일 때 json 형태로 403 error 발생시키지 않고 view 페이지 전달하도록 로직 짤 수 있음
		
	}

}
