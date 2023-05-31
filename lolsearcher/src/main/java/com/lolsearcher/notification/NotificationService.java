package com.lolsearcher.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lolsearcher.notification.NotificationConstant.IDENTIFICATION_SUBJECT;
import static com.lolsearcher.notification.NotificationConstant.LOGIN_ALARM_SUBJECT;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationServerApi notificationServerApi;

    public void sendIdentificationMessage(NotificationDevice device, String deviceId, String certificationNumber) {

        RequestIdentificationNotificationDto contents = new RequestIdentificationNotificationDto(certificationNumber);

        RequestNotificationDto requestNotificationDto = RequestNotificationDto.builder()
                .device(device)
                .deviceId(deviceId)
                .subject(IDENTIFICATION_SUBJECT)
                .contents(contents)
                .build();

        notificationServerApi.sendNotificationMessage(requestNotificationDto);
    }

    public void sendLoginMessage(NotificationDevice device, String deviceId, String sessionId, String ipAddress) {

        RequestLoginNotificationDto contents = new RequestLoginNotificationDto(sessionId, ipAddress);

        RequestNotificationDto requestNotificationDto = RequestNotificationDto.builder()
                .device(device)
                .deviceId(deviceId)
                .subject(LOGIN_ALARM_SUBJECT)
                .contents(contents)
                .build();

        notificationServerApi.sendNotificationMessage(requestNotificationDto);
    }
}
