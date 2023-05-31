package com.lolsearcher.search.match;

import com.lolsearcher.validation.Champion;
import com.lolsearcher.validation.Queue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static com.lolsearcher.search.match.MatchConstant.DEFAULT_MATCH_COUNT;
import static com.lolsearcher.search.match.MatchConstant.DEFAULT_OFFSET;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MIN_LENGTH;

@Builder
@Getter
@Setter
public class MatchRequest {

    @NotBlank @Size(max = SUMMONER_ID_MAX_LENGTH, min = SUMMONER_ID_MIN_LENGTH)
    private String summonerId;
    @Champion
    private Integer championId;
    @Queue
    private Integer queueId;
    @PositiveOrZero
    private Integer count;
    @PositiveOrZero
    private Integer offset;

    public MatchRequest() {

        count = DEFAULT_MATCH_COUNT;
        offset = DEFAULT_OFFSET;
    }

    public MatchRequest(String summonerId, Integer championId, Integer queueId, Integer count, Integer offset) {

        this.summonerId = summonerId;
        this.championId = championId;
        this.queueId = queueId;
        this.count = count == null ? DEFAULT_MATCH_COUNT : count;
        this.offset = offset == null ? DEFAULT_OFFSET : offset;
    }
}
