package com.lolsearcher.search.mostchamp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Repository
@RequiredArgsConstructor
public class JpaMostChampRepository implements MostChampRepository {

    private final EntityManager em;

    @Override
    public List<MostChamp> findMostChampions(String summonerId, int seasonId, int count) {

        String jpql = "SELECT m.summonerId, m.seasonId, m.championId, " +
                "SUM(m.totalWins) as totalWins, " +
                "SUM(m.totalLosses) as totalLosses, " +
                "SUM(m.totalGames) as totalGames, " +
                "SUM(m.totalKills) as totalKills, " +
                "SUM(m.totalDeaths) as totalDeaths, " +
                "SUM(m.totalAssists) as totalAssists, " +
                "SUM(m.totalMinionKills) as totalMinionKills " +
                "FROM MostChamp m " +
                "GROUP BY m.summonerId, m.seasonId, m.championId " +
                "HAVING m.summonerId = :summonerId AND m.seasonId = :seasonId " +
                "ORDER BY totalGames DESC";

        List<Object[]> results = em.createQuery(jpql)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setFirstResult(0)
                .setMaxResults(count)
                .getResultList();

        List<MostChamp> answer = new ArrayList<>();

        for (Object[] result : results) {
            MostChamp mostChamp = new MostChamp();
            mostChamp.setSummonerId((String) result[0]);
            mostChamp.setSeasonId((int) result[1]);
            mostChamp.setChampionId((int) result[2]);
            mostChamp.setTotalWins((long) result[3]);
            mostChamp.setTotalLosses((long) result[4]);
            mostChamp.setTotalGames((long) result[5]);
            mostChamp.setTotalKills((long) result[6]);
            mostChamp.setTotalDeaths( (long) result[7]);
            mostChamp.setTotalAssists((long) result[8]);
            mostChamp.setTotalMinionKills((long) result[9]);

            mostChamp.validate();
            answer.add(mostChamp);
        }

        return answer;
    }

    @Override
    public List<MostChamp> findMostChampions(String summonerId,  Integer queueId, int seasonId, int count) {

        if(queueId == null) {
            return findMostChampions(summonerId, seasonId, count);
        }

        String jpql = "SELECT m FROM MostChamp m " +
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
