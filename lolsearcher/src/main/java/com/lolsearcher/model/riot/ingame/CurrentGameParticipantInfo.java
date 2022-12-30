package com.lolsearcher.model.riot.ingame;

import com.lolsearcher.model.dto.ingame.CurrentGameParticipantDto;
import lombok.Data;

import java.util.List;

@Data
public class CurrentGameParticipantInfo {

    long championId;
    PerksInfo perks;
    long profileIconId;
    boolean bot;
    long teamId;
    String summonerName;
    String summonerId;
    long spell1Id;
    long spell2Id;
    List<GameCustomizationObjectInfo> gameCustomizationObjects;

    public CurrentGameParticipantDto changeToDto(){

        return CurrentGameParticipantDto
                .builder()
                .championId(championId)
                .profileIconId(profileIconId)
                .bot(bot)
                .teamId(teamId)
                .summonerName(summonerName)
                .summonerId(summonerId)
                .spell1Id(spell1Id)
                .spell2Id(spell2Id)
                .build();
    }
}
