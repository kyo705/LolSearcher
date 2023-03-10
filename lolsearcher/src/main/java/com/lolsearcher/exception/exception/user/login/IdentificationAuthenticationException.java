package com.lolsearcher.exception.exception.user.login;

import org.springframework.security.core.AuthenticationException;

public class IdentificationAuthenticationException extends AuthenticationException {

    private final int statusCode;

    public IdentificationAuthenticationException(int statusCode, String msg, Throwable cause) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public IdentificationAuthenticationException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
