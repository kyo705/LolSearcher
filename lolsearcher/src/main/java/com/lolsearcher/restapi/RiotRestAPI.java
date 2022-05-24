package com.lolsearcher.restapi;

import java.util.List;

import com.lolsearcher.domain.Dto.currentgame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.domain.entity.match.Match;

public interface RiotRestAPI {
	
	public Summoner getSummonerByName(String summonername);
	
	public Summoner getSummonerById(String id);
	
	public List<String> listofmatch(String puuid, int queue, String type, int start, int count, String lastmatchid);
	
	public Match getmatch(String matchid);
	
	public List<RankDto> getLeague(String summonerid);
	
	public InGameDto getInGameBySummonerId(String summonerid);
	
}
