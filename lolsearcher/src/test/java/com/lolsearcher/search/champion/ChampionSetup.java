package com.lolsearcher.search.champion;

import java.util.stream.Stream;

import static com.lolsearcher.search.champion.PositionState.TOP;
import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;

public class ChampionSetup {

    static Stream<ChampionsRequest> getValidChampionsRequest() {

        return Stream.of(
                ChampionsRequest.builder()
                        .position(TOP)
                        .gameVersion(CURRENT_GAME_VERSION)
                        .build()
        );
    }

    static Stream<ChampionDetailsRequest> getValidChampionDetailsRequest() {

        return Stream.of(
                ChampionDetailsRequest.builder()
                        .championId(1)
                        .gameVersion(CURRENT_GAME_VERSION)
                        .build()
        );
    }
}
