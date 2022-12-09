package com.lolsearcher.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.model.dto.championstatic.ChampPositionDto;
import com.lolsearcher.model.dto.championstatic.TotalChampDto;
import com.lolsearcher.service.statistic.ChampionService;

@RequiredArgsConstructor
@Controller
public class ChampionController {
	private final ChampionService championService;

	@PostMapping(path = "/champions")
	public ModelAndView champions(@RequestParam(defaultValue = "TOP", name = "position")String position) {
		ModelAndView mv = new ModelAndView();
		
		List<ChampPositionDto> champions = championService.getChampions(position);
		
		mv.addObject("champions", champions);
		mv.setViewName("champion/champions");
			
		return mv;
	}
	
	@PostMapping(path = "/champions/detail")
	public ModelAndView championDetail(@RequestParam(name = "champion") String champion) {
		ModelAndView mv = new ModelAndView();
		
		TotalChampDto totalChampDto = championService.getChampionDetail(champion);
		
		mv.addObject("championDetail", totalChampDto);
		mv.setViewName("champion/champion_detail");
		
		return mv;
	}
}
