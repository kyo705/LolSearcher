package com.lolsearcher.model.entity.rank;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Builder
@Data
@Entity
@Table(name = "ranks", indexes = {@Index(columnList = "summoner_id, season_id, queue_type", unique = true)})
public class Rank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String summonerId;
	private int seasonId;
	private String queueType;
	private String leagueId;
	private String tier; /* GOLD, SLIVER, BRONZE ... */
	private String rank; /* I, II, III ... */
	private int leaguePoints;
	private int wins;
	private int losses;

}

