package com.lolsearcher.model.entity.summoner;

import javax.persistence.*;

import lombok.*;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "ID"), @Index(columnList = "summonerName")})
public class Summoner {
	@Id
	@Column(name = "PRIMARY_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "ID")
	private String summonerId;
	private String accountId;
	private String puuid;
	private String summonerName;
	private String lastMatchId;
	private long revisionDate;
	private int profileIconId;
	private long summonerLevel;
	private long lastRenewTimeStamp;
	private long lastInGameSearchTimeStamp;
}
