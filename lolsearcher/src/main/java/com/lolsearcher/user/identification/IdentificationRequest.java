package com.lolsearcher.user.identification;

import com.lolsearcher.notification.NotificationDevice;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class IdentificationRequest {

    @NotNull NotificationDevice device;
}
