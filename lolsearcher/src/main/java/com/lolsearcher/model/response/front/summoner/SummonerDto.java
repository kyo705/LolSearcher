package com.lolsearcher.model.response.front.summoner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SummonerDto {

	private String name;
	private int profileIconId;
	private long summonerLevel;
	private long lastRenewTimeStamp;
	private String summonerId;
	private String puuId;
}
