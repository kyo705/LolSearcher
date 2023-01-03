package com.lolsearcher.model.response.front.rank;

import com.lolsearcher.model.entity.rank.Rank;

import lombok.*;


@Builder
@AllArgsConstructor
@Data
public class RankDto {
	private final String summonerId;
	private final String queueType;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
	private int seasonId;
	
	public RankDto(Rank r) {
		this.summonerId = r.getCk().getSummonerId();
		this.queueType = r.getCk().getQueueType();
		this.leagueId = r.getLeagueId();
		this.tier = r.getTier();
		this.rank = r.getRank();
		this.leaguePoints = r.getLeaguePoints();
		this.wins = r.getWins();
		this.losses = r.getLosses();
		this.seasonId = r.getCk().getSeasonId();
	}
}
