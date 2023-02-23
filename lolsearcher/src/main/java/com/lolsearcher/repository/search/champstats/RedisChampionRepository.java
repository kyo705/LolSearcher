package com.lolsearcher.repository.search.champstats;

import com.lolsearcher.exception.exception.common.NoExistDataException;
import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.constant.LolSearcherConstants.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisChampionRepository implements ChampionRepository{

    private final Map<String, RedisTemplate> redisTemplates;

    @Override
    public List<ChampPositionStats> findAllChampPositionStats(int positionId, String version) {

        try{
            String key = generateChampPositionStatsKey(positionId, version);
            RedisTemplate<String, ChampPositionStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_POSITION_STATS_TEMPLATE_NAME);

            return champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1).stream().collect(Collectors.toList());
        }catch (ClassCastException e){
            String errorMessage = "RedisTemplate<String, ChampPositionStats> 타입의 redis template이 존재하지 않음";
            log.error(errorMessage);
            throw new NoExistDataException(errorMessage);
        }
    }

    @Override
    public List<ChampItemStats> findChampItems(int championId, String version) {

        try{
            String key = generateChampItemStatsKey(championId, version);
            RedisTemplate<String, ChampItemStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_ITEM_STATS_TEMPLATE_NAME);

            return champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1).stream().collect(Collectors.toList());
        }catch (ClassCastException e){
            String errorMessage = "RedisTemplate<String, ChampItemStats> 타입의 redis template이 존재하지 않음";
            log.error(errorMessage);
            throw new NoExistDataException(errorMessage);
        }
    }

    @Override
    public List<ChampEnemyStats> findChampEnemies(int championId, String version) {

        try{
            String key = generateChampEnemyStatsKey(championId, version);
            RedisTemplate<String, ChampEnemyStats> champPositionStatsTemplate = redisTemplates.get(CHAMP_ENEMY_STATS_TEMPLATE_NAME);

            return champPositionStatsTemplate.opsForZSet().reverseRange(key, 0, -1).stream().collect(Collectors.toList());
        }catch (ClassCastException e){
            String errorMessage = "RedisTemplate<String, ChampEnemyStats> 타입의 redis template이 존재하지 않음";
            log.error(errorMessage);
            throw new NoExistDataException(errorMessage);
        }
    }

    private String generateChampPositionStatsKey(int positionId, String version) {

        return CHAMP_POSITION_STATS_PREFIX + positionId + version;
    }

    private String generateChampItemStatsKey(int positionId, String version) {

        return CHAMP_ITEM_STATS_PREFIX + positionId + version;
    }

    private String generateChampEnemyStatsKey(int positionId, String version) {

        return CHAMP_ENEMY_STATS_PREFIX + positionId + version;
    }
}
