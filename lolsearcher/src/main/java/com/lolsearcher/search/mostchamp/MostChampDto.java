package com.lolsearcher.search.mostchamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MostChampDto {
	private String summonerId;
	private int championId;
	private int seasonId;
	private int queueId;
	private long totalKills;
	private long totalDeaths;
	private long totalAssists;
	private long totalMinionKills;
	private long totalGames;
	private long totalWins;
	private long totalLosses;
}