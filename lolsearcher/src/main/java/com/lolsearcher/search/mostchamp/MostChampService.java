package com.lolsearcher.search.mostchamp;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.utils.factory.FrontServerResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MostChampService {

	private final MostChampRepository mostChampRepository;

	@JpaTransactional(readOnly = true)
	public List<MostChampDto> getMostChamps(MostChampRequest request) {

		String summonerId = request.getSummonerId();
		Integer queueId = request.getQueueId();
		int seasonId = request.getSeasonId();
		int count = request.getCount();

		return mostChampRepository
				.findMostChampions(summonerId, queueId, seasonId, count)
				.stream()
				.map(FrontServerResponseDtoFactory::getResponseMostChampDto)
				.collect(Collectors.toList());
	}
}
