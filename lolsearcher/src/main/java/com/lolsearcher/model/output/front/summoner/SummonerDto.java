package com.lolsearcher.model.output.front.summoner;

import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SummonerDto {

	//클라이언트에게 전달할 값
	private String name;
	private int profileIconId;
	private long summonerLevel;
	private long lastRenewTimeStamp;

	//프론트 서버에 전달할 값
	private String summonerId;
	private String puuId;
	private boolean renewed; /* 해당 값이 true면 갱신되었다는 의미 => 해당 값으로 이후 rank, match 갱신할지 판단 */
}
