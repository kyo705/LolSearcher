package com.lolsearcher.service.user.join;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.exception.exception.join.CertificationTimeOutException;
import com.lolsearcher.exception.exception.join.RandomNumDifferenceException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.response.temporary.TemporaryUser;
import com.lolsearcher.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;

import static com.lolsearcher.constant.RedisCacheConstants.JOIN_CERTIFICATION_KEY;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Service
public class JoinService {

	private final CacheManager cacheManager;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	private final JavaMailSender javaMailSender;
	private final ExecutorService executorService;
	
	@JpaTransactional
	public void joinUser(LolSearcherUser user) {
		userRepository.saveUser(user);
	}
	
	
	@JpaTransactional(readOnly = true)
	public LolSearcherUser findUserByUsername(String username) {
		return userRepository.findUserByName(username);
	}
	
	
	@JpaTransactional(readOnly = true)
	public LolSearcherUser findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	
	public boolean isValidForm(String userid) {
		for(char c : userid.toCharArray()) {
			if((c>='a'&& c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')) {
				continue;
			}
			return false;
		}
		return true;
	}

	
	public void sendCertificationEmail(String username, String rawPassword, String email) {
		LolSearcherUser user = createAccount(username, rawPassword, email);
		int randomNumber = (int)(Math.random()*10000000);
		
		Runnable certificationEmailTask = createCertificationEmail(email, randomNumber);
		executorService.submit(certificationEmailTask);

		TemporaryUser temporaryUser = new TemporaryUser(user, randomNumber);

		requireNonNull(cacheManager.getCache(JOIN_CERTIFICATION_KEY)).put(email, temporaryUser);
	}

	
	public LolSearcherUser certificate(String email, int inputNumber) {

		TemporaryUser temporaryUser = (TemporaryUser) requireNonNull(cacheManager.getCache(JOIN_CERTIFICATION_KEY)).get(email);

		if(temporaryUser == null) {
			throw new CertificationTimeOutException();
		}

		if(inputNumber != temporaryUser.getRandomNumber()) {
			throw new RandomNumDifferenceException(email);
		}

		requireNonNull(cacheManager.getCache(JOIN_CERTIFICATION_KEY)).evict(email);

		return temporaryUser.getUser();
	}
	
	
	private LolSearcherUser createAccount(String username, String rawPassword, String email) {
		String defaultRole = "ROLE_GET";
		String password = bCryptPasswordEncoder.encode(rawPassword);
		
		return new LolSearcherUser(username, password, defaultRole, email, 0);
	}
	
	
	private Runnable createCertificationEmail(String email, int randomNumber) {
		return () -> {
				String subject = "lolsearcher 회원가입 인증 메일입니다.";
				String text = "<p>안녕하세요.</p>"
							+ "<p>lolsearcher 회원가입 인증 메일입니다.</p>"
							+ "<p>인증 코드 번호는 아래와 같습니다.</p>"
							+ "<h2>"+randomNumber+"</h2>"
							+ "<p>해당 코드 번호로 인증을 완료해주세요.</p>";
				
				MimeMessage message = javaMailSender.createMimeMessage();
				try {
					message.setSubject(subject);
					message.setText(text,"UTF-8","html");
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
					
					javaMailSender.send(message);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			};
	}
}
