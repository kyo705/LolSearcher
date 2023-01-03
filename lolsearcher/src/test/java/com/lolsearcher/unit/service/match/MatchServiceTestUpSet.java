package com.lolsearcher.unit.service.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.front.RequestMatchDto;
import org.junit.jupiter.params.provider.Arguments;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.lolsearcher.constant.LolSearcherConstants.MATCH_DEFAULT_COUNT;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MatchServiceTestUpSet {

	protected static Summoner getSummoner(String summonerId) {
		return Summoner.builder()
				.summonerName("닉네임" + summonerId)
				.summonerId(summonerId)
				.lastMatchId("initMatchid")
				.puuid("puuid" + summonerId)
				.build();
	}

	protected static List<String> getMatchIds(int matchIdCount) {
		List<String> matchIds = new ArrayList<>();
		for(int i = 0; i < matchIdCount; i++) {
			matchIds.add("matchId"+i);
		}
		return matchIds;
	}

	protected static List<Match> getDBMatches(RequestMatchDto matchDto) {
		return List.of(new Match(), new Match(), new Match());
	}

	protected static Stream<Arguments> getMatchIdsParameter() {
		return Stream.of(
				arguments(MATCH_DEFAULT_COUNT+1),
				arguments(MATCH_DEFAULT_COUNT),
				arguments(MATCH_DEFAULT_COUNT-1),
				arguments(MATCH_DEFAULT_COUNT-5),
				arguments(MATCH_DEFAULT_COUNT+10)
		);
	}

	protected static Stream<Arguments> getMatchParameter(){
		return Stream.of(
				arguments(
						RequestMatchDto.builder()
								.summonerId("summonerId1")
								.queueId(420)
								.championId("all")
								.count(10)
								.build()
				),
				arguments(
						RequestMatchDto.builder()
								.summonerId("summonerId1")
								.queueId(-1)
								.championId("all")
								.count(5)
								.build()
				)
		);
	}

	protected static Mono<Match> getMatchMono(String matchId) {
		Match match = new Match();
		match.setMatchId(matchId);

		return Mono.just(match);
	}

	protected static RequestMatchDto getRequestMatchInfo() {

		return RequestMatchDto.builder()
				.summonerId("summonerId1")
				.championId("all")
				.queueId(-1)
				.count(20)
				.renew(true)
				.build();
    }
}
