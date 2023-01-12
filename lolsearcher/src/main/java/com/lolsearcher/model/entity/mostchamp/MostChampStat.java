package com.lolsearcher.model.entity.mostchamp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Table(indexes = {@Index(columnList = "summonerId, seasonId, queueId")})
@Entity
public class MostChampStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String summonerId;
    private int championId;
    private int seasonId;
    private int queueId;
    private long totalKills;
    private long totalDeaths;
    private long totalAssists;
    private long totalMinionKills;
    private long totalGames;
    private long totalWins;
    private long totalLosses;
}
