package com.lolsearcher.model.request.riot.match;

import com.lolsearcher.model.request.riot.match.team.RiotGamesTeamDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RiotGamesMatchDto {
    private long gameCreation;
    private long gameDuration;
    private long gameEndTimestamp;
    private long gameId;
    private String gameMode;
    private String gameName;
    private long gameStartTimestamp;
    private String gameType;
    private String gameVersion;
    private int mapId;
    private List<RiotGamesParticipantDto>  participants;
    private String platformId;
    private int queueId;
    private List<RiotGamesTeamDto> teams;
    private String tournamentCode;
}
