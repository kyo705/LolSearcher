package com.lolsearcher.search.summoner;

import java.util.List;
import java.util.Optional;

public interface SummonerRepository {

	
	Optional<Summoner> findById(String summonerId);
	
	List<Summoner> findByName(String summonerName);

}
