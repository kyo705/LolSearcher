package com.lolsearcher.auth.usernamepassword;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.exception.summoner.SameValueExistException;
import com.lolsearcher.repository.user.UserRepository;

@Service
public class LolsearcherUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	
	public LolsearcherUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			LolSearcherUser user = userRepository.findUserByName(username);
			if(user==null) {
				return null;
			}
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			
			return new LolsearcherUserDetails(user);
		}catch(SameValueExistException e) {
			throw e;
		}
	}

}
