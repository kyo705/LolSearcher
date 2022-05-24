package com.lolsearcher.domain.Dto.championstatic;

import java.util.ArrayList;
import java.util.List;

public class TotalChampDto {

	private List<ChampItemDto> champItems = new ArrayList<>();
	
	private List<ChampEnemyDto> champEnemys = new ArrayList<>();

	public void addChampItem(ChampItemDto champItemDto) {
		champItems.add(champItemDto);
	}

	public void addChampEnemy(ChampEnemyDto champEnemyDto) {
		champEnemys.add(champEnemyDto);
	}
	
	public void removeChampItem(ChampItemDto champItemDto) {
		champItems.remove(champItemDto);
	}

	public void removeChampEnemy(ChampEnemyDto champEnemyDto) {
		champEnemys.remove(champEnemyDto);
	}

	public List<ChampItemDto> getChampItems() {
		return champItems;
	}

	public List<ChampEnemyDto> getChampEnemys() {
		return champEnemys;
	}
}
