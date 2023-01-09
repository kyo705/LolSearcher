package com.lolsearcher.model.request.riot.match.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiotGamesTeamObjectivesDto {
    private RiotGamesTeamObjectiveDto baron;
    private RiotGamesTeamObjectiveDto champion;
    private RiotGamesTeamObjectiveDto dragon;
    private RiotGamesTeamObjectiveDto inhibitor;
    private RiotGamesTeamObjectiveDto riftHerald;
    private RiotGamesTeamObjectiveDto tower;

}
