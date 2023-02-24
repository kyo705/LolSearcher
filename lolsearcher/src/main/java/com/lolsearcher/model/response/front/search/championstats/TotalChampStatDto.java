package com.lolsearcher.model.response.front.search.championstats;

import lombok.Data;

import java.util.List;

@Data
public class TotalChampStatDto {
	private List<ChampItemStatsDto> champItems;
	private List<ChampEnemyStatsDto> champEnemies;
}
