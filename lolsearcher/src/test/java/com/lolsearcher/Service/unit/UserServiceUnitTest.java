package com.lolsearcher.Service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lolsearcher.repository.userrepository.UserRepository;
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
