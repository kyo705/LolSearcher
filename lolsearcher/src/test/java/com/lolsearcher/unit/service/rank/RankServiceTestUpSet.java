package com.lolsearcher.unit.service.rank;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

public class RankServiceTestUpSet {
	static final int CURRENT_SEASONID = 22;
	static final String SOLO_RANK = "RANKED_SOLO_5x5";
	static final String FLEX_RANK = "RANKED_FLEX_SR";
	
	protected static Stream<Arguments> getRankParameter(){
		String summonerId = "summonerId";
		
		RankCompKey soloRankKey = new RankCompKey(summonerId, SOLO_RANK, CURRENT_SEASONID);
		RankCompKey flexRankKey = new RankCompKey(summonerId, FLEX_RANK, CURRENT_SEASONID);
		
		Rank soloRank = Rank.builder()
				.ck(soloRankKey)
				.wins(30)
				.losses(20)
				.build();
		Rank flexRank = Rank.builder()
				.ck(soloRankKey)
				.wins(20)
				.losses(10)
				.build();
		
		return Stream.of(
				Arguments.arguments(soloRankKey, flexRankKey, soloRank, flexRank),
				Arguments.arguments(soloRankKey, flexRankKey, soloRank, null),
				Arguments.arguments(soloRankKey, flexRankKey, null, flexRank),
				Arguments.arguments(soloRankKey, flexRankKey, null, null)
				);
	}
	
	protected static Stream<Arguments> setRankParameter(){
		String summonerId = "summonerId";

		RankCompKey soloRankKey = new RankCompKey(summonerId, SOLO_RANK, CURRENT_SEASONID);
		RankCompKey flexRankKey = new RankCompKey(summonerId, FLEX_RANK, CURRENT_SEASONID);

		Rank soloRank = Rank.builder()
				.ck(soloRankKey)
				.wins(30)
				.losses(20)
				.build();

		Rank flexRank = Rank.builder()
				.ck(flexRankKey)
				.wins(20)
				.losses(10)
				.build();
		
		return Stream.of(
				Arguments.arguments(List.of(soloRank)),
				Arguments.arguments(List.of(flexRank)),
				Arguments.arguments(List.of(soloRank, flexRank))
				);
	}
}
