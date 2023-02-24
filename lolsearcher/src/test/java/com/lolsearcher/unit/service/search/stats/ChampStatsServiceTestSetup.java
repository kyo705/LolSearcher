package com.lolsearcher.unit.service.search.stats;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.PositionStatus;
import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;

import java.util.List;

public class ChampStatsServiceTestSetup {

    protected static RequestChampPositionStatsDto getRequestChampPositionStatsDto() {

        return RequestChampPositionStatsDto.builder()
                .position(PositionStatus.TOP.getId())
                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                .build();
    }

    public static List<ChampPositionStats> getAllChampStatsFromPosition(RequestChampPositionStatsDto request) {

        ChampPositionStats zedStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(1)
                .positionId(request.getPosition())
                .wins(50)
                .losses(50)
                .build();

        ChampPositionStats talonStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(2)
                .positionId(request.getPosition())
                .wins(50)
                .losses(50)
                .build();

        ChampPositionStats yasuoStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(3)
                .positionId(request.getPosition())
                .wins(50)
                .losses(50)
                .build();

        return List.of(zedStats, talonStats, yasuoStats);
    }

    public static RequestChampDetailStatsDto getRequestChampDetailStatsDto() {

        return RequestChampDetailStatsDto.builder()
                .championId(1)
                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                .build();
    }

    public static List<ChampItemStats> getAllChampItemStats(RequestChampDetailStatsDto request) {

        ChampItemStats doranRingStats = ChampItemStats.builder()
                .championId(request.getChampionId())
                .gameVersion(request.getGameVersion())
                .itemId(1)
                .wins(50)
                .losses(50)
                .build();

        ChampItemStats doranSwordStats = ChampItemStats.builder()
                .championId(request.getChampionId())
                .gameVersion(request.getGameVersion())
                .itemId(2)
                .wins(50)
                .losses(50)
                .build();

        return List.of(doranSwordStats, doranRingStats);
    }

    public static List<ChampEnemyStats> getAllChampEnemyStats(RequestChampDetailStatsDto request) {

        ChampEnemyStats zedStats = ChampEnemyStats.builder()
                .championId(request.getChampionId())
                .gameVersion(request.getGameVersion())
                .enemyChampionId(1)
                .wins(50)
                .losses(50)
                .build();

        ChampEnemyStats talonStats = ChampEnemyStats.builder()
                .championId(request.getChampionId())
                .gameVersion(request.getGameVersion())
                .enemyChampionId(2)
                .wins(50)
                .losses(50)
                .build();

        ChampEnemyStats yasuoStats = ChampEnemyStats.builder()
                .championId(request.getChampionId())
                .gameVersion(request.getGameVersion())
                .enemyChampionId(3)
                .wins(50)
                .losses(50)
                .build();

        return List.of(zedStats, talonStats, yasuoStats);
    }
}
