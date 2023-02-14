package com.lolsearcher.repository.search.mostchamp;

import com.lolsearcher.model.entity.mostchamp.MostChampStat;

import java.util.List;

public interface MostChampRepository {

    List<MostChampStat> findMostChampions(String summonerId, int seasonId, int limitCount);
    List<MostChampStat> findMostChampions(String summonerId, int seasonId, int queueId, int limitCount);
}
