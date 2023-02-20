package com.lolsearcher.service.user.join;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.exception.exception.join.ExistedUserException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.RequestEmailCheckDto;
import com.lolsearcher.model.request.user.RequestUserJoinDto;
import com.lolsearcher.model.response.front.user.ResponseJoinDto;
import com.lolsearcher.repository.user.UserRepository;
import com.lolsearcher.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutorService;

import static com.lolsearcher.constant.LolSearcherConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class JoinService {

	@Value("${lolsearcher.jwt.secret}")
	private String JWT_SECRET_KEY;

	private final ObjectMapper objectMapper;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final ExecutorService executorService;

	@JpaTransactional(readOnly = true)
	public ResponseEntity<ResponseJoinDto> handleJoinProcedure(RequestUserJoinDto requestDto) {

		String email = requestDto.getEmail();
		validateEmail(email);

		int randomNum = generateRandomNum();
		LolSearcherUser user = generateLolSearcherUser(requestDto);

		sendIdentificationEmail(email, randomNum);
		String token = generateToken(user, randomNum);

		return generateResponseEntity(token);
	}

	@JpaTransactional(readOnly = true)
	public ResponseJoinDto checkPossibleEmail(RequestEmailCheckDto request) {

		String email = request.getEmail();
		validateEmail(email);

		return ResponseJoinDto.builder().message("사용 가능한 이메일입니다.").build();
	}

	@JpaTransactional
	public void joinUser(LolSearcherUser user) {
		userRepository.saveUser(user);
	}


	private void validateEmail(String email){

		if(userRepository.findUserByEmail(email) != null){
			log.info("{} 해당 이메일은 이미 존재하는 메일입니다.", email);
			throw new ExistedUserException(email); /* exception handler에서 400 error 발생 */
		}
	}

	private int generateRandomNum() {

		return (int)(Math.random()*10000000); /* 8자리 랜덤수 */
	}

	private LolSearcherUser generateLolSearcherUser(RequestUserJoinDto requestDto) {

		String email = requestDto.getEmail();
		String password = bCryptPasswordEncoder.encode(requestDto.getPassword());
		String username = requestDto.getUsername();

		return LolSearcherUser.builder()
				.email(email)
				.password(password)
				.username(username)
				.role(DEFAULT_ROLE)
				.build();
	}

	private void sendIdentificationEmail(String email, int randomNumber) {

		executorService.submit(()->mailService.sendIdentificationMail(email, randomNumber));
	}

	private String generateToken(LolSearcherUser user, int randomNum) {

		try {
			String userJson = objectMapper.writeValueAsString(user);

			return JWT.create()
					.withSubject(JOIN_IDENTIFICATION_SIGNATURE)
					.withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRED_TIME))
					.withClaim(USER_INFO, userJson)
					.withClaim(RANDOM_NUMBER, randomNum)
					.sign(Algorithm.HMAC256(JWT_SECRET_KEY));

		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e); //500에러 발생
		}
	}

	private ResponseEntity<ResponseJoinDto> generateResponseEntity(String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);

		ResponseJoinDto body = ResponseJoinDto.builder().message("임시 회원 가입 성공").build();

		return ResponseEntity.status(HttpStatus.OK)
				.headers(headers)
				.body(body);
	}
}
