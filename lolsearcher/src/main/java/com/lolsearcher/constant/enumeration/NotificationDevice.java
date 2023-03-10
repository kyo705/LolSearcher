package com.lolsearcher.constant.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum NotificationDevice {

    E_MAIL(0),
    PHONE(1),
    IOS(2),
    ANDROID(3);

    private final int code;

    private static final Map<Integer, NotificationDevice> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(NotificationDevice::getCode, e -> e));

    public static final NotificationDevice valueOfCode(int code){
        return BY_CODE.get(code);
    }
}
