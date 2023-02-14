package com.lolsearcher.auth.usernamepassword;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LolSearcherAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailService;
	private final BCryptPasswordEncoder pwdEncoding;

	
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
		
		return new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
