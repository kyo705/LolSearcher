package com.lolsearcher.login;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.lolsearcher.user.LoginSecurityState.IDENTIFICATION;
import static com.lolsearcher.user.Role.TEMPORARY;
import static com.lolsearcher.utils.PasswordEncoderUtils.match;

@RequiredArgsConstructor
@Service
public class LolSearcherAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userDetailService;


	@JpaTransactional
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String inputUserId = authentication.getName();
		String inputUserPwd = (String)authentication.getCredentials();
		
		LolsearcherUserDetails userDetails = (LolsearcherUserDetails) userDetailService.loadUserByUsername(inputUserId);

		if(userDetails==null || ! match(inputUserPwd, userDetails.getPassword())) {
			throw new BadCredentialsException(inputUserId);
		}
		else if(!userDetails.isAccountNonExpired()) {
			throw new AccountExpiredException(inputUserId);
		}
		else if(!userDetails.isAccountNonLocked()) {
			throw new LockedException(inputUserId);
		}
		else if(!userDetails.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException(inputUserId);
		}
		else if(!userDetails.isEnabled()) {
			throw new DisabledException(inputUserId);
		}

		// 유저 보안 레벨이 2이상일 경우 인증 절차가 진행되도록 권한을 Temporary로 변경
		if(userDetails.getLoginSecurity().getLevel() >= IDENTIFICATION.getLevel()){
			Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) userDetails.getAuthorities();
			authorities.clear();
			authorities.add(TEMPORARY::getValue);
		}
		userDetails.setPassword(null);
		
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
