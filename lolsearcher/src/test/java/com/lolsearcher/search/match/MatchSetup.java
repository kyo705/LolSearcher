package com.lolsearcher.search.match;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;

import java.util.stream.Stream;

public class MatchSetup {

    public static Stream<Arguments> correctParamWithFindMatches() {

        MatchRequest request1 = new MatchRequest();
        request1.setSummonerId("1");

        MatchRequest request2 = new MatchRequest();
        request2.setSummonerId("123456789012345678901234567890123456789012345678901234567890123"); // 길이 63


        return Stream.of(
                Arguments.of(request1) ,
                Arguments.of(request2)
        );
    }

    public static Stream<Arguments> incorrectParamWithFindMatches() {

        MatchRequest request1 = new MatchRequest();
        request1.setSummonerId("  ");
        request1.setQueueId(1);
        request1.setChampionId(1);
        request1.setCount(20);
        request1.setOffset(1);

        MatchRequest request2 = new MatchRequest();
        request2.setSummonerId("1234567890123456789012345678901234567890123456789012345678901234"); // 길이 64
        request2.setQueueId(1);
        request2.setChampionId(1);
        request2.setCount(20);
        request2.setOffset(1);

        MatchRequest request3 = new MatchRequest();
        request3.setSummonerId("summonerId");
        request3.setChampionId(-2);
        request3.setQueueId(1);
        request3.setCount(20);
        request3.setOffset(1);

        MatchRequest request4 = new MatchRequest();
        request4.setSummonerId("summonerId");
        request4.setChampionId(1);
        request4.setQueueId(-2);
        request4.setCount(20);
        request4.setOffset(1);


        MatchRequest request5 = new MatchRequest();
        request5.setSummonerId("summonerId");
        request5.setQueueId(1);
        request5.setChampionId(1);
        request5.setCount(20);
        request5.setOffset(-1);

        MatchRequest request6 = new MatchRequest();
        request6.setSummonerId("summonerId");
        request6.setQueueId(1);
        request6.setChampionId(1);
        request6.setCount(-1);
        request6.setOffset(1);

        return Stream.of(
                Arguments.of(request1),
                Arguments.of(request2),
                Arguments.of(request3),
                Arguments.of(request4),
                Arguments.of(request5),
                Arguments.of(request6)
        );
    }

    public static Stream<Arguments> externalExceptionWithFindMatches() {

        return Stream.of(
                Arguments.of(new DataIntegrityViolationException("데이터 충돌 발생"))
        );
    }

    public static Stream<Arguments> timeoutErrorWithFindMatches() {

        return Stream.of(
                Arguments.of(new QueryTimeoutException("쿼리 요청 시간 초과!!")),
                Arguments.of(new CannotAcquireLockException("락 획득 요청 시간 초과!!"))
        );
    }

}
