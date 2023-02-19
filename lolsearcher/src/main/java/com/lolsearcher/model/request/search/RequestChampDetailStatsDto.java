package com.lolsearcher.model.request.search;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

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
