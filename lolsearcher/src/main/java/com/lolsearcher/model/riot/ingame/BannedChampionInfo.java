package com.lolsearcher.model.riot.ingame;

import com.lolsearcher.model.dto.ingame.BannedChampionDto;
import lombok.Data;

@Data
public class BannedChampionInfo {

    int pickTurn;
    long championId;
    long teamId;

    public BannedChampionDto changeToDto(){

        return BannedChampionDto
                .builder()
                .pickTurn(pickTurn)
                .championId(championId)
                .teamId(teamId)
                .build();
    }
}
