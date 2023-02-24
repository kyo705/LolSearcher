package com.lolsearcher.integration.controller.search.rank;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.response.front.search.rank.RankDto;

import java.util.Map;

public class RankControllerTestSetup {
    protected static Map<String, RankDto> getRankDto(String summonerId) {

        RankDto soloRankDto = RankDto.builder()
                .summonerId(summonerId)
                .queueType(LolSearcherConstants.SOLO_RANK)
                .seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
                .rank("GOLD")
                .build();

        RankDto flexRankDto = RankDto.builder()
                .summonerId(summonerId)
                .queueType(LolSearcherConstants.FLEX_RANK)
                .seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
                .rank("SILVER")
                .build();

        return Map.of(LolSearcherConstants.SOLO_RANK, soloRankDto,
                LolSearcherConstants.FLEX_RANK, flexRankDto);
    }
}
