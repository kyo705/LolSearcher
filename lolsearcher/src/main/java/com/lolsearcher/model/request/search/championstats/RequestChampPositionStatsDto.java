package com.lolsearcher.model.request.search.championstats;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.PositionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Builder
@AllArgsConstructor
@Data
public class RequestChampPositionStatsDto {

    @Min(1) @Max(5)
    private final int position;
    @NotEmpty
    private final String gameVersion;

    public RequestChampPositionStatsDto(){

        this.position = PositionStatus.TOP.getCode();
        this.gameVersion = LolSearcherConstants.CURRENT_GAME_VERSION;
    }
}
