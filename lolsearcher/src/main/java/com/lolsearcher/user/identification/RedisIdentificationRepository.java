package com.lolsearcher.user.identification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import static com.lolsearcher.user.identification.IdentificationConstant.IDENTIFICATION_PREFIX;

@RequiredArgsConstructor
@Repository
public class RedisIdentificationRepository implements IdentificationRepository {

    private final StringRedisTemplate redisTemplate;

    public static String getKey(Long userId) {

        return IDENTIFICATION_PREFIX + userId;
    }

    @Override
    public void save(Long userId, String identificationNum) {

        redisTemplate.opsForValue().append(getKey(userId), identificationNum);
    }

    @Override
    public String find(Long userId) {

        return redisTemplate.opsForValue().get(getKey(userId));
    }

    @Override
    public Boolean delete(Long userId) {

        return redisTemplate.delete(getKey(userId));
    }

}
