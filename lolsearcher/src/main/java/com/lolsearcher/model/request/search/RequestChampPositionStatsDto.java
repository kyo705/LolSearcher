package com.lolsearcher.model.request.search;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.PositionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Data
public class RequestChampPositionStatsDto {

    @Min(1) @Max(5)
    private final int position;
    @NotEmpty
    private final String gameVersion;

    public RequestChampPositionStatsDto(){

        this.position = PositionStatus.TOP.getId();
        this.gameVersion = LolSearcherConstants.CURRENT_GAME_VERSION;
    }
}
