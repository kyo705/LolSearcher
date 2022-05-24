package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.lolsearcher.domain.Dto.championstatic.ChampEnemyDto;
import com.lolsearcher.domain.Dto.championstatic.ChampItemDto;
import com.lolsearcher.domain.Dto.championstatic.ChampionDto;
import com.lolsearcher.domain.Dto.championstatic.TotalChampDto;
import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.domain.entity.championstatic.item.ChampItem;
import com.lolsearcher.repository.ChampionRepository.ChampionReository;

@Transactional
@Service
public class ChampionService {

	private final ChampionReository championRepository;
	
	public ChampionService(ChampionReository championRepository) {
		this.championRepository = championRepository;
	}
	
	public List<ChampionDto> getChampions(String position) {
		List<ChampionDto> champDtoList = new ArrayList<>();
		
		List<Champion> champList = championRepository.findChamps(position);
		
		for(Champion champ : champList) {
			champDtoList.add(new ChampionDto(champ));
		}
		
		return champDtoList;
	}

	public TotalChampDto getChampionDetail(String champion) {
		TotalChampDto totalChampDto = new TotalChampDto();
		
		List<ChampItem> champItems = championRepository.findChampItems(champion);
		for(ChampItem champItem : champItems) {
			totalChampDto.addChampItem(new ChampItemDto(champItem));
		}
		List<ChampEnemy> champEnemys = championRepository.findChampEnemys(champion);
		for(ChampEnemy champEnemy : champEnemys) {
			totalChampDto.addChampEnemy(new ChampEnemyDto(champEnemy));
		}
		
		return totalChampDto;
	}
}
