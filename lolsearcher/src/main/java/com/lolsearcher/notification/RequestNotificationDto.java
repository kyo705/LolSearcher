package com.lolsearcher.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class RequestNotificationDto {

    private NotificationDevice device;
    private Long fromUserId;
    private List<Long> toUserIds;
    private String subject;
    private Object contents;

    public RequestNotificationDto(){}
}
