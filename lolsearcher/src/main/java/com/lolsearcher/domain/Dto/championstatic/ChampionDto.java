package com.lolsearcher.domain.Dto.championstatic;

import com.lolsearcher.domain.entity.championstatic.Champion;

public class ChampionDto {

	private String championId;
	private String position;
	private int seasonId;
	private long wins;
	private long losses;
	
	public ChampionDto() {}
	
	public ChampionDto(Champion champ) {
		this.championId = champ.getCk().getChampionId();
		this.position = champ.getCk().getPosition();
		this.seasonId = champ.getCk().getSeasonId();
		this.wins = champ.getWins();
		this.losses = champ.getLosses();
	}

	public String getChampionId() {
		return championId;
	}

	public void setChampionId(String championId) {
		this.championId = championId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public long getWins() {
		return wins;
	}

	public void setWins(long wins) {
		this.wins = wins;
	}

	public long getLosses() {
		return losses;
	}

	public void setLosses(long losses) {
		this.losses = losses;
	}
}
