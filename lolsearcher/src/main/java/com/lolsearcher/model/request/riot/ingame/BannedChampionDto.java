package com.lolsearcher.model.request.riot.ingame;

import lombok.Data;

@Data
public class BannedChampionDto {

    int pickTurn;
    long championId;
    long teamId;

    public com.lolsearcher.model.response.front.ingame.BannedChampionDto changeToDto(){

        return com.lolsearcher.model.response.front.ingame.BannedChampionDto
                .builder()
                .pickTurn(pickTurn)
                .championId(championId)
                .teamId(teamId)
                .build();
    }
}
