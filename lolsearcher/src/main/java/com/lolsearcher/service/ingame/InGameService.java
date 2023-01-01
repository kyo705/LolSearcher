package com.lolsearcher.service.ingame;

import com.lolsearcher.exception.ingame.NoInGameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.ingame.InGameDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class InGameService {

	private final RiotRestAPI riotGames;

	public InGameDto getInGame(String summonerId) {

		InGameDto inGameDto = riotGames.getInGameBySummonerId(summonerId);

		if(inGameDto == null){
			throw new NoInGameException(1);
		}
		return inGameDto;
	}
}
