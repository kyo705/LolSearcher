package com.lolsearcher.repository.match;

import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.PerkStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaMatchRepository implements MatchRepository {
    private final EntityManager em;

    @Override
    public Match findMatchById(String matchId) {
        return em.find(Match.class, matchId);
    }

    @Override
    public void saveMatch(Match match) {
        if(em.find(Match.class, match.getMatchId())==null) {
            em.persist(match);
        }
    }

    @Override
    public List<Match> findMatches(String summonerId, int gameType, String champion, int count) {
        List<Match> matchList;
        if(gameType==-1) {
            if(champion.equals("all")) {

                String jpql = "SELECT DISTINCT m FROM Match m "
                        + "WHERE m.matchId IN "
                        + "(SELECT DISTINCT t.ck.matchId FROM Member t WHERE t.summonerId = :summonerId) "
                        + "ORDER BY m.gameEndTimestamp DESC";

                matchList = em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }else {
                String jpql = "SELECT DISTINCT m FROM Match m "
                        + "WHERE m.matchId IN "
                        + "(SELECT DISTINCT t.ck.matchId from Member t "
                        + "WHERE t.summonerId = :summonerId AND t.championId = :championId) "
                        + "ORDER BY m.gameEndTimestamp DESC";

                matchList = em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("championId", champion)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }
        }else {
            if(champion.equals("all")) {
                String jpql = "SELECT DISTINCT m FROM Match m "
                        + "WHERE m.queueId = :queueId AND m.matchId IN "
                        + "(SELECT DISTINCT t.ck.matchId from Member t where t.summonerId = :summonerId) "
                        + "ORDER BY m.gameEndTimestamp DESC";

                matchList = em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("queueId", gameType)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();

            }else {
                String jpql = "SELECT DISTINCT m FROM Match m "
                        + "WHERE m.queueId = :queueId AND m.matchId IN "
                        + "(SELECT DISTINCT t.ck.matchId from Member t "
                        + "WHERE t.championId = :championId AND t.summonerId = :summonerId) "
                        + "ORDER BY m.gameEndTimestamp DESC";

                matchList = em.createQuery(jpql, Match.class)
                        .setParameter("summonerId", summonerId)
                        .setParameter("championId", champion)
                        .setParameter("queueId", gameType)
                        .setFirstResult(0)
                        .setMaxResults(count)
                        .getResultList();
            }
        }
        return matchList;
    }

    @Override
    public PerkStats findPerkStats(short defense, short flex, short offense) {
        String jpql = "SELECT p FROM PerkStats p WHERE p.defense = :defense AND p.flex = :flex AND p.offense = :offense";

        return em.createQuery(jpql, PerkStats.class)
                .setParameter("defense", defense)
                .setParameter("flex", flex)
                .setParameter("offense", offense)
                .getSingleResult();
    }
}
