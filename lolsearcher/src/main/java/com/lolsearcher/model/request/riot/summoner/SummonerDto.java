package com.lolsearcher.model.request.riot.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;
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

    public Summoner changeToSummoner(){

        return Summoner.builder()
                .summonerId(id)
                .accountId(accountId)
                .puuid(puuid)
                .summonerName(name)
                .profileIconId(profileIconId)
                .summonerLevel(summonerLevel)
                .build();
    }
}
