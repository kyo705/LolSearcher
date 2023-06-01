package com.lolsearcher.search.mostchamp;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.lolsearcher.search.mostchamp.MostChampIntegrationTest.*;
import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;

public class MostChampSetup {

    protected static Stream<MostChampRequest> validRequest() {

        return Stream.of(
                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(1)
                        .build()
        );
    }

    protected static Stream<MostChampRequest> invalidRequest() {

        return Stream.of(
                MostChampRequest.builder()
                        .summonerId("12345678901234567890123456789012345678901234567890123456789012345")   //invalid
                        .queueId(1)
                        .seasonId(22)
                        .count(20)
                        .build(),

                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(2)   //invalid
                        .seasonId(22)
                        .count(20)
                        .build(),

                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(1)
                        .seasonId(24) //invalid
                        .count(20)
                        .build(),

                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(1)
                        .seasonId(9) //invalid
                        .count(20)
                        .build(),

                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(1)
                        .seasonId(22)
                        .count(0)    //invalid
                        .build(),

                MostChampRequest.builder()
                        .summonerId("summonerId")
                        .queueId(1)
                        .seasonId(22)
                        .count(-1)    //invalid
                        .build()
        );
    }

    protected static Stream<Arguments> validMostChampParam() {

        return Stream.of(
                /* queueId, seasonId, count */
                Arguments.of(SOLO_RANK_QUEUE_ID, CURRENT_SEASON_ID, 1),
                Arguments.of(SOLO_RANK_QUEUE_ID, INITIAL_SEASON_ID, 2)
        );
    }

    protected static Stream<Arguments> invalidMostChampParam() {

        return Stream.of(
                /* queueId, seasonId, count */
                Arguments.of(2, CURRENT_SEASON_ID, 5),
                Arguments.of(SOLO_RANK_QUEUE_ID, CURRENT_SEASON_ID + 1, 5),
                Arguments.of(SOLO_RANK_QUEUE_ID, INITIAL_SEASON_ID - 1, 5),
                Arguments.of(SOLO_RANK_QUEUE_ID, CURRENT_SEASON_ID, 0),
                Arguments.of(SOLO_RANK_QUEUE_ID, CURRENT_SEASON_ID, -1)
        );
    }

    protected static Stream<Arguments> invalidDataBaseData() {

        return Stream.of(
                /* summonerId, queueId */
                Arguments.of("summonerId1", FLEX_RANK_QUEUE_ID),
                Arguments.of("summonerId2", CUSTOM_GAME_QUEUE_ID)
        );
    }
}
