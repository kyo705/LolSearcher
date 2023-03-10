package com.lolsearcher.service.user.identification;

import com.lolsearcher.constant.enumeration.NotificationDevice;
import com.lolsearcher.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public abstract class AbstractIdentificationService {

    private final NotificationService notificationService;

    public Object createAndSendCertification(Object userInfo, String email) {

        //인증번호 생성
        int certificationNumber = generateCertificationNumber();

        //알림 메시지 생성 및 전송
        notificationService.sendIdentificationMessage(NotificationDevice.E_MAIL, email, certificationNumber);

        // 유저 데이터 임시 저장
        return saveIdentificationTemporarily(userInfo, certificationNumber);
    }

    public int generateCertificationNumber() {
        return (int)(Math.random()*10000000); /* 8자리 랜덤수 */
    }

    protected abstract Object saveIdentificationTemporarily(Object userInfo, int certificationNumber);

    public abstract Authentication authenticate(Authentication authentication);

    protected NotificationService getNotificationService(){
        return notificationService;
    }
}
