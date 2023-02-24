package com.lolsearcher.integration.controller.search.match;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.request.search.match.RequestMatchDto;
import com.lolsearcher.model.response.front.search.match.MatchDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class MatchControllerTestSetup {
    protected static RequestMatchDto getValidRequest() {

        return RequestMatchDto.builder()
                .summonerId("summonerId1")
                .championId(-1)
                .queueId(-1)
                .count(LolSearcherConstants.MATCH_DEFAULT_COUNT)
                .build();
    }

    protected static Stream<Arguments> getInvalidRequest() {

        return Stream.of(
                Arguments.of(
                        RequestMatchDto.builder()
                                .summonerId("") //invalid
                                .championId(-1)
                                .queueId(-1)
                                .count(20)
                                .build()
                ),
                Arguments.of(
                        RequestMatchDto.builder()
                                .summonerId(null) //invalid
                                .championId(-1)
                                .queueId(-1)
                                .count(20)
                                .build()
                ),
                Arguments.of(
                        RequestMatchDto.builder()
                                .summonerId("summonerId1")
                                .championId(-2) //invalid
                                .queueId(-1)
                                .count(20)
                                .build()
                ),
                Arguments.of(
                        RequestMatchDto.builder()
                                .summonerId("summonerId1")
                                .championId(-1)
                                .queueId(-2) //invalid
                                .count(20)
                                .build()
                ),
                Arguments.of(
                        RequestMatchDto.builder()
                                .summonerId("summonerId1")
                                .championId(-1)
                                .queueId(-1)
                                .count(-1) //invalid
                                .build()
                )
        );
    }

    protected static List<MatchDto> getMatchDtoList(RequestMatchDto request) {

        MatchDto matchDto = new MatchDto();
        matchDto.setQueueId(request.getQueueId());
        matchDto.setSeasonId(LolSearcherConstants.CURRENT_SEASON_ID);

        return List.of(matchDto);
    }


}
