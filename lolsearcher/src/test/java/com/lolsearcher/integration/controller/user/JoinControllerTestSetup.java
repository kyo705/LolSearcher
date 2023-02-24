package com.lolsearcher.integration.controller.user;

import com.lolsearcher.model.request.user.RequestUserJoinDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class JoinControllerTestSetup {

    protected static RequestUserJoinDto getValidJoinRequest() {

        return RequestUserJoinDto.builder()
                .email("email@gmail.com")
                .password("password")
                .username("닉네임")
                .build();
    }

    protected static Stream<Arguments> getInvalidJoinRequest() {

        return Stream.of(
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("InvalidEmailFrom") //invalid
                                .password("password")
                                .username("닉네임")
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("") //invalid
                                .password("password")
                                .username("닉네임")
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email(null) //invalid
                                .password("password")
                                .username("닉네임")
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("email@gmail.com")
                                .password("") //invalid
                                .username("닉네임")
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("email@gmail.com")
                                .password(null) //invalid
                                .username("닉네임")
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("email@gmail.com")
                                .password("password")
                                .username("") //invalid
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("email@gmail.com")
                                .password("password")
                                .username(null) //invalid
                                .build()
                ),
                Arguments.of(
                        RequestUserJoinDto.builder()
                                .email("email@gmail.com")
                                .password("password")
                                .username("닉네!임#") //invalid
                                .build()
                )
        );
    }
}
