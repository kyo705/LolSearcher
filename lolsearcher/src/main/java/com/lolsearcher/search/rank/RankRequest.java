package com.lolsearcher.search.rank;

import com.lolsearcher.validation.Rank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_FLEX_SR;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_SOLO_5x5;
import static com.lolsearcher.search.summoner.SummonerConstant.SUMMONER_ID_MAX_LENGTH;

@Builder
@Setter
@Getter
public class RankRequest {

    @NotBlank(groups = {RankFindAllGroup.class, RankFindByIdGroup.class})
    @Size(max = SUMMONER_ID_MAX_LENGTH, groups = {RankFindAllGroup.class, RankFindByIdGroup.class})
    private String summonerId;

    @NotNull(groups = {RankFindByIdGroup.class})
    @Rank(anyOf = {RANKED_FLEX_SR, RANKED_SOLO_5x5}, groups = {RankFindByIdGroup.class})
    private RankTypeState rankId;
    @Max(value = CURRENT_SEASON_ID, groups = {RankFindAllGroup.class, RankFindByIdGroup.class})
    @Min(value = INITIAL_SEASON_ID, groups = {RankFindAllGroup.class, RankFindByIdGroup.class})
    private Integer seasonId;

    public RankRequest() {
        summonerId = "";
        rankId = null;
        seasonId = CURRENT_SEASON_ID;
    }

    public RankRequest(String summonerId, RankTypeState rankId, Integer seasonId) {
        this.summonerId = summonerId;
        this.rankId = rankId;
        this.seasonId = seasonId == null ? CURRENT_SEASON_ID : seasonId;
    }
}
interface RankFindByIdGroup { }
interface RankFindAllGroup { }