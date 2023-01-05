package com.lolsearcher.api.riotgames;

import java.util.List;

import com.lolsearcher.model.response.front.ingame.InGameDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.rank.Rank;
import reactor.core.publisher.Mono;

public interface RiotGamesAPI {
	
	Summoner getSummonerByName(String summonerName);
	
	Summoner getSummonerById(String id);

	List<Rank> getLeague(String summonerId);
	
	List<String> getAllMatchIds(String puuid, String lastMatchId);
	
	List<String> getMatchIds(String puuid, int start, int count, String lastMatchId);

	Mono<Match> getMatchByNonBlocking(String matchId);

	Match getMatchByBlocking(String matchId);

	InGameDto getInGameBySummonerId(String summonerId);

}
