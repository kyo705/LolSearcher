package com.lolsearcher.service.ingame;

import com.lolsearcher.api.riotgames.RiotGamesAPI;
import com.lolsearcher.exception.ingame.NoInGameException;
import com.lolsearcher.exception.summoner.NoExistSummonerException;
import com.lolsearcher.model.response.front.ingame.InGameDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RequiredArgsConstructor
@Service
public class InGameService {

	private final RiotGamesAPI riotGames;

	public InGameDto getInGame(String summonerId) {

		try {
			return riotGames.getInGameBySummonerId(summonerId);
		}catch(WebClientResponseException e){
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
				throw new NoExistSummonerException(summonerId);
			}
			if(e.getStatusCode() == HttpStatus.NOT_FOUND){
				throw new NoInGameException(summonerId);
			}
			throw e;
		}
	}
}
