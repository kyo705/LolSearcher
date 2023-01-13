package com.lolsearcher.model.output.front.match;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantDetailDto {

    private PerkStatsDto perkStatsDto;
    private PerksDto perksDto;
    private int goldEarned;
    private int goldSpent;
    private int totalDamageDealt;
    private int totalDamageDealtToChampions;
    private int totalDamageShieldedOnTeammates;
    private int totalDamageTaken;
    private int timeCCingOthers;
    private int totalHeal;
    private int totalHealsOnTeammates;
    private short detectorWardPurchased;
    private short detectorWardsPlaced;
    private short wardKills;
    private short wardsPlaced;
}
