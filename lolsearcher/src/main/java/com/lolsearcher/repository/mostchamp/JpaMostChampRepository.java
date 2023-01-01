package com.lolsearcher.repository.mostchamp;

import com.lolsearcher.model.dto.mostchamp.MostChampDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaMostChampRepository implements MostChampRepository {
    private final EntityManager em;
    @SuppressWarnings("rawtypes")
    @Override
    public List<String> findMostChampionIds(String summonerId, int queue, int season, int limitCount) {
        final int count = 5;

        List<String> championIds = new ArrayList<>();
        String jpql;
        List results; //집계함수(ex. count(),avg() ...)를 통해 받는 값은 long type임. jpa에서 그렇게 제공해줌
        if(queue==-1) {
            jpql = "select m.championId, COUNT(m.championId) AS c from Member m "
                    + "where m.summonerId = :summonerId and m.match.season = :season "
                    + "GROUP BY m.championId ORDER BY c DESC";

            results = em.createQuery(jpql)
                    .setParameter("summonerId", summonerId)
                    .setParameter("season", season)
                    .setFirstResult(0)
                    .setMaxResults(count)
                    .getResultList();
        }else {
            jpql = "select m.championId, COUNT(m.championId) AS c from Member m "
                    + "where m.summonerId = :summonerId and m.match.queueId = :queue and m.match.season = :season "
                    + "GROUP BY m.championId ORDER BY c DESC";

            results = em.createQuery(jpql)
                    .setParameter("summonerId", summonerId)
                    .setParameter("queue", queue)
                    .setParameter("season", season)
                    .setFirstResult(0)
                    .setMaxResults(count)
                    .getResultList();
        }

        for(Object result : results) {
            Object[] obj = (Object[])result;
            championIds.add((String)obj[0]);
        }

        return championIds;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MostChampDto findMostChampion(String summonerId, String championId, int queue, int season) {
        List<Object> results;

        if(queue==-1) {
            String jpql = "select avg(m.totalMinionsKilled), avg(m.kills), avg(m.deaths), avg(m.assists), count(m), m.win "
                    + "from Member m "
                    + "where m.championId = :championId and m.summonerId = :summonerId and m.match.season = :season "
                    + "group by m.win";

            results = em.createQuery(jpql)
                    .setParameter("summonerId", summonerId)
                    .setParameter("championId", championId)
                    .setParameter("season", season)
                    .getResultList();
        }else {
            String jpql = "select avg(m.cs), avg(m.kills), avg(m.deaths), avg(m.assists), count(m), m.win "
                    + "from Member m "
                    + "where m.championId = :championId and m.summonerId = :summonerId and m.match.queueId = :queue and "
                    + "m.match.season = :season "
                    + "group by m.win";

            results = em.createQuery(jpql)
                    .setParameter("summonerId", summonerId)
                    .setParameter("championId", championId)
                    .setParameter("queue", queue)
                    .setParameter("season", season)
                    .getResultList();
        }

        MostChampDto champ = new MostChampDto();

        champ.setChampionId(championId);

        for(Object result : results) {
            Object[] o = (Object[])result;
            if(((boolean) o[5])) {
                champ.setTotalWinCount((long)o[4]);
            }
            champ.setAvgCs((double)o[0] + champ.getAvgCs());
            champ.setAvgKill((double)o[1] + champ.getAvgKill());
            champ.setAvgDeath((double)o[2] + champ.getAvgDeath());
            champ.setAvgAssist((double)o[3] + champ.getAvgAssist());
            champ.setTotalGameCount((long)o[4] + champ.getTotalGameCount());
        }

        champ.setAvgCs(champ.getAvgCs()/2);
        champ.setAvgKill(champ.getAvgKill()/2);
        champ.setAvgDeath(champ.getAvgDeath()/2);
        champ.setAvgAssist(champ.getAvgAssist()/2);

        return champ;
    }
}
