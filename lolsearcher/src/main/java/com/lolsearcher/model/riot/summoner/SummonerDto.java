package com.lolsearcher.model.riot.summoner;

import lombok.Data;

@Data
public class SummonerDto {
    private String accountId;
    private int profileIconId;
    private long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private long summonerLevel;
}
