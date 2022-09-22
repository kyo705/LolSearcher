package com.lolsearcher.Service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lolsearcher.repository.userrepository.UserRepository;
import com.lolsearcher.service.UserService;

public class UserServiceUnitTest {

	UserService userService;
	@Mock
	UserRepository userRepository;
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@BeforeEach
	void upset() {
		//userService = new UserService(userRepository, bCryptPasswordEncoder);
	}
	
	
}
