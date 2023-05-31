package com.lolsearcher.search.summoner;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.stream.Stream;

public class SummonerSetup {

    public static Stream<String> correctSummonerName() {

        return Stream.of(
                "푸켓푸켓",
                "푸켓#푸켓",
                "135",
                "푸",
                "0123456789012345678901234567890123456789012345678",  // 49 글자
                "01234567890123456789012345678901234567890123456789"  // 50 글자
        );
    }

    public static Stream<String> incorrectSummonerName() {

        return Stream.of(
                "  ",
                "#   ~",
                "012345678901234567890123456789012345678901234567890" // 51 글자
        );
    }

    public static Stream<String> specialCharacterSummonerName() {

        return Stream.of(
                " 푸켓푸켓 ",
                "#푸켓푸켓~",
                "푸#켓#푸  !켓"
        );
    }

    public static Stream<Exception> timeoutError() {

        return Stream.of(
                new QueryTimeoutException("응답 시간 초과"),
                new CannotAcquireLockException("락 획득 실패")
        );
    }

    public static Stream<Exception> externalServerError() {

        return Stream.of(
                new WebClientResponseException(HttpStatus.BAD_GATEWAY.value(), HttpStatus.BAD_GATEWAY.getReasonPhrase(), null, null, null)
        );
    }
}
