package com.lolsearcher.search.mostchamp;

import java.util.stream.Stream;

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
}
