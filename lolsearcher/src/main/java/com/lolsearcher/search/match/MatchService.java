package com.lolsearcher.search.match;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.search.match.dto.MatchDto;
import com.lolsearcher.search.summoner.SummonerService;
import com.lolsearcher.utils.ResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {

	private final SummonerService summonerService;
	private final MatchRepository matchRepository;

	@JpaTransactional(readOnly = true)
	public List<MatchDto> findMatches(MatchRequest request) {

		checkSummonerId(request.getSummonerId());

		return matchRepository
				.findMatches(
						request.getSummonerId(),
						request.getQueueId(),
						request.getChampionId(),
						request.getCount(),
						request.getOffset()
				)
				.stream()
				.map(ResponseDtoFactory::getResponseMatchDto)
				.collect(Collectors.toList());
	}

	private void checkSummonerId(String summonerId) {

		summonerService.findById(summonerId);
	}
}
