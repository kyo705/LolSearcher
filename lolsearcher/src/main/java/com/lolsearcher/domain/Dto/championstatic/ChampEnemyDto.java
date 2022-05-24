package com.lolsearcher.domain.Dto.championstatic;

import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemy;

public class ChampEnemyDto {
	
	private String champinId;
	private int seasonId;
	private String enemychampionId;
	private long wins;
	private long losses;
	
	public ChampEnemyDto() {}
	
	public ChampEnemyDto(ChampEnemy champEnemy) {
		this.champinId = champEnemy.getCk().getChampionId();
		this.seasonId = champEnemy.getCk().getSeasonId();
		this.enemychampionId = champEnemy.getCk().getEnemychampionId();
		this.wins = champEnemy.getWins();
		this.losses = champEnemy.getLosses();
	}

	public String getChampinId() {
		return champinId;
	}

	public void setChampinId(String champinId) {
		this.champinId = champinId;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public String getEnemychampionId() {
		return enemychampionId;
	}

	public void setEnemychampionId(String enemychampionId) {
		this.enemychampionId = enemychampionId;
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
