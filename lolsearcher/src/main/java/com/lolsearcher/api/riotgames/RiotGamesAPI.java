package com.lolsearcher.api.riotgames;

import com.lolsearcher.model.request.riot.ingame.RiotGamesInGameDto;
import com.lolsearcher.model.request.riot.match.RiotGamesTotalMatchDto;
import com.lolsearcher.model.request.riot.rank.RiotGamesRankDto;
import com.lolsearcher.model.request.riot.summoner.RiotGamesSummonerDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RiotGamesAPI {

	RiotGamesSummonerDto getSummonerByName(String summonerName);

	RiotGamesSummonerDto getSummonerById(String id);

	List<RiotGamesRankDto> getLeague(String summonerId);
	
	List<String> getAllMatchIds(String puuid, String lastMatchId);
	
	List<String> getMatchIds(String puuid, int start, int count, String lastMatchId);

	Mono<RiotGamesTotalMatchDto> getMatchByNonBlocking(String matchId);

	RiotGamesTotalMatchDto getMatchByBlocking(String matchId);

	RiotGamesInGameDto getInGameBySummonerId(String summonerId);

}
