package com.lolsearcher.model.request.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.lolsearcher.constant.GameType.ALL_QUEUE_ID;
import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;

@Builder
@AllArgsConstructor
@Data
public class RequestMostChampDto {

    @NotNull
    private final String summonerId;
    @Min(-1)
    private final int gameQueue;
    @Positive
    private final int season;

    public RequestMostChampDto(){
        summonerId = "";
        gameQueue = ALL_QUEUE_ID.getQueueId();
        season = CURRENT_SEASON_ID;
    }
}
