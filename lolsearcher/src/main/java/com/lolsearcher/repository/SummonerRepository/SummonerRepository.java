package com.lolsearcher.repository.SummonerRepository;

import java.util.List;

import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;

public interface SummonerRepository {
	
	//-----------------Summoner 테이블 CRUD----------------------------------
	public void saveSummoner(Summoner summoner);
	
	public Summoner findSummonerById(String summonerid);
	
	public List<Summoner> findSummonerByName(String summonername);
	
	public void deleteSummoner(Summoner summoner);

	//-----------------Rank 테이블 CRUD----------------------------------
	public void saveRanks(List<Rank> set);
	
	public Rank findRank(RankCompKey rankKey);
	
	//-----------------Match,Member(1:N) 테이블 CRUD----------------------------------
	public boolean findMatchid(String matchid);
	
	public void saveMatch(Match match);
	
	public List<Match> findMatchList(String summonerid, int gametype, String champion, int count);
	
	public List<String> findMostchampids(String summonerid, int queue, int season);

	public MostChampDto findChamp(String summonerid, String champid, int queue, int season);
	
}
