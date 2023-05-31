package com.lolsearcher.search.summoner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SummonerDto {

	private String summonerId;
	private String puuId;
	private String name;
	private Integer profileIconId;
	private long summonerLevel;
	private LocalDateTime lastRenewTimeStamp;

}
