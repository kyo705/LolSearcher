package com.lolsearcher.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.auth.LolsearcherUserDetails;
import com.lolsearcher.domain.entity.user.LolSearcherUser;
import com.lolsearcher.exception.join.CertificationTimeOutException;
import com.lolsearcher.exception.join.RandomNumDifferenceException;
import com.lolsearcher.repository.userrepository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final Map<String, LolSearcherUser> userContainer;
	private final Map<String, Integer> randomNumContanier;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	private final JavaMailSender javaMailSender;
	private final ExecutorService executorService;
	
	public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, 
			JavaMailSender mailSender, ExecutorService executorService) {
		this.randomNumContanier = new ConcurrentHashMap<>();
		this.userContainer = new ConcurrentHashMap<>();
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.javaMailSender = mailSender;
		this.executorService =executorService;
	}
	
	@Transactional
	public void joinUser(LolSearcherUser user) {
		String rawPassword = user.getPassword();
		String secretPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(secretPassword);
		
		userRepository.saveUser(user);
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			LolSearcherUser user = userRepository.findUserByName(username);
			
			if(user==null)
				return null;
			
			user.setLastLoginTimeStamp(System.currentTimeMillis());
			return new LolsearcherUserDetails(user);
		}catch(Exception e2) {
			throw e2;
		}
	}

	@Transactional(readOnly = true)
	public boolean isExistedId(String username) {
		
		LolSearcherUser user = userRepository.findUserByName(username);
		
		if(user==null)
			return false;
		else
			return true;
	}
	
	@Transactional(readOnly = true)
	public LolSearcherUser findUserByEmail(String email) {
		
		return userRepository.findUserByEmail(email);
	}

	public boolean isPossibleForm(String userid) {
		for(char c : userid.toCharArray()) {
			if((c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')) {
				continue;
			}else {
				return false;
			}
		}
		
		return true;
	}

	public void sendCertificationToEmail(String username, String password, String email) {
		String default_role = "ROLE_GET";
		LolSearcherUser user = new LolSearcherUser(username, password, default_role, email, 0);
		int randomNumber = (int)(Math.random()*10000000);
		
		userContainer.put(email, user);
		randomNumContanier.put(email, randomNumber);
		
		//email로 number 값을 보내줌
		Runnable sendEmail = new Runnable() {
			public void run() {
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
			}
		};
		
		executorService.submit(sendEmail);
	}

	public LolSearcherUser certificate(String email, int number) {
		if(!userContainer.containsKey(email)) {
			throw new CertificationTimeOutException();
		}
		
		LolSearcherUser user = userContainer.get(email);
		int num = randomNumContanier.get(email);
		
		if(num != number) {
			throw new RandomNumDifferenceException(email);
		}
		
		userContainer.remove(email);
		randomNumContanier.remove(email);
		return user;
	}
	
	

}
