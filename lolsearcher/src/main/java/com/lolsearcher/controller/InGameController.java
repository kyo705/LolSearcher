package com.lolsearcher.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.service.ingame.InGameService;
import com.lolsearcher.service.summoner.SummonerService;

@RequiredArgsConstructor
@Controller
public class InGameController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final long RENEW_TIME = 1000*60*2L; //2분
	private final SummonerService summonerService;
	private final InGameService inGameService;
	
	@PostMapping(path = "/ingame")
	public ModelAndView inGame(@RequestAttribute String name) {
		ModelAndView mv = new ModelAndView();
		
		SummonerDto summonerDto = summonerService.findDbSummoner(name);

		long lastInGameSearchTimeStamp = summonerDto.getLastRenewTimeStamp();
		String summonerId = summonerDto.getSummonerId();

		InGameDto inGameDto = getInGame(lastInGameSearchTimeStamp, summonerId);
		logger.info("'{}'는 게임 : '{}'을 진행 중입니다.", name, inGameDto.getGameId());

		mv.addObject("summoner", summonerDto);
		mv.addObject("ingame", inGameDto);
		mv.setViewName("inGame");

		return mv;
	}

	private InGameDto getInGame(long lastInGameSearchTimeStamp, String summonerId)
			throws WebClientResponseException {

		if(System.currentTimeMillis() - lastInGameSearchTimeStamp < RENEW_TIME){
			return inGameService.getOldInGame(summonerId);
		}

		long inGameId = -1L;
		try{
			InGameDto inGameDto = inGameService.getRenewInGame(summonerId);
			inGameId = inGameDto.getGameId();

			return inGameDto;
		}  finally {
			inGameService.removeDirtyInGame(summonerId, inGameId);
		}
	}
}
