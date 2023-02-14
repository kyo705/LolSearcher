package com.lolsearcher.repository.search.match;

import com.lolsearcher.model.entity.match.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaMatchRepository implements MatchRepository {

    private final EntityManager em;

    @Override
    public Match findMatchByGameId(String matchId) {

        String jpql = "SELECT m FROM Match m WHERE m.matchId = :matchId";

        List<Match> matches = em.createQuery(jpql, Match.class)
                .setParameter("matchId", matchId)
                .getResultList();

        if(matches.size() == 0){
            return null;
        }
        if(matches.size() == 1){
            return matches.get(0);
        }
        throw new NonUniqueResultException();
    }

    @Override
    public List<Match> findMatches(String summonerId, int queueId, int championId, int count) {

        if(queueId==-1) {
            if(championId == -1) {

                String jpql = "SELECT DISTINCT m FROM Match m "
                        + "INNER JOIN Team t ON m.matchId = t.match "
                        + "INNER JOIN SummaryMember s ON t.id = s.team "
                        + "WHERE s.summonerId = :summonerId "
                        + "ORDER BY m.gameEndTimestamp DESC";

                return em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }else {

                String jpql = "SELECT m FROM Match m "
                        + "INNER JOIN Team t ON m.matchId = t.match "
                        + "INNER JOIN SummaryMember s ON t.id = s.team "
                        + "WHERE s.summonerId = :summonerId AND s.championId = :championId "
                        + "ORDER BY m.gameEndTimestamp DESC";

                return em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("championId", championId)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }
        }else {
            if(championId == -1) {

                String jpql = "SELECT m FROM Match m "
                        + "INNER JOIN Team t ON m.matchId = t.match "
                        + "INNER JOIN SummaryMember s ON t.id = s.team "
                        + "WHERE s.summonerId = :summonerId AND m.queueId = :queueId "
                        + "ORDER BY m.gameEndTimestamp DESC";

                return em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("queueId", queueId)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();

            }else {

                String jpql = "SELECT m FROM Match m "
                        + "INNER JOIN Team t ON m.matchId = t.match "
                        + "INNER JOIN SummaryMember s ON t.id = s.team "
                        + "WHERE s.summonerId = :summonerId AND s.championId = :championId AND m.queueId = :queueId "
                        + "ORDER BY m.gameEndTimestamp DESC";

                return em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("championId", championId)
                        .setParameter("queueId", queueId)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }
        }
    }
}
