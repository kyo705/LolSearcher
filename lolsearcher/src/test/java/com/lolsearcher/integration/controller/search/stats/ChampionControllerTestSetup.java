package com.lolsearcher.integration.controller.search.stats;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.PositionStatus;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.TotalChampStatDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class ChampionControllerTestSetup {

    public static RequestChampPositionStatsDto getValidChampPositionRequest() {

        return RequestChampPositionStatsDto.builder()
                .position(PositionStatus.TOP.getCode())
                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                .build();
    }

    public static List<ChampPositionStatsDto> getChampPositionStats(RequestChampPositionStatsDto request) {

        return List.of(
                ChampPositionStatsDto.builder()
                        .championId(1)
                        .positionId(request.getPosition())
                        .gameVersion(request.getGameVersion())
                        .wins(10000)
                        .losses(9000)
                        .bans(5000)
                        .build(),

                ChampPositionStatsDto.builder()
                        .championId(2)
                        .positionId(request.getPosition())
                        .gameVersion(request.getGameVersion())
                        .wins(20000)
                        .losses(19000)
                        .bans(15000)
                        .build(),

                ChampPositionStatsDto.builder()
                        .championId(3)
                        .positionId(request.getPosition())
                        .gameVersion(request.getGameVersion())
                        .wins(13000)
                        .losses(13000)
                        .bans(6000)
                        .build()
        );
    }

    public static Stream<Arguments> getInvalidChampPositionRequest() {

        return Stream.of(
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(PositionStatus.NONE.getCode()) //invalid
                                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                                .build()
                ),
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(-1) //invalid
                                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                                .build()
                ),
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(6) //invalid
                                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                                .build()
                ),
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(PositionStatus.TOP.getCode())
                                .gameVersion("") //invalid
                                .build()
                ),
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(PositionStatus.TOP.getCode())
                                .gameVersion(null) //invalid
                                .build()
                ),
                Arguments.of(
                        RequestChampPositionStatsDto.builder()
                                .position(PositionStatus.TOP.getCode())
                                .gameVersion("not current version") //invalid
                                .build()
                )
        );
    }

    public static RequestChampDetailStatsDto getValidChampDetailRequest() {

        return RequestChampDetailStatsDto.builder()
                .championId(1)
                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                .build();
    }

    public static TotalChampStatDto getChampDetailStats(RequestChampDetailStatsDto request) {

        return new TotalChampStatDto();
    }

    protected static Stream<Arguments> getInvalidChampDetailRequest(){

        return Stream.of(
                Arguments.of(
                        RequestChampDetailStatsDto.builder()
                                .championId(-1) //invalid
                                .gameVersion(LolSearcherConstants.CURRENT_GAME_VERSION)
                                .build()
                ),
                Arguments.of(
                        RequestChampDetailStatsDto.builder()
                                .championId(1)
                                .gameVersion("not current version") //invalid
                                .build()
                ),
                Arguments.of(
                        RequestChampDetailStatsDto.builder()
                                .championId(1)
                                .gameVersion("") //invalid
                                .build()
                ),
                Arguments.of(
                        RequestChampDetailStatsDto.builder()
                                .championId(1)
                                .gameVersion(null) //invalid
                                .build()
                )
        );
    }
}
