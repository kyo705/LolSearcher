package com.lolsearcher.service.statistic;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.model.response.front.championstatic.ChampEnemyDto;
import com.lolsearcher.model.response.front.championstatic.ChampItemDto;
import com.lolsearcher.model.response.front.championstatic.ChampPositionDto;
import com.lolsearcher.model.response.front.championstatic.TotalChampStatDto;
import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import com.lolsearcher.model.entity.champion.item.ChampItem;
import com.lolsearcher.model.entity.champion.position.ChampPosition;
import com.lolsearcher.repository.champion.ChampionReository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChampionService {

	private final ChampionReository championRepository;

	@JpaTransactional
	public List<ChampPositionDto> getChampions(String position) {
		List<ChampPositionDto> champDtoList = new ArrayList<>();
		
		List<ChampPosition> champList = championRepository.findChampPositions(position);
		
		for(ChampPosition champ : champList) {
			champDtoList.add(new ChampPositionDto(champ));
		}
		
		return champDtoList;
	}

	@JpaTransactional
	public TotalChampStatDto getChampionDetail(String champion) {
		TotalChampStatDto totalChampStatDto = new TotalChampStatDto();

		List<ChampItem> champItems = championRepository.findChampItems(champion);
		totalChampStatDto.setChampItems(getChampItemDtos(champItems));

		List<ChampEnemy> champEnemies = championRepository.findChampEnemies(champion);
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
