package com.lolsearcher.model.request.notification;

import com.lolsearcher.constant.enumeration.NotificationDevice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RequestNotificationDto {

    private NotificationDevice device;
    private String deviceId; /* email 주소, phone number ... */
    private String subject; /* 인증, 광고, 로그인 알림 등.. */
    private Object contents;

    public RequestNotificationDto(){}
}
