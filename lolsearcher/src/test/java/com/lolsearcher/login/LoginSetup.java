package com.lolsearcher.login;

import java.util.stream.Stream;

public class LoginSetup {

    static Stream<LoginRequest> getValidUsernamePassword() {

        return Stream.of(
                LoginRequest.builder()
                        .email("user@naver.com")
                        .password("123456789")
                        .build(),
                LoginRequest.builder()
                        .email("temporary@naver.com")
                        .password("123456789")
                        .build()
        );
    }

    static Stream<LoginRequest> getInvalidUsernamePassword() {

        return Stream.of(
                LoginRequest.builder()
                        .email("user@naver")
                        .password("bad password")
                        .build(),
                LoginRequest.builder()
                        .email("user@naver.com")
                        .password("bad password")
                        .build(),
                LoginRequest.builder()
                        .email("not-exist@naver.com")
                        .password("123456789")
                        .build()
        );
    }
}
