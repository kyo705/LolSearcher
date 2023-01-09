package com.lolsearcher.model.request.riot.match.team;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesTeamDto {
    private List<RiotGamesTeamBanDto> bans;
    private RiotGamesTeamObjectivesDto objectives;
    private short teamId;
    private boolean win;
}
