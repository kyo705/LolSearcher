package com.lolsearcher.service.notification;

import com.lolsearcher.api.notification.NotificationServerApi;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.NotificationDevice;
import com.lolsearcher.model.request.notification.RequestIdentificationNotificationDto;
import com.lolsearcher.model.request.notification.RequestLoginNotificationDto;
import com.lolsearcher.model.request.notification.RequestNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationServerApi notificationServerApi;

    public void sendIdentificationMessage(NotificationDevice device, String deviceId, int certificationNumber) {

        RequestIdentificationNotificationDto contents = new RequestIdentificationNotificationDto(certificationNumber);

        RequestNotificationDto requestNotificationDto = RequestNotificationDto.builder()
                .device(device)
                .deviceId(deviceId)
                .subject(LolSearcherConstants.IDENTIFICATION_SUBJECT)
                .contents(contents)
                .build();

        notificationServerApi.sendNotificationMessage(requestNotificationDto);
    }

    public void sendLoginMessage(NotificationDevice device, String deviceId, String sessionId, String ipAddress) {

        RequestLoginNotificationDto contents = new RequestLoginNotificationDto(sessionId, ipAddress);

        RequestNotificationDto requestNotificationDto = RequestNotificationDto.builder()
                .device(device)
                .deviceId(deviceId)
                .subject(LolSearcherConstants.LOGIN_ALARM_SUBJECT)
                .contents(contents)
                .build();

        notificationServerApi.sendNotificationMessage(requestNotificationDto);
    }
}
