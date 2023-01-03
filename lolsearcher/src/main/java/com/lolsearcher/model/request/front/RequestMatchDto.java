package com.lolsearcher.model.request.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Builder
@AllArgsConstructor
@Data
public class RequestMatchDto {

    @NotNull
    private final String summonerId;
    @NotNull
    private final String championId;
    @Min(-1)
    private final int queueId;
    @PositiveOrZero
    private int count; /* 변경 가능 */
    private final boolean renew;

    public RequestMatchDto(){
        this.summonerId = "";
        this.championId = "all"; /* all : 모든 챔피언을 의미 */
        this.queueId = -1; /* -1 : 모든 매치 큐를 의미 */
        this.count = 20;
        this.renew = false;
    }
}
