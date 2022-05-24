package com.lolsearcher.domain.entity.rank;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.lolsearcher.domain.Dto.summoner.RankDto;


@Entity
@Table(name = "ranks")
public class Rank {

	@EmbeddedId
	private RankCompKey ck;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
	
	public Rank() {
		
	}
	
	public Rank(RankDto rankDto) {
		
		this.ck = new RankCompKey(
				rankDto.getSummonerId(),
				rankDto.getQueueType(),
				rankDto.getSeasonId()
				);
		
		this.leagueId = rankDto.getLeagueId();
		this.tier = rankDto.getTier();
		this.rank = rankDto.getRank();
		this.leaguePoints = rankDto.getLeaguePoints();
		this.wins = rankDto.getWins();
		this.losses = rankDto.getLosses();
	}
	
	public RankCompKey getCk() {
		return ck;
	}
	public void setCk(RankCompKey ck) {
		this.ck = ck;
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

