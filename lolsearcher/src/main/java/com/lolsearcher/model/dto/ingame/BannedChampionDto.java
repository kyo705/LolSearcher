package com.lolsearcher.model.dto.ingame;

import com.lolsearcher.model.entity.ingame.BannedChampion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class BannedChampionDto {

	private int pickTurn;
	private long championId;
	private long teamId;

	public BannedChampionDto(BannedChampion bannedChampion) {
		this.pickTurn = bannedChampion.getCk().getPickTurn();
		this.championId = bannedChampion.getChampionId();
		this.teamId = bannedChampion.getTeamId();
	}
}
