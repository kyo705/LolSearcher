package com.lolsearcher.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;

import com.lolsearcher.domain.Dto.MostChampDto;
import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.Summoner;

public interface SummonerRepository {
	
	//-----------------Summoner ���̺� CRUD----------------------------------
	public void savesummoner(Summoner summoner) throws EntityExistsException;
	
	public Summoner findsummonerById(String summonerid);
	
	public void updatesummoner(Summoner summoner1,Summoner summoner2);

	//-----------------Rank ���̺� CRUD----------------------------------
	public void saveLeagueEntry(Set<Rank> set) throws EntityExistsException;
	
	public Set<Rank> findLeagueEntry(String summonerid);
	
	public void updateLeagueEntry(Set<Rank> apileague, Set<Rank> dbleague);
	
	//-----------------Match,Member(1:N) ���̺� CRUD----------------------------------
	public boolean findMatchid(String matchid);
	
	public void saveMatch(Match match) throws EntityExistsException;
	
	public List<String> findMatchList(String summonerid, int gametype, String champion, int count);

	public Match findMatch(String matchid);
	
	public List<String> findMostchampids(String summonerid, int queue, int season);

	public MostChampDto findChamp(String summonerid, String champid, int queue, int season);
}
