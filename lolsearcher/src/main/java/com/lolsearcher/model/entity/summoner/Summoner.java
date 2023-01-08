package com.lolsearcher.model.entity.summoner;

import javax.persistence.*;

import lombok.*;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "summoner_id"), @Index(columnList = "summoner_name")})
public class Summoner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String summonerId;
	private String accountId;
	private String puuid;
	private String summonerName;
	private String lastMatchId;
	private long revisionDate;
	private int profileIconId;
	private long summonerLevel;
	private long lastRenewTimeStamp;
}
