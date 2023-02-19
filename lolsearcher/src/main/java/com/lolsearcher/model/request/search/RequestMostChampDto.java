package com.lolsearcher.model.request.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;
import static com.lolsearcher.constant.enumeration.GameType.ALL_QUEUE_ID;

@Builder
@AllArgsConstructor
@Data
public class RequestMostChampDto {

    @NotNull
    private final String summonerId;
    @Min(-1)
    private final int queueId;
    @Positive
    private final int seasonId;

    public RequestMostChampDto(){

        summonerId = "";
        queueId = ALL_QUEUE_ID.getQueueId();
        seasonId = CURRENT_SEASON_ID;
    }
}
