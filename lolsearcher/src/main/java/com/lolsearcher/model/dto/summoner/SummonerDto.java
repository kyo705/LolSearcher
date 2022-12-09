package com.lolsearcher.model.dto.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;

import lombok.*;


@Builder
@AllArgsConstructor
@Data
public class SummonerDto {
	private final String summonerId;
	private final String puuId;
	private final String name;
	private int profileIconId;
	private long summonerLevel;
	private long lastRenewTimeStamp;
	
	public SummonerDto(Summoner summoner) {
		this.summonerId = summoner.getSummonerId();
		this.puuId = summoner.getPuuid();
		this.name = summoner.getName();
		this.profileIconId = summoner.getProfileIconId();
		this.summonerLevel = summoner.getSummonerLevel();
		this.lastRenewTimeStamp = summoner.getLastRenewTimeStamp();
	}
}
