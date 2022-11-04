package com.lolsearcher.service.join;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.entity.user.LolSearcherUser;
import com.lolsearcher.exception.join.CertificationTimeOutException;
import com.lolsearcher.exception.join.RandomNumDifferenceException;
import com.lolsearcher.repository.userrepository.UserRepository;
import com.lolsearcher.scheduler.dto.Timer;
import com.lolsearcher.scheduler.job.RemovingCertificationEmailJob;
import com.lolsearcher.scheduler.service.SchedulerService;

@Service
public class JoinService {
	private final Map<String, LolSearcherUser> userContainer = new ConcurrentHashMap<>();
	private final Map<String, Integer> randomNumContanier = new ConcurrentHashMap<>();
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	private final JavaMailSender javaMailSender;
	private final ExecutorService executorService;
	private final SchedulerService schedulerService;
	
	public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, 
						JavaMailSender mailSender, ExecutorService executorService,
						SchedulerService schedulerService) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.javaMailSender = mailSender;
		this.executorService =executorService;
		this.schedulerService = schedulerService;
	}
	
	@Transactional
	public void joinUser(LolSearcherUser user) {
		userRepository.saveUser(user);
	}
	
	
	@Transactional(readOnly = true)
	public LolSearcherUser findUserByUsername(String username) {
		return userRepository.findUserByName(username);
	}
	
	
	@Transactional(readOnly = true)
	public LolSearcherUser findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	
	public boolean isExistedId(String username) {
		LolSearcherUser user = findUserByUsername(username);
		if(user==null)
			return false;
		else
			return true;
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

	
	public void sendCertificationToEmail(String username, String rawPassword, String email) {
		String defaultRole = "ROLE_GET";
		String password = bCryptPasswordEncoder.encode(rawPassword);
		
		LolSearcherUser user = new LolSearcherUser(username, password, defaultRole, email, 0);
		int randomNumber = (int)(Math.random()*10000000);
		
		userContainer.put(email, user);
		randomNumContanier.put(email, randomNumber);
		
		Runnable certificationEmailTask = createCertificationEmail(email, randomNumber);
		executorService.submit(certificationEmailTask);
		
		Timer timer = new Timer();
		timer.setInitialOffsetMs(1000*60*5); //5분
		timer.setTotalFireCount(1);
		timer.setCallbackData(email);
		
		schedulerService.schedule(RemovingCertificationEmailJob.class, timer);
	}

	
	public LolSearcherUser certificate(String email, int inputNumber) {
		if(!userContainer.containsKey(email)) {
			throw new CertificationTimeOutException();
		}
		int randomNumber = randomNumContanier.get(email);
		if(randomNumber != inputNumber) {
			throw new RandomNumDifferenceException(email);
		}
		
		LolSearcherUser user = userContainer.get(email);
		userContainer.remove(email);
		randomNumContanier.remove(email);
		
		return user;
	}
	
	
	public void removeRandomNumber(String email) {
		randomNumContanier.remove(email);
	}
	
	
	public void removeUncertificatedUser(String email) {
		userContainer.remove(email);
	}
	
	
	private Runnable createCertificationEmail(String email, int randomNumber) {
		Runnable certificationEmailTask = new Runnable() {
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
		
		return certificationEmailTask;
	}
}
