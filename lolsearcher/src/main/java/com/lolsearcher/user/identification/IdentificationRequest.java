package com.lolsearcher.user.identification;

import com.lolsearcher.notification.NotificationDevice;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class IdentificationRequest {

    @NotNull NotificationDevice device;
    @NotBlank String deviceValue;
}
