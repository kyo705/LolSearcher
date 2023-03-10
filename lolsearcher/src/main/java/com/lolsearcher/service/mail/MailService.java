package com.lolsearcher.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendIdentificationMail(String email, int randomNumber) {

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

    public void sendLoginMessage(String email, String sessionId, String ipAddress) {

    }
}
