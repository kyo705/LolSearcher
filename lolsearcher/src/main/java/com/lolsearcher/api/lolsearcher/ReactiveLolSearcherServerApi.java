package com.lolsearcher.api.lolsearcher;

import com.lolsearcher.model.entity.summoner.Summoner;

import java.util.List;

public interface ReactiveLolSearcherServerApi {

    Summoner updateSameNameSummoners(List<String> summonerIds);
}
