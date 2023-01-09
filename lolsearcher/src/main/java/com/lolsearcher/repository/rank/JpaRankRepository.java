package com.lolsearcher.repository.rank;

import com.lolsearcher.exception.rank.NonUniqueRankTypeException;
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
    public void saveRank(Rank rank) {

        Rank oldRank = findRank(rank.getSummonerId(), rank.getSeasonId(), rank.getQueueType());
        if(oldRank == null){
            em.persist(rank);
        }else{
            updateRank(oldRank, rank);
        }
    }

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

    @Override
    public void updateRank(Rank oldRank, Rank newRank) {

        oldRank.setTier(newRank.getTier());
        oldRank.setRank(newRank.getRank());
        oldRank.setLeagueId(newRank.getLeagueId());
        oldRank.setLeaguePoints(newRank.getLeaguePoints());
        oldRank.setWins(newRank.getWins());
        oldRank.setLosses(newRank.getLosses());
    }
}
