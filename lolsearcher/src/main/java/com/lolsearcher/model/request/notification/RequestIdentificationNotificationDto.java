package com.lolsearcher.model.request.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RequestIdentificationNotificationDto {

    private final int certificationNumber;

    public RequestIdentificationNotificationDto(){
        certificationNumber = 0;
    }
}
