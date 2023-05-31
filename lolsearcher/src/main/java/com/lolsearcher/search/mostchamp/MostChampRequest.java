package com.lolsearcher.search.mostchamp;

import com.lolsearcher.validation.Queue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

import static com.lolsearcher.search.mostchamp.MostChampConstant.MOST_CHAMP_DEFAULT_COUNT;
import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MIN_LENGTH;


@Builder
@Setter
@Getter
public class MostChampRequest {

    @NotBlank
    @Size(max = SUMMONER_ID_MAX_LENGTH, min = SUMMONER_ID_MIN_LENGTH)
    private String summonerId;

    @Queue
    private Integer queueId;

    @Min(INITIAL_SEASON_ID)
    @Max(CURRENT_SEASON_ID)
    private int seasonId;

    @Positive
    private int count;

    public MostChampRequest(){

        seasonId = CURRENT_SEASON_ID;
        count = MOST_CHAMP_DEFAULT_COUNT;
    }

    public MostChampRequest(String summonerId, Integer queueId, Integer seasonId, Integer count){

        this.summonerId = summonerId;
        this.queueId = queueId;
        this.seasonId = seasonId == null ? CURRENT_SEASON_ID : seasonId;
        this.count = count == null ? MOST_CHAMP_DEFAULT_COUNT : count;
    }
}
