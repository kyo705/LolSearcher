package com.lolsearcher.configuration.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.lolsearcher.constant.CacheConstants.*;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${lolsearcher.redis.ttl.ingame}")
    private Long IN_GAME_TTL;
    @Value("${lolsearcher.redis.ttl.match}")
    private Long MATCH_TTL;
    @Value("${lolsearcher.redis.ttl.search-ban}")
    private Long SEARCH_BAN_TTL;
    @Value("${lolsearcher.redis.ttl.search-abusing}")
    private Long SEARCH_ABUSING_TTL;
    @Value("${lolsearcher.redis.ttl.search-ban}")
    private Long LOGIN_BAN_TTL;
    @Value("${lolsearcher.redis.ttl.login-abusing}")
    private Long LOGIN_ABUSING_TTL;
    @Value("${lolsearcher.redis.ttl.champions}")
    private Long CHAMPION_LIST_TTL;
    @Value("${lolsearcher.redis.ttl.join-certification}")
    private Long JOIN_CERTIFICATION_TTL;

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){

        RedisCacheConfiguration config = defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> entityCacheConfigs = new HashMap<>();
        entityCacheConfigs.put(IN_GAME_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(IN_GAME_TTL)));
        entityCacheConfigs.put(MATCH_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(MATCH_TTL)));
        entityCacheConfigs.put(SEARCH_BAN_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(SEARCH_BAN_TTL)));
        entityCacheConfigs.put(SEARCH_ABUSING_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(SEARCH_ABUSING_TTL)));
        entityCacheConfigs.put(LOGIN_BAN_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(LOGIN_BAN_TTL)));
        entityCacheConfigs.put(LOGIN_ABUSING_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(LOGIN_ABUSING_TTL)));
        entityCacheConfigs.put(CHAMPION_LIST_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(CHAMPION_LIST_TTL)));
        entityCacheConfigs.put(JOIN_CERTIFICATION_KEY, defaultCacheConfig().entryTtl(Duration.ofSeconds(JOIN_CERTIFICATION_TTL)));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(entityCacheConfigs)
                .build();
    }
}
