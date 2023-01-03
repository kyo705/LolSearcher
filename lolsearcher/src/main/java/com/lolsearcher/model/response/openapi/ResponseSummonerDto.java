package com.lolsearcher.model.response.openapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ResponseSummonerDto {

    private final String name;
    private int profileIconId;
    private long summonerLevel;
    private long lastRenewTimeStamp;
    private final String summonerId;
    private final String puuId;
}
