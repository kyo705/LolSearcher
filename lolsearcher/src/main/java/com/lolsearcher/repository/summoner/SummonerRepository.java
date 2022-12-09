package com.lolsearcher.repository.summoner;

import java.util.List;

import com.lolsearcher.model.entity.summoner.Summoner;

public interface SummonerRepository {

	void saveSummoner(Summoner summoner);
	
	Summoner findSummonerById(String summonerId);
	
	List<Summoner> findSummonerByName(String summonerName);
	
	void deleteSummoner(Summoner summoner);

	
}
