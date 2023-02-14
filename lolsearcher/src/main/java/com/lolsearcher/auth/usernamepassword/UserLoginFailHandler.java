package com.lolsearcher.auth.usernamepassword;

import com.lolsearcher.service.ban.LoginIpBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class UserLoginFailHandler implements AuthenticationFailureHandler {

	private final LoginIpBanService loginIpBanService;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String ipAddress = request.getRemoteAddr();

		if(loginIpBanService.isExceedBanCount(ipAddress)) {
			loginIpBanService.registerBanList(ipAddress);
			
			request.setAttribute("loginFailMessage", "로그인 시도 횟수 초과!! 10분 뒤 다시 시도해주세요");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/loginForm");
			dispatcher.forward(request, response);
			return;
		}
		
		if (exception instanceof AuthenticationServiceException) {
			request.setAttribute("loginFailMessage", "존재하지 않는 사용자입니다.");
		
		} else if(exception instanceof BadCredentialsException) {
			request.setAttribute("loginFailMessage", "아이디 또는 비밀번호가 틀립니다.");
			
		} else if(exception instanceof LockedException) {
			request.setAttribute("loginFailMessage", "잠긴 계정입니다..");
			
		} else if(exception instanceof DisabledException) {
			request.setAttribute("loginFailMessage", "비활성화된 계정입니다..");
			
		} else if(exception instanceof AccountExpiredException) {
			request.setAttribute("loginFailMessage", "만료된 계정입니다..");
			
		} else if(exception instanceof CredentialsExpiredException) {
			request.setAttribute("loginFailMessage", "비밀번호가 만료되었습니다.");
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/loginForm");
		dispatcher.forward(request, response);
	}

}
