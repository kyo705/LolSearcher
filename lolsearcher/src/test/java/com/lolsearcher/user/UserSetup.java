package com.lolsearcher.user;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Map;
import java.util.stream.Stream;

import static com.lolsearcher.user.LoginSecurityState.ALARM;
import static com.lolsearcher.user.Role.TEMPORARY;

public class UserSetup {

    protected static Stream<UserCreateRequest> validUserCreatingParam() {

        return Stream.of(
                UserCreateRequest.builder()
                        .email("new_user@naver.com")
                        .password("1234567890")
                        .username("뉴비")
                        .build()
        );
    }

    protected static Stream<UserCreateRequest> invalidUserCreatingParam() {

        return Stream.of(
                UserCreateRequest.builder()
                        .email("u!ser@naver.com")
                        .password("123456789")
                        .username("뉴비")
                        .build(),
                UserCreateRequest.builder()
                        .email("new_user@naver.com")
                        .password("12345678")
                        .username("kyo705")
                        .build(),
                UserCreateRequest.builder()
                        .email("new_user@naver.com")
                        .password("123456789")
                        .username("   ")
                        .build()
        );
    }

    protected static Stream<UserCreateRequest> existingUserCreatingParam() {

        return Stream.of(
                UserCreateRequest.builder()
                        .email("user@naver.com")
                        .password("123456789")
                        .username("뉴비")
                        .build()
        );
    }

    protected static Stream<Arguments> validUpdatingParam() {

        return Stream.of(
                Arguments.of(
                        UserUpdateRequest.builder()
                        .password("987654321")
                        .build()
                ),
                Arguments.of(
                        UserUpdateRequest.builder()
                        .email("updatingUser@naver.com")
                        .build()
                ),
                Arguments.of(
                        UserUpdateRequest.builder()
                        .name("변경된 유저")
                        .build()
                ),
                Arguments.of(
                        UserUpdateRequest.builder()
                        .role(TEMPORARY)
                        .build()
                ),
                Arguments.of(
                        UserUpdateRequest.builder()
                                .loginSecurity(ALARM)
                                .build()
                ),
                Arguments.of(
                        UserUpdateRequest.builder()
                        .email("updatingUser@naver.com")
                        .password("987654321")
                        .name("변경된 유저")
                        .role(TEMPORARY)
                        .build()
                )
        );
    }

    protected static Stream<Arguments> invalidUpdatingParam() {

        return Stream.of(
                Arguments.of(
                        Map.of(
                                "password", "12345678"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "email", "updating%User@naver.com"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "name", "123456789012345678901"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "role", "INVALID"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "role", "2"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "loginSecurity", "INVALID"
                        )
                ),
                Arguments.of(
                        Map.of(
                                "loginSecurity", "3"
                        )
                )
        );
    }
}
