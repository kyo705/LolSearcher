package com.lolsearcher.unit.service.search.rank;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.request.search.rank.RequestRankDto;

import java.util.List;

public class RankServiceTestSetup {

    public static RequestRankDto getRequestDto() {

		return new RequestRankDto("summonerId1");
    }

	public static List<Rank> getValidRanks(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.SOLO_RANK)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.FLEX_RANK)
				.build();

		return List.of(soloRank, flexRank);
	}

	public static List<Rank> getInvalidRanksToSameData(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.SOLO_RANK)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.SOLO_RANK)
				.build();

		return List.of(soloRank, flexRank);
	}

	public static List<Rank> getInvalidRanksToOverData(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.SOLO_RANK)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType(LolSearcherConstants.SOLO_RANK)
				.build();

		Rank invalidRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(LolSearcherConstants.CURRENT_SEASON_ID)
				.queueType("InvalidRankType")
				.build();

		return List.of(soloRank, flexRank, invalidRank);
	}
}
