package com.lolsearcher.service.user.login;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.response.front.user.LolsearcherUserDetails;
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

	@JpaTransactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		LolSearcherUser user = userRepository.findUserByEmail(username);

		if(user==null) return null;

		return new LolsearcherUserDetails(user);
	}
}
