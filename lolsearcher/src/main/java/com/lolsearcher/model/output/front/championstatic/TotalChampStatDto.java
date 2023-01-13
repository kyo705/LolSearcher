package com.lolsearcher.model.output.front.championstatic;

import lombok.Data;

import java.util.List;

@Data
public class TotalChampStatDto {
	private List<ChampItemDto> champItems;
	private List<ChampEnemyDto> champEnemies;
}
