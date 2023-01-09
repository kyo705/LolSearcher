package com.lolsearcher.model.request.riot.ingame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotGamesInGameBannedChampionDto {

    int pickTurn;
    long championId;
    long teamId;
}
