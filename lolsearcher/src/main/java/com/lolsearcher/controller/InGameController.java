package com.lolsearcher.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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
		
		//view로 전달될 데이터(Model)
		InGameDto inGameDto = null;
		SummonerDto summonerDto = null;
		
		summonerDto = summonerService.findDbSummoner(name);
		String summonerId = summonerDto.getSummonerid();
		
		inGameDto = inGameService.getInGame(summonerDto);
		
		
		if(inGameDto==null) {
			logger.info("'{}' is not in game", name);
			threadService.runRemovingDirtyInGame(summonerId);
			
			mv.addObject("summoner", summonerDto);
			mv.setViewName("error_ingame");
		}else {
			logger.info("'{}' is in game '{}'", name, inGameDto.getGameId());
			threadService.runSavingInGame(inGameDto);
			threadService.runRemovingDirtyInGame(summonerId, inGameDto.getGameId());
			
			mv.addObject("summoner", summonerDto);
			mv.addObject("ingame", inGameDto);
			mv.setViewName("inGame");
		}
		
		return mv;
	}
}
