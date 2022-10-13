package com.lolsearcher.restapi;

import java.util.List;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.RecentMatchesDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;

public interface RiotRestAPI {
	
	public Summoner getSummonerByName(String summonername);
	
	public Summoner getSummonerById(String id);
	
	public List<String> getAllMatchIds(String puuid, String lastMatchId);
	
	public List<String> getMatchIds(String puuid, int queue, String type, int start, int count, String lastmatchid);
	
	public Match getOneMatchByBlocking(String matchId);
	
	public RecentMatchesDto getMatchesByNonBlocking(List<String> matchIds);
	
	public List<RankDto> getLeague(String summonerid);
	
	public InGameDto getInGameBySummonerId(String summonerid);
	
}
