package com.lolsearcher.auth.exceptiontranslationfilter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
