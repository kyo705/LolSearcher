package com.lolsearcher.repository.rank;

import com.lolsearcher.model.entity.rank.Rank;

import java.util.List;

public interface RankRepository {

    void saveRank(Rank rank);

    Rank findRank(String summonerId, int currentSeasonId, String queueType);

    List<Rank> findRanks(String summonerId, int currentSeasonId);

    void updateRank(Rank dbRank, Rank apiRank);
}
