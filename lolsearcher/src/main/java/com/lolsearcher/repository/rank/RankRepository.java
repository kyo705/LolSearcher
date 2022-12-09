package com.lolsearcher.repository.rank;

import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;

import java.util.List;

public interface RankRepository {
    void saveRanks(List<Rank> list);

    void saveRank(Rank rank);

    Rank findRank(RankCompKey rankKey);
}
