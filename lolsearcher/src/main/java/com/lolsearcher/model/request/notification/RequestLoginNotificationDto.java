package com.lolsearcher.model.request.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RequestLoginNotificationDto {

    private String sessionId;
    private String ipAddress;

    public RequestLoginNotificationDto(){
        sessionId = "";
        ipAddress = "";
    }
}
