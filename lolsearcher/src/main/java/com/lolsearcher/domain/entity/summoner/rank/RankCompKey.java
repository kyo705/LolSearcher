package com.lolsearcher.domain.entity.summoner.rank;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class RankCompKey implements Serializable {

	private static final long serialVersionUID = -8000650119610519628L;
	
	private String summonerId;
	private String queueType;
	private int seasonId;
	
	public RankCompKey(){
		
	}

	public RankCompKey(String summonerId, String queueType, int seasonId) {
		this.summonerId = summonerId;
		this.queueType = queueType;
		this.seasonId = seasonId;
	}

	public String getSummonerId() {
		return summonerId;
	}

	public void setSummonerId(String summonerId) {
		this.summonerId = summonerId;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(queueType, seasonId, summonerId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RankCompKey other = (RankCompKey) obj;
		return Objects.equals(queueType, other.queueType) && seasonId == other.seasonId
				&& Objects.equals(summonerId, other.summonerId);
	}
	
}
