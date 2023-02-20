package com.lolsearcher.service.search.match;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.search.RequestMatchDto;
import com.lolsearcher.model.response.front.match.MatchDto;
import com.lolsearcher.repository.search.match.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {

	private final MatchRepository matchRepository;

	@JpaTransactional(readOnly = true)
	public List<MatchDto> getDbMatches(RequestMatchDto request){

		List<Match> matches = matchRepository.findMatches(
				request.getSummonerId(), request.getQueueId(), request.getChampionId(), request.getCount()
		);

		return matches.stream()
				.map(FrontServerResponseDtoFactory::getResponseMatchDto)
				.collect(Collectors.toList());
	}

}
