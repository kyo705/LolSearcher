package com.lolsearcher.search.summoner;

import java.util.List;
import java.util.Optional;

public interface SummonerAPI {

    Optional<Summoner> updateSameNameSummoners(List<String> summonerIds);
}
