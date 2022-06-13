package com.lolsearcher.domain.Dto.ingame;

import com.lolsearcher.domain.entity.ingame.BannedChampion;

public class BannedChampionDto {

	private int pickTurn;
	private long championId;
	private long teamId;
	
	public BannedChampionDto() {}

	public BannedChampionDto(BannedChampion bannedChampion) {
		this.pickTurn = bannedChampion.getCk().getPickTurn();
		this.championId = bannedChampion.getChampionId();
		this.teamId = bannedChampion.getTeamId();
	}

	public int getPickTurn() {
		return pickTurn;
	}

	public void setPickTurn(int pickTurn) {
		this.pickTurn = pickTurn;
	}

	public long getChampionId() {
		return championId;
	}

	public void setChampionId(long championId) {
		this.championId = championId;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	
}
