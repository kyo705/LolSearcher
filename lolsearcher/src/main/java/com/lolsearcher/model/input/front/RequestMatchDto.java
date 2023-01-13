package com.lolsearcher.model.input.front;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Builder
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
        this.championId = -1; /* -1 : 모든 챔피언을 의미 */
        this.queueId = -1; /* -1 : 모든 매치 큐를 의미 */
        this.count = 20;
    }
}
