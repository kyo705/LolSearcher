package com.lolsearcher.search.mostchamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MIN_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Builder
@AllArgsConstructor
@Getter
@Table(indexes = {@Index(columnList = "summonerId, seasonId, queueId")})
@Entity
public class MostChamp {

    @Id
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

    public void validate() {

        checkArgument(
                isNotEmpty(summonerId) &&
                        summonerId.length() >= SUMMONER_ID_MIN_LENGTH &&
                        summonerId.length() <= SUMMONER_ID_MAX_LENGTH,

                String.format("summonerId must be provided and its length must be between %s and %s",
                        SUMMONER_ID_MIN_LENGTH, SUMMONER_ID_MAX_LENGTH)
        );

        checkArgument(seasonId >= INITIAL_SEASON_ID && seasonId <= CURRENT_SEASON_ID,
                "seasonId must be in boundary seasonId");

        checkArgument(totalKills >= 0, "totalKills must be positive");
        checkArgument(totalDeaths >= 0, "totalDeaths must be positive");
        checkArgument(totalAssists >= 0, "totalAssists must be positive");
        checkArgument(totalMinionKills >= 0, "totalMinionKills must be positive");
        checkArgument(totalGames >= 0, "totalGames must be positive");
        checkArgument(totalWins >= 0, "totalWins must be positive");
        checkArgument(totalLosses >= 0, "totalLosses must be positive");
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalKills(long totalKills) {
        checkArgument(totalKills >= 0, "totalKills must be positive");
        this.totalKills = totalKills;
    }

    public void setTotalDeaths(long totalDeaths) {
        checkArgument(totalDeaths >= 0, "totalDeaths must be positive");
        this.totalDeaths = totalDeaths;
    }

    public void setTotalAssists(long totalAssists) {
        checkArgument(totalAssists >= 0, "totalAssists must be positive");
        this.totalAssists = totalAssists;
    }

    public void setTotalMinionKills(long totalMinionKills) {
        checkArgument(totalMinionKills >= 0, "totalMinionKills must be positive");
        this.totalMinionKills = totalMinionKills;
    }

    public void setTotalGames(long totalGames) {
        checkArgument(totalGames >= 0, "totalGames must be positive");
        this.totalGames = totalGames;
    }

    public void setTotalWins(long totalWins) {
        checkArgument(totalWins >= 0, "totalWins must be positive");
        this.totalWins = totalWins;
    }

    public void setTotalLosses(long totalLosses) {
        checkArgument(totalLosses >= 0, "totalLosses must be positive");
        this.totalLosses = totalLosses;
    }
}
