package com.lolsearcher.service.statistic;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.output.front.championstatic.ChampEnemyDto;
import com.lolsearcher.model.output.front.championstatic.ChampItemDto;
import com.lolsearcher.model.output.front.championstatic.ChampPositionDto;
import com.lolsearcher.model.output.front.championstatic.TotalChampStatDto;
import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import com.lolsearcher.model.entity.champion.item.ChampItem;
import com.lolsearcher.model.entity.champion.position.ChampPosition;
import com.lolsearcher.repository.champion.ChampionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChampionService {

	private final ChampionRepository championRepository;

	@JpaTransactional
	public List<ChampPositionDto> getChampions(int positionId) {
		List<ChampPositionDto> champDtoList = new ArrayList<>();
		
		List<ChampPosition> champList = championRepository.findChampPositions(positionId);
		
		for(ChampPosition champ : champList) {
			champDtoList.add(new ChampPositionDto(champ));
		}
		
		return champDtoList;
	}

	@JpaTransactional
	public TotalChampStatDto getChampionDetail(int championId) {
		TotalChampStatDto totalChampStatDto = new TotalChampStatDto();

		List<ChampItem> champItems = championRepository.findChampItems(championId);
		totalChampStatDto.setChampItems(getChampItemDtos(champItems));

		List<ChampEnemy> champEnemies = championRepository.findChampEnemies(championId);
		totalChampStatDto.setChampEnemies(getChampEnemyDtos(champEnemies));
		
		return totalChampStatDto;
	}

	private List<ChampItemDto> getChampItemDtos(List<ChampItem> champItems) {
		List<ChampItemDto> champItemDtos = new ArrayList<>();

		for(ChampItem champItem : champItems) {
			champItemDtos.add(new ChampItemDto(champItem));
		}
		return champItemDtos;
	}

	private List<ChampEnemyDto> getChampEnemyDtos(List<ChampEnemy> champEnemies) {
		List<ChampEnemyDto> champEnemyDtos = new ArrayList<>();

		for(ChampEnemy champEnemy : champEnemies) {
			champEnemyDtos.add(new ChampEnemyDto(champEnemy));
		}
		return champEnemyDtos;
	}
}
