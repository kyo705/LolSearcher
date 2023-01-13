package com.lolsearcher.model.input.riot.ingame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotGamesInGameBannedChampionDto {

    int pickTurn;
    long championId;
    long teamId;
}
