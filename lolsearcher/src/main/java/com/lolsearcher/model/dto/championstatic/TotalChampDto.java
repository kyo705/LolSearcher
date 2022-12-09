package com.lolsearcher.model.dto.championstatic;

import lombok.Data;

import java.util.List;

@Data
public class TotalChampDto {
	private List<ChampItemDto> champItems;
	private List<ChampEnemyDto> champEnemies;
}
