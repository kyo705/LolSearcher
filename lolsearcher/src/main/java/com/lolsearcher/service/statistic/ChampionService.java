package com.lolsearcher.service.statistic;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.model.dto.championstatic.ChampEnemyDto;
import com.lolsearcher.model.dto.championstatic.ChampItemDto;
import com.lolsearcher.model.dto.championstatic.ChampPositionDto;
import com.lolsearcher.model.dto.championstatic.TotalChampDto;
import com.lolsearcher.model.entity.championstatic.position.ChampPosition;
import com.lolsearcher.model.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.model.entity.championstatic.item.ChampItem;
import com.lolsearcher.repository.champion.ChampionReository;

@RequiredArgsConstructor
@Transactional
@Service
public class ChampionService {

	private final ChampionReository championRepository;
	
	public List<ChampPositionDto> getChampions(String position) {
		List<ChampPositionDto> champDtoList = new ArrayList<>();
		
		List<ChampPosition> champList = championRepository.findChampPositions(position);
		
		for(ChampPosition champ : champList) {
			champDtoList.add(new ChampPositionDto(champ));
		}
		
		return champDtoList;
	}

	public TotalChampDto getChampionDetail(String champion) {
		TotalChampDto totalChampDto = new TotalChampDto();

		List<ChampItem> champItems = championRepository.findChampItems(champion);
		totalChampDto.setChampItems(getChampItemDtos(champItems));

		List<ChampEnemy> champEnemies = championRepository.findChampEnemies(champion);
		totalChampDto.setChampEnemies(getChampEnemyDtos(champEnemies));
		
		return totalChampDto;
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
