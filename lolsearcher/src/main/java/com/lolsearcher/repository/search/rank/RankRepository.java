package com.lolsearcher.repository.search.rank;

import com.lolsearcher.model.entity.rank.Rank;

import java.util.List;

public interface RankRepository {

    Rank findRank(String summonerId, int currentSeasonId, String queueType);

    List<Rank> findRanks(String summonerId, int currentSeasonId);

}
