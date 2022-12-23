package com.lolsearcher.model.entity.summoner;

import javax.persistence.*;

import lombok.*;


@Builder
@Data
@Entity
@Table(indexes = {@Index(columnList = "ID"), @Index(columnList = "name")})
public class Summoner {
	@Id
	@Column(name = "PRIMARY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "ID")
	private String summonerId;
	private String accountId;
	private String puuid;
	private String name;
	private String lastMatchId;
	private int profileIconId;
	private long revisionDate;
	private long summonerLevel;
	private long lastRenewTimeStamp;
	private long lastInGameSearchTimeStamp;
}
