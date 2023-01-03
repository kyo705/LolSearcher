package com.lolsearcher.integration.cache;

import com.lolsearcher.constant.GameType;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.request.front.RequestMatchDto;
import com.lolsearcher.model.response.front.ingame.InGameDto;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.summoner.Summoner;
import reactor.core.publisher.Mono;

import java.util.List;

public class CacheTestUpSet {

    protected static InGameDto createInGameDto(String summonerId){
        return InGameDto
                .builder()
                .gameId(0)
                .gameMode("CLASSIC")
                .build();
    }

    protected static List<String> getMatchIds() {
        return List.of("matchId1" , "matchId2", "matchId3");
    }

    protected static Mono<Match> getMatchMono(String matchId) {

        Match match = new Match();
        match.setMatchId(matchId);

        return Mono.just(match);
    }

    protected static Summoner getSummoner(String summonerId) {

        return Summoner.builder()
                .summonerId(summonerId)
                .build();
    }

    protected static RequestMatchDto getRequestMatchDto(String summonerId) {

        return RequestMatchDto.builder()
                .summonerId(summonerId)
                .championId(LolSearcherConstants.ALL)
                .count(LolSearcherConstants.MATCH_DEFAULT_COUNT)
                .queueId(GameType.ALL_QUEUE_ID.getQueueId())
                .renew(true)
                .build();
    }
}
