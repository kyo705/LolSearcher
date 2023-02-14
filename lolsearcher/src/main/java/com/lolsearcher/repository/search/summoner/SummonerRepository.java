package com.lolsearcher.repository.search.summoner;

import com.lolsearcher.model.entity.summoner.Summoner;

import java.util.List;

public interface SummonerRepository {

	
	Summoner findSummonerById(String summonerId);
	
	List<Summoner> findSummonerByName(String summonerName);

}
