package com.lolsearcher.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RequestIdentificationNotificationDto {

    private final String certificationNumber;
}
