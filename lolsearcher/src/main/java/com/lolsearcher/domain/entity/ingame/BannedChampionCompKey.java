package com.lolsearcher.domain.entity.ingame;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class BannedChampionCompKey implements Serializable {

	private static final long serialVersionUID = 9021092936682719458L;
	
	private long gameId;
	
	private int pickTurn;
	
	public BannedChampionCompKey() {}

	public BannedChampionCompKey(long gameId, int pickTurn) {
		this.gameId = gameId;
		this.pickTurn = pickTurn;
	}

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public int getPickTurn() {
		return pickTurn;
	}

	public void setPickTurn(int pickTurn) {
		this.pickTurn = pickTurn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameId, pickTurn);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BannedChampionCompKey other = (BannedChampionCompKey) obj;
		return gameId == other.gameId && pickTurn == other.pickTurn;
	}
	
}
