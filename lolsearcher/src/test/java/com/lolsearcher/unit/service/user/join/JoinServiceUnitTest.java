package com.lolsearcher.unit.service.user.join;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.exception.exception.join.ExistedUserException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.RequestUserJoinDto;
import com.lolsearcher.model.response.front.user.ResponseJoinDto;
import com.lolsearcher.repository.user.UserRepository;
import com.lolsearcher.service.mail.MailService;
import com.lolsearcher.service.user.join.JoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.lolsearcher.constant.LolSearcherConstants.JWT_PREFIX;
import static com.lolsearcher.constant.LolSearcherConstants.USER_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JoinServiceUnitTest {

	String SECRET;
	JoinService joinService;
	ObjectMapper objectMapper;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock UserRepository userRepository;
	@Mock MailService mailService;
	@Mock ExecutorService executorService;
	
	@BeforeEach
	void upset() {
		SECRET = "secret";
		objectMapper = new ObjectMapper();
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
		joinService = new JoinService(objectMapper, bCryptPasswordEncoder, userRepository, mailService, executorService);
	}

	@DisplayName("Email이 중복되는 경우 예외가 발생한다.")
	@Test
	public void handleJoinProcedureWithExistedEmail(){

		//given
		RequestUserJoinDto request = JoinServiceTestSetup.getRequestUserJoinDto();
		String email = request.getEmail();
		LolSearcherUser existedUser = JoinServiceTestSetup.getLolSearcherUser(email);

		given(userRepository.findUserByEmail(email)).willReturn(existedUser);

		//when & then
		assertThrows(ExistedUserException.class, ()->joinService.handleJoinProcedure(request));
	}

	@DisplayName("정상적인 회원가입 요청시 본인 인증용 임시 토큰을 발급발급하고 리턴한다.")
	@Test
	public void handleJoinProcedureWithSuccess() throws JsonProcessingException {

		//given
		RequestUserJoinDto request = JoinServiceTestSetup.getRequestUserJoinDto();
		String email = request.getEmail();

		given(userRepository.findUserByEmail(email)).willReturn(null);

		//when
		ResponseEntity<ResponseJoinDto> result = joinService.handleJoinProcedure(request);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<String> authHeader = result.getHeaders().get(HttpHeaders.AUTHORIZATION);

		assertThat(authHeader).isNotNull();
		assertThat(authHeader.size()).isEqualTo(1);

		String token = authHeader.get(0);
		assertThat(token).contains(JWT_PREFIX);
		token = token.replaceAll(JWT_PREFIX, "");
		String userInfo = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token).getClaim(USER_INFO).asString();
		LolSearcherUser user = objectMapper.readValue(userInfo, LolSearcherUser.class);

		assertThat(user.getEmail()).isEqualTo(request.getEmail());
		assertThat(user.getUsername()).isEqualTo(request.getUsername());
		assertThat(bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
	}
}
