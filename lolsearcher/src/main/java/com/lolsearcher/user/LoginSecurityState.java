package com.lolsearcher.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum LoginSecurityState {

    NONE(0),
    ALARM(1),
    IDENTIFICATION(2);
    private final int level;

    private static final Map<Integer, LoginSecurityState> BY_LEVEL =
            Stream.of(values()).collect(Collectors.toMap(LoginSecurityState::getLevel, e -> e));


    public static LoginSecurityState valueOfLevel(int level){
        try{
            return BY_LEVEL.get(level);
        }catch (NullPointerException e) {
            throw new IllegalArgumentException("LoginSecurityState must be in boundary");
        }
    }
}

@Component
class LoginSecurityConverter implements Converter<Integer, LoginSecurityState> {

    @Override
    public LoginSecurityState convert(Integer source) {
        return LoginSecurityState.valueOfLevel(source);
    }
}