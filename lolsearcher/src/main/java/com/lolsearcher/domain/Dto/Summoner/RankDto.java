package com.lolsearcher.domain.Dto.Summoner;

import com.lolsearcher.domain.entity.Rank;

public class RankDto {

	private String queuetype;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
	
	public RankDto() {
		
	}
	
	public RankDto(Rank r) {
		this.queuetype = r.getCk().getQueueType();
		this.leagueId = r.getLeagueId();
		this.tier = r.getTier();
		this.rank = r.getRank();
		this.leaguePoints = r.getLeaguePoints();
		this.wins = r.getWins();
		this.losses = r.getLosses();
	}

	public String getQueuetype() {
		return queuetype;
	}

	public void setQueuetype(String queuetype) {
		this.queuetype = queuetype;
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

}
