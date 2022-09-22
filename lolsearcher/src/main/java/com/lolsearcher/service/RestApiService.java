package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.repository.restapirepository.RestRepository;

@Service
@Transactional
public class RestApiService {

	private RestRepository restRepository;
	
	public RestApiService(RestRepository restRepository) {
		this.restRepository = restRepository;
	}

	public SummonerDto getSummonerById(String id) throws NoResultException {
		
		Summoner summoner = restRepository.getSummonerById(id);
		
		SummonerDto summonerDto = new SummonerDto(summoner);
		
		return summonerDto;
	}

	public SummonerDto getSummonerByName(String name) throws NoResultException {
		
		Summoner summoner = restRepository.getSummonerByName(name);
		
		SummonerDto summonerDto = new SummonerDto(summoner);
		
		return summonerDto;
	}

	public RankDto getRankById(String id, String type, int season) {
		Rank rank = restRepository.getRank(id, type, season);
		
		RankDto rankDto = new RankDto(rank);
		
		return rankDto;
	}

	public List<RankDto> getRanksById(String id, int season) {
		List<Rank> ranks = restRepository.getRanks(id, season);
		
		List<RankDto> ranksDto = new ArrayList<>();
		
		for(Rank rank : ranks) {
			ranksDto.add(new RankDto(rank));
		}
		
		return ranksDto;
	}

	public List<String> getMatchIds(String summonerId, int start, int count) {
		
		List<String> matchIds = restRepository.getMatchIds(summonerId, start, count);
		
		return matchIds;
	}

	public MatchDto getMatch(String matchId) {
		
		Match match = restRepository.getMatch(matchId);
		
		MatchDto matchDto = new MatchDto(match);
		
		return matchDto;
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void setMatches(List<Match> matches) {
		for(Match match : matches) {
			if(restRepository.getMatch(match.getMatchId())==null) {
				restRepository.setMatch(match);
			}
		}
	}

	public void setOneMatch(Match match) {
		if(restRepository.getMatch(match.getMatchId())==null) {
			restRepository.setMatch(match);
		}
	}

}
