package com.lolsearcher.model.request.front;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

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
