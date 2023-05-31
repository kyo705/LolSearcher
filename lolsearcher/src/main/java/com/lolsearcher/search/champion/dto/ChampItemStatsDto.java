package com.lolsearcher.search.champion.dto;

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
	private long bans;
}
