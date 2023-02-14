package com.lolsearcher.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static com.lolsearcher.constant.LolSearcherConstants.ALL_CHAMPION_ID;
import static com.lolsearcher.constant.LolSearcherConstants.MATCH_DEFAULT_COUNT;
import static com.lolsearcher.constant.enumeration.GameType.ALL_QUEUE_ID;

@Builder
@AllArgsConstructor
@Data
public class RequestMatchDto {

    @NotNull
    private final String summonerId;
    @Min(-1)
    private final int championId;
    @Min(-1)
    private final int queueId;
    @PositiveOrZero
    private final int count;

    public RequestMatchDto(){

        this.summonerId = "";
        this.championId = ALL_CHAMPION_ID; /* -1 : 모든 챔피언을 의미 */
        this.queueId = ALL_QUEUE_ID.getQueueId(); /* -1 : 모든 매치 큐를 의미 */
        this.count = MATCH_DEFAULT_COUNT;
    }
}
