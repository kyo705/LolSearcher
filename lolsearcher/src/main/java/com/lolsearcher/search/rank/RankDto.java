package com.lolsearcher.search.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RankDto {
	private String summonerId;
	private int seasonId;
	private RankTypeState queueType;
	private String leagueId;
	private TierState tier;
	private RankState rank;
	private int leaguePoints;
	private long wins;
	private long losses;
}
