package com.lolsearcher.service.search.stats;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.search.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.championstats.ChampEnemyStatsDto;
import com.lolsearcher.model.response.front.championstats.ChampItemStatsDto;
import com.lolsearcher.model.response.front.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.championstats.TotalChampStatDto;
import com.lolsearcher.repository.search.champstats.ChampionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChampionService {

	private final ChampionRepository championRepository;

	@JpaTransactional(readOnly = true)
	public List<ChampPositionStatsDto> getAllChampPositionStats(RequestChampPositionStatsDto request) {

		int positionId = request.getPosition();
		String version = request.getGameVersion();

		return championRepository.findAllChampPositionStats(positionId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampPositionStatsDto)
				.collect(Collectors.toList());
	}

	@JpaTransactional(readOnly = true)
	public TotalChampStatDto getChampDetailStats(RequestChampDetailStatsDto request) {

		int championId = request.getChampionId();
		String version = request.getGameVersion();

		List<ChampItemStatsDto> champItemStatsDto = championRepository.findChampItems(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampItemStatsDto)
				.collect(Collectors.toList());

		List<ChampEnemyStatsDto> champEnemyStatsDto = championRepository.findChampEnemies(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampEnemyStatsDto)
				.collect(Collectors.toList());

		TotalChampStatDto totalChampStatDto = new TotalChampStatDto();
		totalChampStatDto.setChampItems(champItemStatsDto);
		totalChampStatDto.setChampEnemies(champEnemyStatsDto);
		
		return totalChampStatDto;
	}
}
