package com.lolsearcher.auth.exceptiontranslationfilter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class LolsearcherDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		String url = request.getServletPath();
		
		if(url.subSequence(1, 4).equals("api")) {
			request.getRequestDispatcher("/api/error/forbidden").forward(request, response);
		}
	}
}
