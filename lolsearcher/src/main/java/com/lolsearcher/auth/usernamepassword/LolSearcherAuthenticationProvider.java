package com.lolsearcher.auth.usernamepassword;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LolSearcherAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailService;
	private BCryptPasswordEncoder pwdEncoding;
	
	public LolSearcherAuthenticationProvider(UserDetailsService userDetailService, BCryptPasswordEncoder pwdEncoding) {
		this.userDetailService = userDetailService;
		this.pwdEncoding = pwdEncoding;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String inputUserId = authentication.getName();
		String inputUserPwd = (String)authentication.getCredentials();
		
		LolsearcherUserDetails userDetails = (LolsearcherUserDetails)userDetailService.loadUserByUsername(inputUserId);
		
		if(userDetails==null||!pwdEncoding.matches(inputUserPwd, userDetails.getPassword())) {
			throw new BadCredentialsException(inputUserId);
		}else if(!userDetails.isAccountNonExpired()) {
			throw new AccountExpiredException(inputUserId);
		}else if(!userDetails.isAccountNonLocked()) {
			throw new LockedException(inputUserId);
		}else if(!userDetails.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException(inputUserId);
		}else if(!userDetails.isEnabled()) {
			throw new DisabledException(inputUserId);
		}
		
		userDetails.setPassword(null);
		
		Authentication newAuth = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
		
		return newAuth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
