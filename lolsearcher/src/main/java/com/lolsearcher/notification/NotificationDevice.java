package com.lolsearcher.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.user.UserConstant.EMAIL_REGEX;
import static com.lolsearcher.user.UserConstant.PHONE_NUMBER_REGEX;

@Getter
@RequiredArgsConstructor
public enum NotificationDevice {

    E_MAIL(0),
    PHONE(1);

    private final int code;

    private static final Map<Integer, NotificationDevice> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(NotificationDevice::getCode, e -> e));

    public static NotificationDevice valueOfCode(int code){
        return BY_CODE.get(code);
    }

    public void validate(String value) {

        checkArgument((this.code == 0 && value.matches(EMAIL_REGEX)) ||
                        (this.code == 1 && value.matches(PHONE_NUMBER_REGEX))
                , "Device value doesn't match with device"
        );
    }
}
