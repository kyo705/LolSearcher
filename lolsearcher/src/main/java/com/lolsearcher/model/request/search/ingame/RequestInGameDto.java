package com.lolsearcher.model.request.search.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Builder
@AllArgsConstructor
@Data
public class RequestInGameDto {

    @NotEmpty
    private final String summonerId;
    @NotEmpty
    private final String summonerName;

    public RequestInGameDto(){
        summonerId = "";
        summonerName = "";
    }
}
