package com.lolsearcher.model.response.openapi;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenApiSummonerDto {

    private final String summonerId;
    private final String puuId;
    private final String name;
    private final int profileIconId;
    private final long summonerLevel;
}
