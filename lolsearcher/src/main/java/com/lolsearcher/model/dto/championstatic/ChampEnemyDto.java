package com.lolsearcher.model.dto.championstatic;

import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ChampEnemyDto {
	
	private String championId;
	private String enemyChampionId;
	private int seasonId;
	private long wins;
	private long losses;
	
	public ChampEnemyDto(ChampEnemy champEnemy) {
		this.championId = champEnemy.getCk().getChampionId();
		this.seasonId = champEnemy.getCk().getSeasonId();
		this.enemyChampionId = champEnemy.getCk().getEnemyChampionId();
		this.wins = champEnemy.getWins();
		this.losses = champEnemy.getLosses();
	}
}
