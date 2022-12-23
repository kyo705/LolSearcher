package com.lolsearcher.model.entity.rank;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@Builder
@Data
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
}

