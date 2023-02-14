package com.lolsearcher.model.entity.summoner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "summonerId"), @Index(columnList = "summonerName")})
public class Summoner {

	@Id
	private Long id;
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
