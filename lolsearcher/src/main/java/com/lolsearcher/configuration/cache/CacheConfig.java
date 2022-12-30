package com.lolsearcher.configuration.cache;

import com.lolsearcher.constant.CacheConstants;
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

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${lolsearcher.redis.ttl.ingame}")
    private Long IN_GAME_TTL;

    @Value("${lolsearcher.redis.ttl.match}")
    private Long MATCH_TTL;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){

        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> entityCacheConfigs = new HashMap<>();
        entityCacheConfigs.put(CacheConstants.IN_GAME_KEY, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(IN_GAME_TTL)));
        entityCacheConfigs.put(CacheConstants.MATCH_KEY, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(MATCH_TTL)));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(entityCacheConfigs)
                .build();
    }
}
