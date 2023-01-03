package com.lolsearcher.model.request.riot.rank;

import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class RankDto {

    @Value("${lolsearcher.season}")
    private int season;

    private String leagueId;
    private String summonerId;
    private String summonerName;
    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;
    private boolean hotStreak;
    private boolean veteran;
    private boolean freshBlood;
    private boolean inactive;

    public Rank changeToRank(){

        return Rank.builder()
                .ck(new RankCompKey(summonerId, queueType, season))
                .rank(rank)
                .tier(tier)
                .leagueId(leagueId)
                .leaguePoints(leaguePoints)
                .wins(wins)
                .losses(losses)
                .build();

    }
}
