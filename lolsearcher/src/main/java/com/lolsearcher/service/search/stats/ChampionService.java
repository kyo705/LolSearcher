package com.lolsearcher.service.search.stats;

import com.lolsearcher.annotation.transaction.JpaTransactional;
import com.lolsearcher.exception.exception.common.NoExistDataException;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampEnemyStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampItemStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.TotalChampStatDto;
import com.lolsearcher.repository.search.champstats.ChampionRepository;
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
	public List<ChampPositionStatsDto> getAllChampPositionStats(RequestChampPositionStatsDto request) {

		int positionId = request.getPosition();
		String version = request.getGameVersion();

		List<ChampPositionStats> champPositionStats = redisChampionRepository.findAllChampPositionStats(positionId, version);

		if(champPositionStats.isEmpty()){
			String errorMessage = "포지션 별 챔피언 통계 데이터가 데이터베이스에 존재하지 않음";
			log.error(errorMessage);
			throw new NoExistDataException(errorMessage);
		}

		return champPositionStats
				.stream()
				.map(FrontServerResponseDtoFactory::getChampPositionStatsDto)
				.collect(Collectors.toList());
	}

	@JpaTransactional(readOnly = true)
	public TotalChampStatDto getChampDetailStats(RequestChampDetailStatsDto request) {

		int championId = request.getChampionId();
		String version = request.getGameVersion();

		List<ChampItemStatsDto> champItemStatsDto = getChampItemStats(championId, version);
		List<ChampEnemyStatsDto> champEnemyStatsDto = getChampEnemyStats(championId, version);

		TotalChampStatDto totalChampStatDto = new TotalChampStatDto();
		totalChampStatDto.setChampItems(champItemStatsDto);
		totalChampStatDto.setChampEnemies(champEnemyStatsDto);
		
		return totalChampStatDto;
	}

	@JpaTransactional(readOnly = true)
	public List<ChampItemStatsDto> getChampItemStats(int championId, String version){

		List<ChampItemStatsDto> champItemStatsDto = redisChampionRepository.findChampItems(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampItemStatsDto)
				.collect(Collectors.toList());

		if(champItemStatsDto.isEmpty()){
			String errorMessage = "특정 챔피언의 아이템 통계 데이터가 데이터베이스에 존재하지 않음";
			log.error(errorMessage);
			throw new NoExistDataException(errorMessage);
		}

		return champItemStatsDto;
	}

	@JpaTransactional(readOnly = true)
	public List<ChampEnemyStatsDto> getChampEnemyStats(int championId, String version){

		List<ChampEnemyStatsDto> champEnemyStatsDto = redisChampionRepository.findChampEnemies(championId, version)
				.stream()
				.map(FrontServerResponseDtoFactory::getChampEnemyStatsDto)
				.collect(Collectors.toList());

		if(champEnemyStatsDto.isEmpty()){
			String errorMessage = "특정 챔피언의 적 챔피언 통계 데이터가 데이터베이스에 존재하지 않음";
			log.error(errorMessage);
			throw new NoExistDataException(errorMessage);
		}

		return champEnemyStatsDto;
	}
}
