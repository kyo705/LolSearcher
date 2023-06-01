package com.lolsearcher.search.champion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
import com.lolsearcher.search.match.MatchConstant;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.stream.Stream;

import static com.lolsearcher.search.champion.ChampionConstant.*;
import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;

public class ChampionSetup {

    protected static Stream<Arguments> validChampionsParam() {

        return Stream.of(
                Arguments.of(
                        "TOP",
                        CURRENT_GAME_VERSION
                ) ,
                Arguments.of(
                        "JUNGLE",
                        CURRENT_GAME_VERSION
                )
        );
    }

    protected static Stream<Arguments> invalidChampionsParam() {

        return Stream.of(
                Arguments.of(
                        "TOP",
                        "INVALID_GAME_VERSION"
                ),
                Arguments.of(
                        "top",
                        CURRENT_GAME_VERSION
                ),
                Arguments.of(
                        "INVALID_POSITION",
                        CURRENT_GAME_VERSION
                )
        );
    }

    protected  static Stream<Arguments> validChampionDetailsParam() {

        return Stream.of(
                Arguments.of(
                        1,
                        CURRENT_GAME_VERSION
                )
        );
    }

    protected  static Stream<Arguments> invalidChampionDetailsParam() {

        return Stream.of(
                Arguments.of(
                        1,
                        "INVALID_GAME_VERSION"
                ),
                Arguments.of(
                        100,
                        CURRENT_GAME_VERSION
                )
        );
    }

    protected static void setupWithCache(CacheManager cacheManager) {

        cacheManager.getCache(MatchConstant.GAME_VERSION_LIST).put(CURRENT_GAME_VERSION, "true");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(1, "제드");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(2, "탈론");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(3, "르블랑");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(4, "럭스");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(5, "그레이브즈");
        cacheManager.getCache(MatchConstant.CHAMPION_ID_LIST).put(6, "아펠리오스");
        cacheManager.getCache(MatchConstant.ITEM_ID_LIST).put(1, "월식");
        cacheManager.getCache(MatchConstant.ITEM_ID_LIST).put(2, "불꽃 태양 망토");
        cacheManager.getCache(MatchConstant.ITEM_ID_LIST).put(3, "슈렐리아");
    }

    protected static void setupChampPositionStatsWithRedis(ZSetOperations<String, String> ops, ObjectMapper objectMapper) {

        String key = CHAMP_POSITION_STATS_PREFIX + PositionState.TOP.getCode() + ":" + CURRENT_GAME_VERSION;

        champPositionStats(1, CURRENT_GAME_VERSION)
                .forEach(value -> {
                    try {
                        ops.add(key, objectMapper.writeValueAsString(value), score(value));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected static void setupChampItemStatsWithRedis(ZSetOperations<String, String> ops, ObjectMapper objectMapper) {

        String key = CHAMP_ITEM_STATS_PREFIX + 1 + ":" + CURRENT_GAME_VERSION;

        champItemStats(1, CURRENT_GAME_VERSION)
                .forEach(value -> {
                    try {
                        ops.add(key, objectMapper.writeValueAsString(value), score(value));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected static void setupChampEnemyStatsWithRedis(ZSetOperations<String, String> ops, ObjectMapper objectMapper) {

        String key = CHAMP_ENEMY_STATS_PREFIX + 1 + ":" + CURRENT_GAME_VERSION;

        champEnemyStats(1, CURRENT_GAME_VERSION)
                .forEach(value -> {
                    try {
                        ops.add(key, objectMapper.writeValueAsString(value), score(value));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }



    protected static List<ChampPositionStats> champPositionStats(int positionId, String gameVersion) {

        return List.of(
                ChampPositionStats.builder()
                        .id(1)
                        .championId(1)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(1000)
                        .losses(900)
                        .bans(2000)
                        .build(),

                ChampPositionStats.builder()
                        .id(2)
                        .championId(2)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(1100)
                        .losses(950)
                        .bans(3000)
                        .build(),

                ChampPositionStats.builder()
                        .id(3)
                        .championId(3)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(1000)
                        .losses(950)
                        .bans(500)
                        .build(),

                ChampPositionStats.builder()
                        .id(4)
                        .championId(4)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(2000)
                        .losses(1900)
                        .bans(0)
                        .build(),

                ChampPositionStats.builder()
                        .id(5)
                        .championId(5)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(1500)
                        .losses(1600)
                        .bans(0)
                        .build(),

                ChampPositionStats.builder()
                        .id(6)
                        .championId(6)
                        .gameVersion(gameVersion)
                        .positionId(positionId)
                        .wins(1300)
                        .losses(1100)
                        .bans(200)
                        .build()
        );
    }

    protected static List<ChampItemStats> champItemStats(int championId, String gameVersion) {

        return List.of(
                ChampItemStats.builder()
                        .id(1)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .itemId(1)
                        .wins(1000)
                        .losses(900)
                        .build(),

                ChampItemStats.builder()
                        .id(2)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .itemId(2)
                        .wins(1000)
                        .losses(900)
                        .build(),

                ChampItemStats.builder()
                        .id(3)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .itemId(3)
                        .wins(1000)
                        .losses(900)
                        .build()
        );
    }

    protected static List<ChampEnemyStats> champEnemyStats(int championId, String gameVersion) {

        return List.of(
                ChampEnemyStats.builder()
                        .id(1)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .enemyChampionId(1)
                        .wins(1000)
                        .losses(900)
                        .build(),

                ChampEnemyStats.builder()
                        .id(2)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .enemyChampionId(5)
                        .wins(1000)
                        .losses(900)
                        .build(),

                ChampEnemyStats.builder()
                        .id(3)
                        .championId(championId)
                        .gameVersion(gameVersion)
                        .enemyChampionId(2)
                        .wins(1000)
                        .losses(900)
                        .build()
        );
    }


    protected static double score(ChampPositionStats value) {

        return value.getWins() - value.getLosses() + value.getBans()*1.2;
    }

    protected static double score(ChampItemStats value) {

        return ((double) value.getWins() + value.getLosses())/1000 + ((double) value.getWins() - value.getLosses())/100;
    }

    protected static double score(ChampEnemyStats value) {

        return ((double) value.getWins() + value.getLosses())/1000 + ((double) value.getWins() - value.getLosses())/100;
    }
}
