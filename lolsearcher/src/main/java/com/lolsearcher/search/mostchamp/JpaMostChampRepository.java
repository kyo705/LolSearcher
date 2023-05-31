package com.lolsearcher.search.mostchamp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.lolsearcher.search.match.MatchQueue.ALL_QUEUE_ID;

@SuppressWarnings("ALL")
@Repository
@RequiredArgsConstructor
public class JpaMostChampRepository implements MostChampRepository {

    private final EntityManager em;

    @Override
    public List<MostChamp> findMostChampions(String summonerId, int seasonId, int count) {

        String jpql = "SELECT m FROM MostChampStats m " +
                "WHERE m.summonerId = :summonerId AND m.seasonId = :seasonId " +
                "ORDER BY m.totalGames DESC";

        return em.createQuery(jpql, MostChamp.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setFirstResult(0)
                .setMaxResults(count)
                .getResultList()
                .stream()
                .peek(MostChamp::validate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MostChamp> findMostChampions(String summonerId, int seasonId, int queueId, int count) {

        if(queueId == ALL_QUEUE_ID.getQueueId()) {
            return findMostChampions(summonerId, seasonId, count);
        }

        String jpql = "SELECT m FROM MostChampStats m " +
                "WHERE m.summonerId = :summonerId AND m.seasonId = :seasonId AND queueId = :queueId " +
                "ORDER BY m.totalGames DESC";

        return em.createQuery(jpql, MostChamp.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setParameter("queueId", queueId)
                .setFirstResult(0)
                .setMaxResults(count)
                .getResultList()
                .stream()
                .peek(MostChamp::validate)
                .collect(Collectors.toList());
    }
}
