package com.lolsearcher.search.mostchamp;

import java.util.List;

public interface MostChampRepository {

    List<MostChamp> findMostChampions(String summonerId, int seasonId, int count);
    List<MostChamp> findMostChampions(String summonerId, int seasonId, int queueId, int count);
}
