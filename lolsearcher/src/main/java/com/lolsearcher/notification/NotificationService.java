package com.lolsearcher.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationServerApi notificationServerApi;

    public void sendNotificationMessage(NotificationDevice device, Long from, List<Long> to, String subject, Object contents) {

        RequestNotificationDto requestNotificationDto = RequestNotificationDto.builder()
                .device(device)
                .fromUserId(from)
                .toUserIds(to)
                .subject(subject)
                .contents(contents)
                .build();

        notificationServerApi.sendNotificationMessage(requestNotificationDto);
    }
}
