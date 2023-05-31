package com.lolsearcher.search.champion;

import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.search.champion.ChampionConstant.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChampionRepository implements ChampionRepository{

    private final CacheManager cacheManager;
    private final Map<String, RedisTemplate> redisTemplates;

    @Override
    public List<ChampPositionStats> findAll(int positionId, String version) {

        String key = getChampPositionStatsKey(positionId, version);
        RedisTemplate<String, ChampPositionStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_POSITION_STATS_TEMPLATE_NAME);

        return requireNonNull(champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChampItemStats> findItemStats(int championId, String version) {

        String key = getChampItemStatsKey(championId, version);
        RedisTemplate<String, ChampItemStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_ITEM_STATS_TEMPLATE_NAME);

        return requireNonNull(champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChampEnemyStats> findEnemyStats(int championId, String version) {

        String key = getChampEnemyStatsKey(championId, version);
        RedisTemplate<String, ChampEnemyStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_ENEMY_STATS_TEMPLATE_NAME);

        return requireNonNull(champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1))
                .stream()
                .peek(entity -> entity.validate(cacheManager))
                .collect(Collectors.toList());
    }

    private String getChampPositionStatsKey(int positionId, String version) {

        return CHAMP_POSITION_STATS_PREFIX + positionId + version;
    }

    private String getChampItemStatsKey(int positionId, String version) {

        return CHAMP_ITEM_STATS_PREFIX + positionId + version;
    }

    private String getChampEnemyStatsKey(int positionId, String version) {

        return CHAMP_ENEMY_STATS_PREFIX + positionId + version;
    }
}
