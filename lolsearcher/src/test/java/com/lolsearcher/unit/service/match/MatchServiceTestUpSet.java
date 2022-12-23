package com.lolsearcher.unit.service.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.lolsearcher.model.dto.parameter.MatchParam;
import org.junit.jupiter.params.provider.Arguments;

import com.lolsearcher.model.dto.match.SuccessMatchesAndFailMatchIds;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MatchServiceTestUpSet {

	protected static Summoner getSummoner(String summonerId) {
		return Summoner.builder()
				.name("닉네임" + summonerId)
				.summonerId(summonerId)
				.lastMatchId("matchid1")
				.puuid("puuid" + summonerId)
				.build();
	}

	protected static List<String> getMatchIds(int start, int end) {
		List<String> matchIds = new ArrayList<>();
		for(int i = start; i <= end; i++) {
			matchIds.add("matchId"+i);
		}
		return matchIds;
	}

	protected static SuccessMatchesAndFailMatchIds getSuccessMatchesAndFailMatchIds(List<String> allMatchIds, int limitedCount) {
		SuccessMatchesAndFailMatchIds successMatchesAndFailMatchIds = new SuccessMatchesAndFailMatchIds();

		List<Match> matches = new ArrayList<>(allMatchIds.size());
		for(int i=0;i<limitedCount;i++){
			String matchId = allMatchIds.get(i);
			matches.add(generateMatch(matchId));
		}
		List<String> failMatchIds = allMatchIds.subList(limitedCount, allMatchIds.size());

		successMatchesAndFailMatchIds.setSuccessMatches(matches);
		successMatchesAndFailMatchIds.setFailMatchIds(failMatchIds);
		
		return successMatchesAndFailMatchIds;
	}
	
	protected static Map<String, Match> getExistedMatches(List<Integer> mids) {
		Map<String, Match> existedMatches = new HashMap<>();
		for(int mid : mids) {
			Match match = new Match();
			match.setMatchId("matchId"+mid);

			existedMatches.put("matchId"+mid, match);
		}
		return existedMatches;
	}

	private static Match generateMatch(String matchId){
		Match match = new Match();
		match.setMatchId(matchId);

		return match;
	}

	protected static List<String> getNewMatchIds(List<String> allMatchIds, Map<String, Match> existedMatches) {
		List<String> newMatchIds = new ArrayList<>();
		for(String matchId : allMatchIds){
			if(existedMatches.containsKey(matchId)){
				continue;
			}
			newMatchIds.add(matchId);
		}
		return newMatchIds;
	}

	protected static List<Match> getDBMatches(MatchParam matchParam) {
		return List.of(new Match(), new Match(), new Match());
	}

	protected static Stream<Arguments> getMatchIdsParameter() {
		return Stream.of(
				arguments(0, 10, List.of(3,4,5), 5),
				arguments(0, 10, List.of(3,4,5), 0),
				arguments(0, 20, List.of(3,4,15), 10)
		);
	}

	protected static Stream<Arguments> getMatchParameter(){
		return Stream.of(
				arguments(
						MatchParam.builder()
								.summonerId("summonerId1")
								.gameType(420)
								.champion("all")
								.count(10)
								.build()
				),
				arguments(
						MatchParam.builder()
								.summonerId("summonerId1")
								.gameType(-1)
								.champion("all")
								.count(5)
								.build()
				)
		);
	}
}
