package com.lolsearcher.search.champion;

import com.lolsearcher.validation.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.lolsearcher.search.champion.PositionState.*;
import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;

@Builder
@Getter
@Setter
public class ChampionsRequest {

    @Position(anyOf = {TOP, JUNGLE, MIDDLE, BOTTOM, UTILITY})
    private PositionState position;
    private String gameVersion;

    public ChampionsRequest(){

        this.position = TOP;
        this.gameVersion = CURRENT_GAME_VERSION;
    }

    public ChampionsRequest(PositionState position, String gameVersion) {

        this.position = position == null ? TOP : position;
        this.gameVersion = gameVersion == null ? CURRENT_GAME_VERSION : gameVersion;
    }
}
