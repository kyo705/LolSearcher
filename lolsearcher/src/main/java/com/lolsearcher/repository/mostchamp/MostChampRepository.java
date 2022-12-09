package com.lolsearcher.repository.mostchamp;

import com.lolsearcher.model.dto.mostchamp.MostChampDto;

import java.util.List;

public interface MostChampRepository {
    List<String> findMostChampionIds(String summonerId, int queue, int season);

    MostChampDto findMostChampion(String summonerId, String championId, int queue, int season);
}
