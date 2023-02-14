package com.lolsearcher.model.response.openapi;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenApiRankDto {

    private final String summonerId;
    private final int seasonId;
    private final String queueType;
    private final String leagueId;
    private final String tier;
    private final String rank;
    private final int leaguePoints;
    private final int wins;
    private final int losses;
}
