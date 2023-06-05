package com.lolsearcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.lolsearcher.ban.BanConstant.LOGIN_BAN;
import static com.lolsearcher.ban.BanConstant.SEARCH_BAN;
import static com.lolsearcher.search.match.MatchConstant.*;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${lolsearcher.redis.ttl.login-ban}")
    private Long LOGIN_BAN_TTL;
    @Value("${lolsearcher.redis.ttl.search-ban}")
    private Long SEARCH_BAN_TTL;
    @Value("${lolsearcher.redis.ttl.championId}")
    private Long CHAMPION_ID_LIST_TTL;
    @Value("${lolsearcher.redis.ttl.queueId}")
    private Long QUEUE_ID_LIST_TTL;
    @Value("${lolsearcher.redis.ttl.item}")
    private Long ITEM_ID_LIST_TTL;
    @Value("${lolsearcher.redis.ttl.perk}")
    private Long PERK_ID_LIST_TTL;
    @Value("${lolsearcher.redis.ttl.gameVersion}")
    private Long GAME_VERSION_LIST_TTL;

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory){

        Map<String, RedisCacheConfiguration> entityCacheConfigs = new HashMap<>();

        entityCacheConfigs.put(LOGIN_BAN,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(LOGIN_BAN_TTL)));
        entityCacheConfigs.put(SEARCH_BAN,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(SEARCH_BAN_TTL)));
        entityCacheConfigs.put(CHAMPION_ID_LIST,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CHAMPION_ID_LIST_TTL)));
        entityCacheConfigs.put(QUEUE_ID_LIST,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(QUEUE_ID_LIST_TTL)));
        entityCacheConfigs.put(ITEM_ID_LIST,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(ITEM_ID_LIST_TTL)));
        entityCacheConfigs.put(PERK_ID_LIST,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(PERK_ID_LIST_TTL)));
        entityCacheConfigs.put(GAME_VERSION_LIST,
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(GAME_VERSION_LIST_TTL)));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(createDefaultCacheConfig())
                .withInitialCacheConfigurations(entityCacheConfigs)
                .build();
    }

    private RedisCacheConfiguration createDefaultCacheConfig(){

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }
}
