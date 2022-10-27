package com.lolsearcher.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

public class OauthUserService extends DefaultOAuth2UserService {
	
	
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
		Map<String, Object> attributes = oAuth2User.getAttributes();
		System.out.println(oAuth2User);
		String username = (String)oAuth2User.getAttributes().get("sub");
		String password = bCryptPasswordEncoder.encode(username+System.currentTimeMillis());
		String role = "ROLE_GET";
		String email = (String) oAuth2User.getAttributes().get("email");
		
		LolSearcherUser user = userRepository.findUserByName(username);
		
		if(user==null) {
			user = userRepository.findUserByEmail(email);
			if(user==null) {
				user = new LolSearcherUser(username, password, role, email, 0);
				userRepository.saveUser(user);
			}
		}
		
		user.setLastLoginTimeStamp(System.currentTimeMillis());
		LolsearcherUserDetails lolseracherUserDetails = new LolsearcherUserDetails(user, attributes);
		
		return lolseracherUserDetails;
	}
}
