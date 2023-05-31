package com.lolsearcher.errors.exception.user.login;

import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Getter
public class NotYetSecondLevelLoginException extends AuthenticationException {

    private final Authentication authentication;

    public NotYetSecondLevelLoginException(Authentication authentication) {
        super("2차 로그인이 필요함");
        this.authentication = authentication;
    }
}
