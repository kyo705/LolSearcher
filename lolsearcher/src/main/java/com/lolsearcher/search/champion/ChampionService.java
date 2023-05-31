package com.lolsearcher.search.champion;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.search.champion.dto.ChampEnemyStatsDto;
import com.lolsearcher.search.champion.dto.ChampItemStatsDto;
import com.lolsearcher.search.champion.dto.ChampPositionStatsDto;
import com.lolsearcher.utils.factory.FrontServerResponseDtoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChampionService {

	private final ChampionRepository redisChampionRepository;

	@JpaTransactional(readOnly = true)
	public List<ChampPositionStatsDto> findAllByPosition(ChampionsRequest request) {

		int positionId = request.getPosition().getCode();
		String version = request.getGameVersion();

		return redisChampionRepository.findAll(positionId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampPositionStatsDto)
				.collect(Collectors.toList());
	}

	@JpaTransactional(readOnly = true)
	public List<ChampItemStatsDto> findItemStats(ChampionDetailsRequest request){

		int championId = request.getChampionId();
		String version = request.getGameVersion();

		return redisChampionRepository.findItemStats(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampItemStatsDto)
				.collect(Collectors.toList());
	}

	@JpaTransactional(readOnly = true)
	public List<ChampEnemyStatsDto> findEnemyStats(ChampionDetailsRequest request){

		int championId = request.getChampionId();
		String version = request.getGameVersion();

		return redisChampionRepository.findEnemyStats(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampEnemyStatsDto)
				.collect(Collectors.toList());
	}
}
