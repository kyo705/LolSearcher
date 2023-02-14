package com.lolsearcher.repository.search.mostchamp;

import com.lolsearcher.model.entity.mostchamp.MostChampStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Repository
@RequiredArgsConstructor
public class JpaMostChampRepository implements MostChampRepository {

    private final EntityManager em;

    @Override
    public List<MostChampStat> findMostChampions(String summonerId, int seasonId, int limitCount) {

        String jpql = "SELECT m.championId, SUM(m.totalKills), SUM(m.totalDeaths), SUM(m.totalAssists), " +
                "SUM(m.totalMinionKills), SUM(m.totalGames) AS totalGames ,SUM(m.totalWins), SUM(m.totalLosses)" +
                "  FROM MostChampStat m WHERE m.summonerId = :summonerId AND m.seasonId = :seasonId " +
                "GROUP BY m.championId ORDER BY totalGames DESC";

        List result = em.createQuery(jpql)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setFirstResult(0)
                .setMaxResults(limitCount)
                .getResultList();

        return getMostChampDto(result, summonerId, seasonId);
    }

    @Override
    public List<MostChampStat> findMostChampions(String summonerId, int seasonId, int queueId, int limitCount) {

        String jpql = "SELECT m FROM MostChampStat m " +
                "WHERE m.summonerId = :summonerId AND m.seasonId = :seasonId AND queueId = :queueId " +
                "ORDER BY m.totalGames DESC";

        return em.createQuery(jpql, MostChampStat.class)
                .setParameter("summonerId", summonerId)
                .setParameter("seasonId", seasonId)
                .setParameter("queueId", queueId)
                .setFirstResult(0)
                .setMaxResults(limitCount)
                .getResultList();
    }


    private List<MostChampStat> getMostChampDto(List results, String summonerId, int seasonId) {

        List<MostChampStat> mostChampStatList = new ArrayList<>();

        results.forEach(object -> {
            MostChampStat mostChampStat = changeMostChampStats(object);
            mostChampStat.setSummonerId(summonerId);
            mostChampStat.setSeasonId(seasonId);

            mostChampStatList.add(mostChampStat);
        });

        return mostChampStatList;
    }

    private MostChampStat changeMostChampStats(Object object) {

        /*  0:championId,  1:kills,  2:deaths,  3:assists,  4:minionKills,  5:totalGames  6:totalWins  7:totalLosses  */
        Object[] objects = (Object[]) object;

        return MostChampStat.builder()
                .championId((int)objects[0])
                .totalKills((long)objects[1])
                .totalDeaths((long)objects[2])
                .totalAssists((long)objects[3])
                .totalMinionKills((long)objects[4])
                .totalGames((long)objects[5])
                .totalWins((long)objects[6])
                .totalLosses((long)objects[7])
                .build();

    }
}
