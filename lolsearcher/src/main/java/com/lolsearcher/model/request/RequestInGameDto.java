package com.lolsearcher.model.request;

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
