package com.lolsearcher.domain.Dto.championstatic;

import com.lolsearcher.domain.entity.championstatic.item.ChampItem;

public class ChampItemDto {
	
	private String championId;
	private int seasonId;
	private int itemId;
	private long wins;
	private long losses;
	
	public ChampItemDto() {}
	
	public ChampItemDto(ChampItem champItem) {
		this.championId = champItem.getCk().getChampionId();
		this.seasonId = champItem.getCk().getSeasonId();
		this.itemId = champItem.getCk().getItemId();
		this.wins = champItem.getWins();
		this.losses = champItem.getLosses();
	}

	public String getChampionId() {
		return championId;
	}

	public void setChampionId(String championId) {
		this.championId = championId;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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
