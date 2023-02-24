package com.lolsearcher.model.response.front.search.mostchamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseMostChampDto {
	private int championId;
	private double avgCs;
	private double avgKill;
	private double avgDeath;
	private double avgAssist;
	private long totalGameCount;
	private long totalWinCount;
}