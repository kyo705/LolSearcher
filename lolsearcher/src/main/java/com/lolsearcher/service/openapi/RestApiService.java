package com.lolsearcher.service.openapi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.rank.RankDto;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.repository.restapi.RestRepository;

@RequiredArgsConstructor
@Service
public class RestApiService {


	private final RestRepository restRepository;

	@JpaTransactional
	public SummonerDto getSummonerById(String id) throws NoResultException {
		Summoner summoner = restRepository.getSummonerById(id);
		
		return new SummonerDto(summoner);
	}

	@JpaTransactional
	public SummonerDto getSummonerByName(String name) throws NoResultException {
		Summoner summoner = restRepository.getSummonerByName(name);
		
		return new SummonerDto(summoner);
	}

	@JpaTransactional
	public RankDto getRankById(String id, String type, int season) {
		Rank rank = restRepository.getRank(id, type, season);
		
		return new RankDto(rank);
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

	@JpaTransactional
	public void setMatches(List<Match> matches) {
		for(Match match : matches) {
			if(restRepository.getMatch(match.getMatchId())==null) {
				restRepository.setMatch(match);
			}
		}
	}

	@JpaTransactional
	public void setOneMatch(Match match) {
		if(restRepository.getMatch(match.getMatchId())==null) {
			restRepository.setMatch(match);
		}
	}

}
