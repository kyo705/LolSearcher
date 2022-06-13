package com.lolsearcher.domain.entity.ingame;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.lolsearcher.domain.Dto.ingame.BannedChampionDto;

@Entity
public class BannedChampion {
	
	@EmbeddedId
	private BannedChampionCompKey ck;
	
	private long championId;
	
	private long teamId;
	
	@MapsId("gameId") //ck의 필드값
	@ManyToOne
	@JoinColumn(name = "gameId") //One table의 조인되는 컬럼명
	private InGame ingame;
	
	public BannedChampion() {}

	public void changeDtoToEntity(BannedChampionDto bannedchampDto, long gameId) {
		this.ck = new BannedChampionCompKey(gameId, bannedchampDto.getPickTurn());
		this.championId = bannedchampDto.getChampionId();
		this.teamId = bannedchampDto.getTeamId();
	}

	public BannedChampionCompKey getCk() {
		return ck;
	}

	public void setCk(BannedChampionCompKey ck) {
		this.ck = ck;
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

	public InGame getIngame() {
		return ingame;
	}

	public void setIngame(InGame ingame) {
		this.ingame = ingame;
	}
}
