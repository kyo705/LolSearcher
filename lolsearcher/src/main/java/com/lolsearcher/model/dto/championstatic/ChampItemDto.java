package com.lolsearcher.model.dto.championstatic;

import com.lolsearcher.model.entity.champion.item.ChampItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ChampItemDto {
	private String championId;
	private int seasonId;
	private int itemId;
	private long wins;
	private long losses;
	
	public ChampItemDto(ChampItem champItem) {
		this.championId = champItem.getCk().getChampionId();
		this.seasonId = champItem.getCk().getSeasonId();
		this.itemId = champItem.getCk().getItemId();
		this.wins = champItem.getWins();
		this.losses = champItem.getLosses();
	}
}
