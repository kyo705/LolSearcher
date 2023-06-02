package com.lolsearcher.user.session;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SessionSetup {

    protected static Stream<Arguments> sessionIds() {

        return Stream.of(
                Arguments.of(
                        List.of("session-id-1", "session-id-2", "session-id-3")
                )
        );
    }

    protected static void setupSessions(List<String> sessionIds, String username, RedisTemplate<String, Object> redisTemplate) {

        String principleKey = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:" + username;
        String sessionKeyPrefix = "spring:session:sessions:";
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("attr1", "val1");
        sessionData.put("attr2", "val2");
        sessionData.put("attr3", "val3");

        sessionIds.forEach(sessionId -> redisTemplate.opsForSet().add(principleKey, sessionId));

        sessionIds.forEach(sessionId -> redisTemplate.opsForHash().putAll(sessionKeyPrefix + sessionId, sessionData));
    }
}
