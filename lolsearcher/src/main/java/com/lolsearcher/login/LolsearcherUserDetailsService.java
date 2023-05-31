package com.lolsearcher.login;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.user.UserRepository;
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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		return userRepository.findByEmail(email)
				.map(LolsearcherUserDetails::new)
				.orElse(null);
	}
}
