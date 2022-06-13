package com.lolsearcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.championstatic.ChampionDto;
import com.lolsearcher.domain.Dto.championstatic.TotalChampDto;
import com.lolsearcher.service.ChampionService;

@Controller
public class ChampionController {
	
	private final ChampionService championService;
	
	@Autowired
	public ChampionController(ChampionService championService) {
		this.championService = championService;
	}


	@PostMapping(path = "/champions")
	public ModelAndView champions(@RequestParam(defaultValue = "TOP", name = "position")String position) {
		
		ModelAndView mv = new ModelAndView();
		
		List<ChampionDto> champions = championService.getChampions(position);
		mv.addObject("champions", champions);
		mv.setViewName("champions");
			
		return mv;
	}
	
	@PostMapping(path = "/champions/detail")
	public ModelAndView championDetail(String champion) {
		
		ModelAndView mv = new ModelAndView();
		
		TotalChampDto totalChampDto = championService.getChampionDetail(champion);
		mv.addObject("championDetail", totalChampDto);
		mv.setViewName("champion_detail");
		
		return mv;
		
		
	}
}
