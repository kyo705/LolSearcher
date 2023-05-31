package com.lolsearcher.search.match;

import com.lolsearcher.search.match.entity.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JpaMatchRepository implements MatchRepository {

    private final CacheManager cacheManager;
    private final EntityManager em;

    @Override
    public Optional<Match> findById(String matchId) {

        String jpql = "SELECT m FROM Match m WHERE m.matchId = :matchId";

        List<Match> matches = em.createQuery(jpql, Match.class)
                .setParameter("matchId", matchId)
                .getResultList();

        if(matches.size() == 0){
            return Optional.empty();
        }
        if(matches.size() >= 2){
            throw new NonUniqueResultException();
        }
        return matches.stream().peek(match -> match.validate(cacheManager)).findAny();
    }

    @Override
    public List<Match> findMatches(String summonerId, Integer queueId, Integer championId, int count, int offset) {

        List<Match> matches = null;

        if(queueId == null && championId == null) {

            String jpql = "SELECT DISTINCT m FROM Match m "
                    + "INNER JOIN SummaryMember s ON m.matchId = s.match "
                    + "WHERE s.summonerId = :summonerId "
                    + "ORDER BY m.gameEndTimestamp DESC";

            matches = em.createQuery(jpql, Match.class)
                    .setParameter("summonerId", summonerId)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        }
        else if(queueId == null && championId != null) {

            String jpql = "SELECT m FROM Match m "
                    + "INNER JOIN SummaryMember s ON m.matchId = s.matchId "
                    + "WHERE s.summonerId = :summonerId AND s.championId = :championId "
                    + "ORDER BY m.gameEndTimestamp DESC";

            matches = em.createQuery(jpql, Match.class)
                    .setParameter("summonerId", summonerId)
                    .setParameter("championId", championId)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        }
        else if(queueId != null && championId == null) {

            String jpql = "SELECT m FROM Match m "
                    + "INNER JOIN SummaryMember s ON m.matchId = s.matchId "
                    + "WHERE s.summonerId = :summonerId AND m.queueId = :queueId "
                    + "ORDER BY m.gameEndTimestamp DESC";

            matches = em.createQuery(jpql, Match.class)
                    .setParameter("summonerId", summonerId)
                    .setParameter("queueId", queueId)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        }
        else {
            String jpql = "SELECT m FROM Match m "
                    + "INNER JOIN SummaryMember s ON m.matchId = s.matchId "
                    + "WHERE s.summonerId = :summonerId AND s.championId = :championId AND m.queueId = :queueId "
                    + "ORDER BY m.gameEndTimestamp DESC";

            matches = em.createQuery(jpql, Match.class)
                    .setParameter("summonerId", summonerId)
                    .setParameter("championId", championId)
                    .setParameter("queueId", queueId)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        }

        return matches.stream().peek(match -> match.validate(cacheManager)).collect(Collectors.toList());
    }
}
