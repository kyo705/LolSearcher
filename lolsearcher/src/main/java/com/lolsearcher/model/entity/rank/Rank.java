package com.lolsearcher.model.entity.rank;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.lolsearcher.model.dto.rank.RankDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ranks")
public class Rank {
	@EmbeddedId
	private RankCompKey ck;
	private String leagueId;
	private String tier;
	private String rank;
	private int leaguePoints;
	private int wins;
	private int losses;
	
	public Rank(RankDto rankDto) {
		
		this.ck = new RankCompKey(
				rankDto.getSummonerId(),
				rankDto.getQueueType(),
				rankDto.getSeasonId()
				);
		
		this.leagueId = rankDto.getLeagueId();
		this.tier = rankDto.getTier();
		this.rank = rankDto.getRank();
		this.leaguePoints = rankDto.getLeaguePoints();
		this.wins = rankDto.getWins();
		this.losses = rankDto.getLosses();
	}
}

