package com.lolsearcher.model.response.front.championstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ChampItemStatsDto {

	private String gameVersion;
	private int championId;
	private int itemId;
	private long wins;
	private long losses;
}
