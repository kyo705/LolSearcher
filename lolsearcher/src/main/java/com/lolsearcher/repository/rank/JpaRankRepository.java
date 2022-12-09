package com.lolsearcher.repository.rank;

import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaRankRepository implements RankRepository {
    private final EntityManager em;

    @Override
    public void saveRanks(List<Rank> list) throws DataIntegrityViolationException {
        for (Rank rank : list) {
            em.merge(rank);
        }
    }

    @Override
    public void saveRank(Rank rank) {
        em.merge(rank);
    }

    @Override
    public Rank findRank(RankCompKey rankKey) {
        return em.find(Rank.class, rankKey);
    }
}
