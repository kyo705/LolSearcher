package com.lolsearcher.search.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class RankDto {
	private final String summonerId;
	private final int seasonId;
	private final RankTypeState queueType;
	private String leagueId;
	private TierState tier;
	private RankState rank;
	private int leaguePoints;
	private long wins;
	private long losses;
}
