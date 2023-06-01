package com.lolsearcher.search.champion;

import com.lolsearcher.validation.Champion;
import com.lolsearcher.validation.GameVersion;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.lolsearcher.search.match.MatchConstant.CURRENT_GAME_VERSION;

@Builder
@Data
public class ChampionDetailsRequest {

    @NotNull
    @Champion
    private Integer championId;
    @GameVersion
    private String gameVersion;

    public ChampionDetailsRequest(){
        this.gameVersion = CURRENT_GAME_VERSION;
    }

    public ChampionDetailsRequest(Integer championId, String gameVersion) {
        this.championId = championId;
        this.gameVersion = gameVersion == null ? CURRENT_GAME_VERSION : gameVersion;
    }
}
