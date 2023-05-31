package com.lolsearcher.search.rank;

import java.util.List;
import java.util.Optional;

public interface RankRepository {

    Optional<Rank> findRank(String summonerId, int seasonId, RankTypeState queueType);

    List<Rank> findRanks(String summonerId, int seasonId);

}
