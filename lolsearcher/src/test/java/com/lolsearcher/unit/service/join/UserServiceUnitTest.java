package com.lolsearcher.unit.service.join;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lolsearcher.repository.user.UserRepository;
import com.lolsearcher.service.join.JoinService;

public class UserServiceUnitTest {

	JoinService userService;
	@Mock
	UserRepository userRepository;
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@BeforeEach
	void upset() {
		//userService = new UserService(userRepository, bCryptPasswordEncoder);
	}
	
	
}
