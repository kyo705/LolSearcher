package com.lolsearcher.service.user.login;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
import com.lolsearcher.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthUserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	@JpaTransactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		LolSearcherUser user = findLolSearcherAccount(oAuth2User);
		log.info("로그인된 lolsearcher user : '{}'", user);
		
		Map<String, Object> attributes = oAuth2User.getAttributes();

		return new LolsearcherUserDetails(user, attributes);
	}
	
	private LolSearcherUser findLolSearcherAccount(OAuth2User oAuth2User) {
		String username = (String)oAuth2User.getAttributes().get("sub");
		String email = (String) oAuth2User.getAttributes().get("email");
		
		LolSearcherUser user = userRepository.findUserByName(username);
		if(user!=null) {
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			return user;
		}
		
		user = userRepository.findUserByEmail(email);
		if(user!=null) {
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			return user;
		}
		
		user = createLolSearcherAccount(oAuth2User);
		user.setLastLoginTimeStamp(System.currentTimeMillis());
		return user;
	}
	
	
	private LolSearcherUser createLolSearcherAccount(OAuth2User oAuth2User) {
		String username = (String)oAuth2User.getAttributes().get("sub");
		String password = bCryptPasswordEncoder.encode(username+System.currentTimeMillis());
		String role = "ROLE_GET";
		String email = (String) oAuth2User.getAttributes().get("email");
		
		return new LolSearcherUser(username, password, role, email, 0);
	}
}
