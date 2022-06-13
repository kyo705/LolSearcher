package com.lolsearcher.domain.Dto.summoner;

import com.lolsearcher.domain.entity.summoner.rank.Rank;

public class RankDto {

	private String summonerId;
	private String queueType;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
	private int seasonId;
	
	public RankDto() {
		
	}
	
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
	
	public String getSummonerId() {
		return summonerId;
	}

	public void setSummonerId(String summonerId) {
		this.summonerId = summonerId;
	}
	
	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public int getLeaguePoints() {
		return leaguePoints;
	}

	public void setLeaguePoints(int leaguePoints) {
		this.leaguePoints = leaguePoints;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}


}
