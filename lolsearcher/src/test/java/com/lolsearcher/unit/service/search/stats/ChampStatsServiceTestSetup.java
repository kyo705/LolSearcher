package com.lolsearcher.unit.service.search.stats;

import com.lolsearcher.search.champion.ChampionDetailsRequest;
import com.lolsearcher.search.champion.ChampionsRequest;
import com.lolsearcher.search.champion.PositionState;
import com.lolsearcher.search.champion.entity.ChampEnemyStats;
import com.lolsearcher.search.champion.entity.ChampItemStats;
import com.lolsearcher.search.champion.entity.ChampPositionStats;

import java.util.List;

import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;

public class ChampStatsServiceTestSetup {

    protected static ChampionsRequest getRequestChampPositionStatsDto() {

        return ChampionsRequest.builder()
                .position(PositionState.TOP)
                .gameVersion(CURRENT_GAME_VERSION)
                .build();
    }

    public static List<ChampPositionStats> getAllChampStatsFromPosition(ChampionsRequest request) {

        ChampPositionStats zedStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(1)
                .positionId(request.getPosition().getCode())
                .wins(50)
                .losses(50)
                .build();

        ChampPositionStats talonStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(2)
                .positionId(request.getPosition().getCode())
                .wins(50)
                .losses(50)
                .build();

        ChampPositionStats yasuoStats = ChampPositionStats.builder()
                .gameVersion(request.getGameVersion())
                .championId(3)
                .positionId(request.getPosition().getCode())
                .wins(50)
                .losses(50)
                .build();

        return List.of(zedStats, talonStats, yasuoStats);
    }

    public static ChampionDetailsRequest getRequestChampDetailStatsDto() {

        return ChampionDetailsRequest.builder()
                .championId(1)
                .gameVersion(CURRENT_GAME_VERSION)
                .build();
    }

    public static List<ChampItemStats> getAllChampItemStats(ChampionDetailsRequest request) {

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

    public static List<ChampEnemyStats> getAllChampEnemyStats(ChampionDetailsRequest request) {

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
