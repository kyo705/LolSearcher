package com.lolsearcher.unit.service.search.stats;

import com.lolsearcher.model.entity.champion.ChampEnemyStats;
import com.lolsearcher.model.entity.champion.ChampItemStats;
import com.lolsearcher.model.entity.champion.ChampPositionStats;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.TotalChampStatDto;
import com.lolsearcher.repository.search.champstats.JpaChampionRepository;
import com.lolsearcher.service.search.stats.ChampionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChampStatsServiceUnitTest {

	@Mock JpaChampionRepository championRepository;
	ChampionService championService;
	
	@BeforeEach
	void setup() {
		championService = new ChampionService(championRepository);
	}

	@DisplayName("특정 포지션에 해당하는 모든 챔피언의 전반적인 통계 데이터를 리턴한다.")
	@Test
	public void getAllChampPositionStats() {

		//given
		RequestChampPositionStatsDto request = ChampStatsServiceTestSetup.getRequestChampPositionStatsDto();
		List<ChampPositionStats> champPositions = ChampStatsServiceTestSetup.getAllChampStatsFromPosition(request);
		
		given(championRepository.findAllChampPositionStats(request.getPosition(), request.getGameVersion()))
				.willReturn(champPositions);
		
		//when
		List<ChampPositionStatsDto> champPositionStatsDtos = championService.getAllChampPositionStats(request);
		
		//then
		champPositionStatsDtos
				.forEach(stats -> {
					assertThat(stats.getPositionId()).isEqualTo(request.getPosition());
					assertThat(stats.getGameVersion()).isEqualTo(request.getGameVersion());
				});
	}

	@DisplayName("특정 챔피언에 대한 아이템 통계, 상대별 챔피언 통계 데이터가 리턴된다.")
	@Test
	public void getChampDetailStats() {
		
		//given
		RequestChampDetailStatsDto request = ChampStatsServiceTestSetup.getRequestChampDetailStatsDto();
		List<ChampItemStats> champItems = ChampStatsServiceTestSetup.getAllChampItemStats(request);
		List<ChampEnemyStats> champEnemies = ChampStatsServiceTestSetup.getAllChampEnemyStats(request);

		given(championRepository.findChampItems(request.getChampionId(), request.getGameVersion())).willReturn(champItems);
		given(championRepository.findChampEnemies(request.getChampionId(), request.getGameVersion())).willReturn(champEnemies);
		
		//when
		TotalChampStatDto totalChampStatDto = championService.getChampDetailStats(request);
		
		//then
		totalChampStatDto.getChampEnemies()
				.forEach(stats -> {
					assertThat(stats.getChampionId()).isEqualTo(request.getChampionId());
					assertThat(stats.getGameVersion()).isEqualTo(request.getGameVersion());
				});

		totalChampStatDto.getChampItems()
				.forEach(stats -> {
					assertThat(stats.getChampionId()).isEqualTo(request.getChampionId());
					assertThat(stats.getGameVersion()).isEqualTo(request.getGameVersion());
				});
	}
}
