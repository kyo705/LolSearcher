package com.lolsearcher.unit.service.search.stats;

import com.lolsearcher.search.champion.ChampionService;
import com.lolsearcher.search.champion.ChampionsRequest;
import com.lolsearcher.search.champion.JpaChampionRepository;
import com.lolsearcher.search.champion.dto.ChampPositionStatsDto;
import com.lolsearcher.search.champion.entity.ChampPositionStats;
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
		ChampionsRequest request = ChampStatsServiceTestSetup.getRequestChampPositionStatsDto();
		List<ChampPositionStats> champPositions = ChampStatsServiceTestSetup.getAllChampStatsFromPosition(request);
		
		given(championRepository.findAll(request.getPosition().getCode(), request.getGameVersion()))
				.willReturn(champPositions);
		
		//when
		List<ChampPositionStatsDto> champPositionStatsDtos = championService.findAllByPosition(request);
		
		//then
		champPositionStatsDtos
				.forEach(stats -> {
					assertThat(stats.getPositionId()).isEqualTo(request.getPosition());
					assertThat(stats.getGameVersion()).isEqualTo(request.getGameVersion());
				});
	}
/*
	@DisplayName("특정 챔피언에 대한 아이템 통계, 상대별 챔피언 통계 데이터가 리턴된다.")
	@Test
	public void getChampDetailStats() {
		
		//given
		ChampionDetailsRequest request = ChampStatsServiceTestSetup.getRequestChampDetailStatsDto();
		List<ChampItemStats> champItems = ChampStatsServiceTestSetup.getAllChampItemStats(request);
		List<ChampEnemyStats> champEnemies = ChampStatsServiceTestSetup.getAllChampEnemyStats(request);

		given(championRepository.findItemStats(request.getChampionId(), request.getGameVersion())).willReturn(champItems);
		given(championRepository.findEnemyStats(request.getChampionId(), request.getGameVersion())).willReturn(champEnemies);
		
		//when
		TotalChampStatDto totalChampStatDto = championService.findById(request);
		
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
	}*/
}
