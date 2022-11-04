package com.lolsearcher.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.auth.usernamepassword.LolsearcherUserDetails;
import com.lolsearcher.domain.entity.user.LolSearcherUser;
import com.lolsearcher.repository.userrepository.UserRepository;

@Service
public class OauthUserService extends DefaultOAuth2UserService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private UserRepository userRepository;

	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public OauthUserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		LolSearcherUser user = findLolSearcherAccount(oAuth2User);
		logger.info("로그인된 lolsearcher user : '{}'", user);
		
		Map<String, Object> attributes = oAuth2User.getAttributes();
		LolsearcherUserDetails lolseracherUserDetails = new LolsearcherUserDetails(user, attributes);
		
		return lolseracherUserDetails;
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
