package com.lolsearcher.model.request.riot.match;

import com.lolsearcher.model.request.riot.match.team.TeamDto;
import lombok.Data;

import java.util.List;

@Data
public class MatchDto {
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
    private List<ParticipantDto>  participants;
    private String platformId;
    private int queueId;
    private List<TeamDto> teams;
    private String tournamentCode;
}
