package com.lolsearcher.service.openapi;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.factory.OpenApiResponseDtoFactory;
import com.lolsearcher.model.response.front.search.match.MatchDto;
import com.lolsearcher.model.response.openapi.OpenApiRankDto;
import com.lolsearcher.model.response.openapi.OpenApiSummonerDto;
import com.lolsearcher.repository.openapi.RestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestApiService {


	private final RestRepository restRepository;

	@JpaTransactional
	public OpenApiSummonerDto getSummonerById(String id) throws NoResultException {
		Summoner summoner = restRepository.getSummonerById(id);
		
		return OpenApiResponseDtoFactory.getOpenApiSummonerDtoFromEntity(summoner);
	}

	@JpaTransactional
	public OpenApiSummonerDto getSummonerByName(String name) throws NoResultException {
		Summoner summoner = restRepository.getSummonerByName(name);

		return OpenApiResponseDtoFactory.getOpenApiSummonerDtoFromEntity(summoner);
	}

	@JpaTransactional
	public OpenApiRankDto getRankById(String id, String type, int season) {
		Rank rank = restRepository.getRank(id, type, season);

		return OpenApiResponseDtoFactory.getOpenApiRankDtoFromEntity(rank);
	}



	@JpaTransactional
	public List<OpenApiRankDto> getRanksById(String id, int season) {
		List<Rank> ranks = restRepository.getRanks(id, season);

		return ranks.stream()
				.map(OpenApiResponseDtoFactory::getOpenApiRankDtoFromEntity)
				.collect(Collectors.toList());
	}

	@JpaTransactional
	public List<String> getMatchIds(String summonerId, int start, int count) {

		return restRepository.getMatchIds(summonerId, start, count);
	}

	@JpaTransactional
	public MatchDto getMatch(String matchId) {
		Match match = restRepository.getMatch(matchId);

		return OpenApiResponseDtoFactory.getOpenApiMatchDtoFromEntity(match);
	}

}
