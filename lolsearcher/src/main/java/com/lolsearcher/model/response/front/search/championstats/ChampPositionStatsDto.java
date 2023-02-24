package com.lolsearcher.model.response.front.search.championstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ChampPositionStatsDto {

	private int championId;
	private int positionId;
	private String gameVersion;
	private long wins;
	private long losses;
	private long bans;
}
