package com.lolsearcher.domain.entity.ingame;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class CurrentParticipantCompKey implements Serializable {

	private static final long serialVersionUID = -7262931192315016423L;

	private long gameId;
	
	private String summonerId;
	

	public CurrentParticipantCompKey() {}

	public CurrentParticipantCompKey(long gameId, String summonerId) {
		this.gameId = gameId;
		this.summonerId = summonerId;
	}

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public String getSummonerId() {
		return summonerId;
	}

	public void setSummonerId(String summonerId) {
		this.summonerId = summonerId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameId, summonerId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrentParticipantCompKey other = (CurrentParticipantCompKey) obj;
		return gameId == other.gameId && Objects.equals(summonerId, other.summonerId);
	}

	
	
}
