package com.lolsearcher.auth.usernamepassword;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.exception.exception.summoner.SameValueExistException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LolsearcherUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@JpaTransactional
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
