package com.lolsearcher.user.identification;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class IdentificationSetup {

    protected static Stream<Arguments> validParam() {

        return Stream.of(
                Arguments.of(
                        1L,
                        "{\"device\" : \"E_MAIL\", \"deviceValue\" : \"user@naver.com\"}"
                ),
                Arguments.of(
                        1L,
                        "{\"device\" : \"PHONE\", \"deviceValue\" : \"010-1234-5678\"}"
                ),
                Arguments.of(
                        1L,
                        "{\"device\" : \"0\", \"deviceValue\" : \"user@naver.com\"}"
                )
        );
    }

    protected static Stream<Arguments> invalidParam() {

        return Stream.of(
                Arguments.of(
                        1L,
                        "{\"device\" : \"e-mail\", \"deviceValue\" : \"user@naver.com\"}"
                ),
                Arguments.of(
                        1L,
                        "{\"device\" : \"E_MAIL\", \"deviceValue\" : \"user@naver\"}"
                ),
                Arguments.of(
                        1L,
                        "{\"device\" : \"E_MAIL\", \"deviceValue\" : \"010-1234-5678\"}"
                )
        );
    }

}
