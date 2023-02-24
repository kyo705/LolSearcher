package com.lolsearcher.unit.service.search.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.SummaryMember;
import com.lolsearcher.model.entity.match.Team;
import com.lolsearcher.model.request.search.match.RequestMatchDto;

import java.util.ArrayList;
import java.util.List;

public class MatchServiceTestSetup {

	protected static RequestMatchDto getRequestMatchDto() {

		return RequestMatchDto.builder()
				.summonerId("summonerId1")
				.championId(-1)
				.queueId(-1)
				.count(20)
				.build();
    }

	protected static List<Match> getDBMatches(RequestMatchDto request) throws IllegalAccessException {

		String summonerId = request.getSummonerId();
		int championId = request.getChampionId();
		int queueId = request.getQueueId();
		int count = request.getCount();

		List<Match> result = new ArrayList<>();
		for(int i=0;i<count;i++){
			Match match = new Match();
			match.setMatchId("matchId"+i);
			match.setQueueId(queueId);

			for(int j=0;j<2;j++){
				Team team = new Team();

				for(int k=0;k<5;k++){
					SummaryMember member = new SummaryMember();
					if(k==0 && j==0) {
						member.setSummonerId(summonerId);
					}else{
						member.setSummonerId("summonerId"+j+k);
					}
					member.setPickChampionId(championId);
					member.setTeam(team);
				}
				team.setMatch(match);
			}
			result.add(match);
		}
		return result;
	}
}
