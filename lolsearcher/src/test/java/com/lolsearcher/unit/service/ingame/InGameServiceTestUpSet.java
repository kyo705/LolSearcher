package com.lolsearcher.unit.service.ingame;

import com.lolsearcher.model.entity.ingame.InGame;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class InGameServiceTestUpSet {
    protected static Stream<Arguments> getOldInGameForMoreDataParam(){
        return Stream.of(
                Arguments.arguments(List.of(new InGame(), new InGame())),
                Arguments.arguments(List.of(new InGame(), new InGame(), new InGame()))
        );
    }

    protected static Stream<Arguments> removeDirtyInGameParam(){
        String summonerId = "summonerId";
        long inGameId = 100L;
        return Stream.of(
                Arguments.arguments(
                        summonerId,
                        inGameId,
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(2L).build(),
                                InGame.builder().gameId(3L).build(),
                                InGame.builder().gameId(4L).build()
                        ),
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(3L).build()
                        )
                ),
                Arguments.arguments(
                        summonerId,
                        inGameId,
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(2L).build(),
                                InGame.builder().gameId(3L).build(),
                                InGame.builder().gameId(4L).build()
                        ),
                        List.of()
                ),
                Arguments.arguments(
                        summonerId,
                        inGameId,
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(2L).build(),
                                InGame.builder().gameId(100L).build(),
                                InGame.builder().gameId(3L).build()
                        ),
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(3L).build()
                        )
                ),
                Arguments.arguments(
                        summonerId,
                        inGameId,
                        List.of(
                                InGame.builder().gameId(1L).build(),
                                InGame.builder().gameId(2L).build(),
                                InGame.builder().gameId(100L).build(),
                                InGame.builder().gameId(3L).build()
                        ),
                        List.of()
                ),
                Arguments.arguments(
                        summonerId,
                        inGameId,
                        List.of(),
                        List.of()
                )
        );
    }
}
