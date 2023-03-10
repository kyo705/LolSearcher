package com.lolsearcher.model.response.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ResponseNotificationDto {

    private final String message;

    public ResponseNotificationDto(){
        message = "";
    }
}
