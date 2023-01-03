package com.lolsearcher.model.request.riot.ingame;

import lombok.Data;

import java.util.List;

@Data
public class CurrentGameParticipantDto {

    long championId;
    PerksDto perks;
    long profileIconId;
    boolean bot;
    long teamId;
    String summonerName;
    String summonerId;
    long spell1Id;
    long spell2Id;
    List<GameCustomizationObjectDto> gameCustomizationObjects;

    public com.lolsearcher.model.response.front.ingame.CurrentGameParticipantDto changeToDto(){

        return com.lolsearcher.model.response.front.ingame.CurrentGameParticipantDto
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
