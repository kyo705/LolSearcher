package com.lolsearcher.config.database;

import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Qualifier("champPositionStatsRedisTemplate")
    @Bean
    RedisTemplate<String, ChampPositionStats> champPositionStatsRedisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, ChampPositionStats> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChampPositionStats.class));
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

    @Qualifier("champItemStatsRedisTemplate")
    @Bean
    RedisTemplate<String, ChampItemStats> champItemStatsRedisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, ChampItemStats> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChampItemStats.class));
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }

    @Qualifier("champEnemyStatsRedisTemplate")
    @Bean
    RedisTemplate<String, ChampEnemyStats> champEnemyStatsRedisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, ChampEnemyStats> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChampEnemyStats.class));
        redisTemplate.setConnectionFactory(connectionFactory);

        return redisTemplate;
    }
}
