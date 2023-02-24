package com.lolsearcher.integration.controller.search.mostchamp;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.request.search.mostchamp.RequestMostChampDto;
import com.lolsearcher.model.response.front.search.mostchamp.ResponseMostChampDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class MostChampControllerTestSetup {

    protected static RequestMostChampDto getValidRequest() {

        return RequestMostChampDto.builder()
                .summonerId("summonerId1")
                .queueId(-1)
                .seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
                .build();
    }

    protected static List<ResponseMostChampDto> getMostChamps() {

        return List.of(
                ResponseMostChampDto.builder()
                        .championId(1)
                        .totalGameCount(50)
                        .totalWinCount(20)
                        .build(),

                ResponseMostChampDto.builder()
                        .championId(2)
                        .totalGameCount(30)
                        .totalWinCount(10)
                        .build(),

                ResponseMostChampDto.builder()
                        .championId(3)
                        .totalGameCount(20)
                        .totalWinCount(10)
                        .build()
        );
    }

    protected static Stream<Arguments> getInvalidRequest() {

        return Stream.of(
                Arguments.of(
                        RequestMostChampDto.builder()
                                .summonerId("") //Invalid
                                .queueId(-1)
                                .seasonId(1)
                                .build()
                ),
                Arguments.of(
                        RequestMostChampDto.builder()
                                .summonerId(null) //Invalid
                                .queueId(-1)
                                .seasonId(1)
                                .build()
                ),
                Arguments.of(
                        RequestMostChampDto.builder()
                                .summonerId("summoner1")
                                .queueId(-2) //Invalid
                                .seasonId(1)
                                .build()
                ),
                Arguments.of(
                        RequestMostChampDto.builder()
                                .summonerId("")
                                .queueId(-1)
                                .seasonId(0) //Invalid
                                .build()
                )
        );
    }
}
