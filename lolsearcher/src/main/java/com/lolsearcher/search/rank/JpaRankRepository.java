package com.lolsearcher.search.rank;

import lombok.RequiredArgsConstructor;
import org.hibernate.NonUniqueResultException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lolsearcher.search.rank.RankConstant.MAX_COUNT_PER_RANK_TYPE;

@RequiredArgsConstructor
@Repository
public class JpaRankRepository implements RankRepository {

    private final EntityManager em;

    @Override
    public Optional<Rank> findRank(String summonerId, int seasonId, RankTypeState queueType) {

        String jpql = "SELECT r FROM Rank r " +
                "WHERE r.summonerId = :summonerId AND r.seasonId = :seasonId AND r.queueType = :queueType";

        List<Rank> ranks = em.createQuery(jpql, Rank.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setParameter("queueType", queueType)
                .getResultList();

        if(ranks.size() > MAX_COUNT_PER_RANK_TYPE){
            throw new NonUniqueResultException(ranks.size());
        }
        if(ranks.size() == 0){
            return Optional.empty();
        }
        return ranks.stream().peek(Rank::validate).findAny();
    }

    @Override
    public List<Rank> findRanks(String summonerId, int seasonId) {

        String jpql = "SELECT r FROM Rank r WHERE r.summonerId = :summonerId AND r.seasonId = :seasonId";

        return em.createQuery(jpql, Rank.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .getResultList()
                .stream()
                .peek(Rank::validate)
                .collect(Collectors.toList());
    }
}
