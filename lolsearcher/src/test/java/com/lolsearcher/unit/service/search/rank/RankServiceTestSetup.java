package com.lolsearcher.unit.service.search.rank;

import com.lolsearcher.search.rank.Rank;

import java.util.List;

import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_FLEX_SR;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_SOLO_5x5;

public class RankServiceTestSetup {


	public static List<Rank> getValidRanks(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_SOLO_5x5)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_FLEX_SR)
				.build();

		return List.of(soloRank, flexRank);
	}

	public static List<Rank> getInvalidRanksToSameData(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_SOLO_5x5)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_SOLO_5x5)
				.build();

		return List.of(soloRank, flexRank);
	}

	public static List<Rank> getInvalidRanksToOverData(String summonerId) {

		Rank soloRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_SOLO_5x5)
				.build();

		Rank flexRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(RANKED_SOLO_5x5)
				.build();

		Rank invalidRank = Rank.builder()
				.summonerId(summonerId)
				.seasonId(CURRENT_SEASON_ID)
				.queueType(null)
				.build();

		return List.of(soloRank, flexRank, invalidRank);
	}
}
