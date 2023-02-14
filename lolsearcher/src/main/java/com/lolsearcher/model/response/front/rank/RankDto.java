package com.lolsearcher.model.response.front.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class RankDto {
	private final String summonerId;
	private final int seasonId;
	private final String queueType;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
}
