package com.lolsearcher.repository.search.rank;

import com.lolsearcher.exception.exception.search.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.entity.rank.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaRankRepository implements RankRepository {

    private final EntityManager em;

    @Override
    public Rank findRank(String summonerId, int seasonId, String queueType) {

        String jpql = "SELECT r FROM Rank r " +
                "WHERE r.summonerId = :summonerId AND r.seasonId = :seasonId AND r.queueType = :queueType";

        List<Rank> ranks = em.createQuery(jpql, Rank.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setParameter("queueType", queueType)
                .getResultList();

        if(ranks.size() == 0){
            return null;
        }
        if(ranks.size() == 1){
            return ranks.get(0);
        }
        throw new NonUniqueRankTypeException(queueType);
    }

    @Override
    public List<Rank> findRanks(String summonerId, int seasonId) {

        String jpql = "SELECT r FROM Rank r WHERE r.summonerId = :summonerId AND r.seasonId = :seasonId";

        return em.createQuery(jpql, Rank.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .getResultList();
    }
}
