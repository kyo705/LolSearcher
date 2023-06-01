package com.lolsearcher.search.match;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;

import java.util.stream.Stream;

import static com.lolsearcher.search.match.MatchConstant.*;

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

    protected static void setupWithCache(CacheManager cacheManager) {

        Cache cache1 = cacheManager.getCache(CHAMPION_ID_LIST);
        cache1.put(1, "true");cache1.put(2, "true");cache1.put(3, "true");cache1.put(4, "true");cache1.put(5, "true");
        cache1.put(6, "true");cache1.put(7, "true");cache1.put(8, "true");cache1.put(9, "true");cache1.put(10, "true");
        cache1.put(11, "true");cache1.put(12, "true");cache1.put(13, "true");cache1.put(14, "true");cache1.put(15, "true");
        cache1.put(16, "true");cache1.put(17, "true");cache1.put(18, "true");cache1.put(19, "true");cache1.put(20, "true");

        Cache cache2 = cacheManager.getCache(ITEM_ID_LIST);
        cache2.put(0, "true");cache2.put(1, "true");cache2.put(2, "true");cache2.put(3, "true");cache2.put(4, "true");
        cache2.put(5, "true");cache2.put(6, "true");

        Cache cache3 = cacheManager.getCache(PERK_ID_LIST);
        cache3.put(1000, "true");cache3.put(2000, "true");cache3.put(100, "true");cache3.put(500, "true");cache3.put(600, "true");
        cache3.put(1, "true");cache3.put(2, "true");cache3.put(3, "true");cache3.put(4, "true");cache3.put(21, "true");cache3.put(22, "true");

        Cache cache4 = cacheManager.getCache(QUEUE_ID_LIST);
        cache4.put(1, "true");
    }
}
