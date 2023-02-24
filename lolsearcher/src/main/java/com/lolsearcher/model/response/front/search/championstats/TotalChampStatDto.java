package com.lolsearcher.model.response.front.search.championstats;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TotalChampStatDto {
	private List<ChampItemStatsDto> champItems;
	private List<ChampEnemyStatsDto> champEnemies;
}
