package com.lolsearcher.model.request.riot.ingame;

import lombok.Data;

import java.util.List;

@Data
public class InGameDto {

    long gameId;
    String gameType;
    long gameStartTime;
    long mapId;
    long gameLength;
    String platformId;
    String gameMode;
    List<BannedChampionDto> bannedChampions;
    long gameQueueConfigId;
    List<CurrentGameParticipantDto> participants;

    public com.lolsearcher.model.response.front.ingame.InGameDto changeToDto() {

        return com.lolsearcher.model.response.front.ingame.InGameDto
                .builder()
                .gameId(gameId)
                .gameType(gameType)
                .gameStartTime(gameStartTime)
                .gameLength(gameLength)
                .gameMode(gameMode)
                .gameQueueConfigId(gameQueueConfigId)
                .mapId(mapId)
                .platformId(platformId)
                .build();
    }
}
