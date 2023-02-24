package com.lolsearcher.model.request.search.championstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Builder
@AllArgsConstructor
@Data
public class RequestChampDetailStatsDto {

    private final int championId;
    @NotEmpty
    private final String gameVersion;

    public RequestChampDetailStatsDto(){
        this.championId = 0;
        this.gameVersion = "";
    }
}
