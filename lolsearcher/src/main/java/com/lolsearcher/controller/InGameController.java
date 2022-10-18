package com.lolsearcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.service.InGameService;
import com.lolsearcher.service.SummonerService;
import com.lolsearcher.service.ThreadService;

@Controller
public class InGameController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final SummonerService summonerService;
	
	private final InGameService inGameService;
	
	private final ThreadService threadService;
	
	public InGameController(SummonerService summonerService,
							InGameService inGameService,
							ThreadService threadService) {
		this.summonerService = summonerService;
		this.inGameService = inGameService;
		this.threadService = threadService;
	}
	
	@GetMapping(path = "/ingame")
	public ModelAndView inGame(String filteredname) {
		ModelAndView mv = new ModelAndView();
		
		//view로 전달될 데이터(Model)
		InGameDto inGameDto = null;
		SummonerDto summonerDto = null;
		
		summonerDto = summonerService.findDbSummoner(filteredname);
		String summonerId = summonerDto.getSummonerid();
		
		inGameDto = inGameService.getInGame(summonerDto);
		
		
		if(inGameDto==null) {
			logger.info("'{}' is not in game", filteredname);
			threadService.runRemovingDirtyInGame(summonerId);
			
			mv.addObject("summoner", summonerDto);
			mv.setViewName("error_ingame");
		}else {
			logger.info("'{}' is in game '{}'", filteredname, inGameDto.getGameId());
			threadService.runSavingInGame(inGameDto);
			threadService.runRemovingDirtyInGame(summonerId, inGameDto.getGameId());
			
			mv.addObject("summoner", summonerDto);
			mv.addObject("ingame", inGameDto);
			mv.setViewName("inGame");
		}
		
		return mv;
	}
}
