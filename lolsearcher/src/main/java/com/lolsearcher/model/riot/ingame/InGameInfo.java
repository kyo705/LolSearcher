package com.lolsearcher.model.riot.ingame;

import com.lolsearcher.model.dto.ingame.InGameDto;
import lombok.Data;

import java.util.List;

@Data
public class InGameInfo {

    long gameId;
    String gameType;
    long gameStartTime;
    long mapId;
    long gameLength;
    String platformId;
    String gameMode;
    List<BannedChampionInfo> bannedChampions;
    long gameQueueConfigId;
    List<CurrentGameParticipantInfo> participants;

    public InGameDto changeToDto() {

        return InGameDto
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
