package com.lolsearcher.api.riotgames;

import java.util.List;

import com.lolsearcher.model.dto.match.SuccessMatchesAndFailMatchIds;
import com.lolsearcher.model.entity.ingame.InGame;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;

public interface RiotRestAPI {
	
	Summoner getSummonerByName(String summonerName);
	
	Summoner getSummonerById(String id);

	List<Rank> getLeague(String summonerId);
	
	List<String> getAllMatchIds(String puuid, String lastMatchId);
	
	List<String> getMatchIds(String puuid, int queue, String type, int start, int count, String lastmatchid);
	
	Match getOneMatchByBlocking(String matchId);
	
	SuccessMatchesAndFailMatchIds getMatchesByNonBlocking(List<String> matchIds);
	
	InGame getInGameBySummonerId(String summonerId);
	
}
