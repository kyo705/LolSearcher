package com.lolsearcher.model.entity.ingame;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import com.lolsearcher.model.dto.ingame.BannedChampionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
@Entity
public class BannedChampion {
	@EmbeddedId
	private BannedChampionCompKey ck;
	
	private long championId;
	
	private long teamId;
	
	@MapsId("gameId") //ck의 필드값
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gameId") //One table의 조인되는 컬럼명
	private InGame inGame;

	public BannedChampion(BannedChampionDto bannedChampDto, long gameId) {
		this.ck = new BannedChampionCompKey(gameId, bannedChampDto.getPickTurn());
		this.championId = bannedChampDto.getChampionId();
		this.teamId = bannedChampDto.getTeamId();
	}

	public void setInGame(InGame inGame) {
		if(this.inGame != null) {
			this.inGame.getBannedChampions().remove(this);
		}
		this.inGame = inGame;
		inGame.addBannedChampion(this);
	}
}
