package com.lolsearcher.constant.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum LoginSecurityPolicyStatus {

    NONE(0),
    ALARM(1),
    IDENTIFICATION(2);
    private final int level;

    private static final Map<Integer, LoginSecurityPolicyStatus> BY_LEVEL =
            Stream.of(values()).collect(Collectors.toMap(LoginSecurityPolicyStatus::getLevel, e -> e));

    public static final LoginSecurityPolicyStatus valueOfLevel(int level){
        return BY_LEVEL.get(level);
    }
}
