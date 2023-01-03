package com.lolsearcher.service.openapi;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.response.front.match.MatchDto;
import com.lolsearcher.model.response.front.rank.RankDto;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.response.openapi.ResponseSummonerDto;
import com.lolsearcher.repository.restapi.RestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestApiService {


	private final RestRepository restRepository;

	@JpaTransactional
	public ResponseSummonerDto getSummonerById(String id) throws NoResultException {
		Summoner summoner = restRepository.getSummonerById(id);
		
		return changeSummonerDto(summoner);
	}

	@JpaTransactional
	public ResponseSummonerDto getSummonerByName(String name) throws NoResultException {
		Summoner summoner = restRepository.getSummonerByName(name);
		
		return changeSummonerDto(summoner);
	}

	@JpaTransactional
	public RankDto getRankById(String id, String type, int season) {
		Rank rank = restRepository.getRank(id, type, season);
		
		return chagneRankDto(rank);
	}



	@JpaTransactional
	public List<RankDto> getRanksById(String id, int season) {
		List<Rank> ranks = restRepository.getRanks(id, season);
		
		List<RankDto> ranksDto = new ArrayList<>();
		for(Rank rank : ranks) {
			ranksDto.add(new RankDto(rank));
		}
		return ranksDto;
	}

	@JpaTransactional
	public List<String> getMatchIds(String summonerId, int start, int count) {
		return restRepository.getMatchIds(summonerId, start, count);
	}

	@JpaTransactional
	public MatchDto getMatch(String matchId) {
		Match match = restRepository.getMatch(matchId);
		
		return new MatchDto(match);
	}

	private ResponseSummonerDto changeSummonerDto(Summoner summoner) {
		return ResponseSummonerDto.builder()
				.summonerId(summoner.getSummonerId())
				.puuId(summoner.getPuuid())
				.name(summoner.getSummonerName())
				.profileIconId(summoner.getProfileIconId())
				.summonerLevel(summoner.getSummonerLevel())
				.lastRenewTimeStamp(summoner.getLastRenewTimeStamp())
				.build();
	}

	private RankDto chagneRankDto(Rank rank) {
		return null;
	}

}
