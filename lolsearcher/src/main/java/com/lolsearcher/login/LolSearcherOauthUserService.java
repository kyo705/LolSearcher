package com.lolsearcher.login;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.user.User;
import com.lolsearcher.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LolSearcherOauthUserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;
	
	@Override
	@JpaTransactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		User user = findLolSearcherAccount(oAuth2User);
		log.info("로그인된 lolsearcher user : '{}'", user);
		
		Map<String, Object> attributes = oAuth2User.getAttributes();

		return new LolsearcherUserDetails(user, attributes);
	}
	
	private User findLolSearcherAccount(OAuth2User oAuth2User) {
		String username = (String)oAuth2User.getAttributes().get("sub");
		String email = (String) oAuth2User.getAttributes().get("email");
		
/*		User user = userRepository.findUserByName(username);
		if(user!=null) {
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			return user;
		}
		
		user = userRepository.findByEmail(email);
		if(user!=null) {
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			return user;
		}
		
		user = createLolSearcherAccount(oAuth2User);
		user.setLastLoginTimeStamp(System.currentTimeMillis());
		return user;*/
		return null;
	}
	
	
/*	private User createLolSearcherAccount(OAuth2User oAuth2User) {
		String username = (String)oAuth2User.getAttributes().get("sub");
		String password = bCryptPasswordEncoder.encode(username+System.currentTimeMillis());
		String role = "ROLE_GET";
		String email = (String) oAuth2User.getAttributes().get("email");
		
		return new User(0L, username, password, role, email, 0, LoginSecurityState.NONE.getLevel());
	}*/
}
