package com.lolsearcher.model.response.front.championstatic;

import com.lolsearcher.model.entity.champion.position.ChampPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ChampPositionDto {
	private String championId;
	private String position;
	private int seasonId;
	private long wins;
	private long losses;
	
	public ChampPositionDto(ChampPosition champ) {
		this.championId = champ.getCk().getChampionId();
		this.position = champ.getCk().getPosition();
		this.seasonId = champ.getCk().getSeasonId();
		this.wins = champ.getWins();
		this.losses = champ.getLosses();
	}
}
