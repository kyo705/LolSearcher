package com.lolsearcher.search.champion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.lolsearcher.search.champion.ChampionConstant.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChampionRepository implements ChampionRepository{

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private final StringRedisTemplate redisTemplate;

    @Override
    public List<ChampPositionStats> findAll(int positionId, String version) {

        String key = getChampPositionStatsKey(positionId, version);

        return requireNonNull(redisTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .map(str -> {
                    try {
                        return objectMapper.readValue(str, ChampPositionStats.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChampItemStats> findItemStats(int championId, String version) {

        String key = getChampItemStatsKey(championId, version);

        return requireNonNull(redisTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .map(str -> {
                    try {
                        return objectMapper.readValue(str, ChampItemStats.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChampEnemyStats> findEnemyStats(int championId, String version) {

        String key = getChampEnemyStatsKey(championId, version);

        return requireNonNull(redisTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .map(str ->  {
                    try {
                        return objectMapper.readValue(str, ChampEnemyStats.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    private String getChampPositionStatsKey(int positionId, String version) {

        return CHAMP_POSITION_STATS_PREFIX + positionId + ":" + version;
    }

    private String getChampItemStatsKey(int positionId, String version) {

        return CHAMP_ITEM_STATS_PREFIX + positionId + ":" + version;
    }

    private String getChampEnemyStatsKey(int positionId, String version) {

        return CHAMP_ENEMY_STATS_PREFIX + positionId + ":" + version;
    }
}
