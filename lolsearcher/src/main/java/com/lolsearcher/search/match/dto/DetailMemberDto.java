package com.lolsearcher.search.match.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailMemberDto {

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
