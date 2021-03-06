package com.lolsearcher.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MatchParamDtoBuilder;
import com.lolsearcher.domain.Dto.command.MostChampParamDtoBuilder;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.command.SummonerParamDto;
import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.service.InGameService;
import com.lolsearcher.service.SummonerService;

@Controller
public class SummonerController {
	
	private static final String error404 = "404 NOT_FOUND";
	private static final String error429 = "429 TOO_MANY_REQUESTS";
	
	private final SummonerService summonerService;
	
	private final InGameService inGameService;
	
	@Autowired
	public SummonerController(SummonerService summonerService, InGameService inGameService) {
		this.summonerService = summonerService;
		this.inGameService = inGameService;
	}
	
	//param값을 따로 받는것이 아니라 command객체를 사용해서 한번에 param값 받음(DTO)
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(SummonerParamDto param) {
		
		ModelAndView mv = new ModelAndView();
		
		//사용자 요청 필터링(xxs 방지)
		String unfilteredname = param.getName();
		String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
		String filteredname = unfilteredname.replaceAll(regex, "");
		param.setName(filteredname);
		
		//view로 전달될 데이터(Model)
		SummonerDto summonerdto = null;
		TotalRanksDto ranks = null;
		List<MatchDto> matches = new ArrayList<>();
		List<MostChampDto> mostchamps =null;
		
		
		try {
			summonerdto = summonerService.findDbSummoner(param.getName());
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
		}
		
		//DB에서 소환사 정보가 없는 경우 || 클라이언트에서 전적 갱신 버튼을 통해 갱신 요청이 들어오는 경우
		if(summonerdto==null||
				(param.isRenew()&&System.currentTimeMillis()-summonerdto.getLastRenewTimeStamp()>=5*60*1000)) {
			
			try {
				summonerdto = summonerService.setSummoner(param.getName());
			}catch(WebClientResponseException e) {
				if(e.getStatusCode().toString().equals(error404)) {
					//존재하지 않는 닉네임일 때
					summonerService.updateDbSummoner(param.getName());
					mv.addObject("name", param.getName());
					mv.setViewName("error_name");
					return mv;
				} else if(e.getStatusCode().toString().equals(error429)) {
					//요청 제한 횟수를 초과한 경우
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				//멀티 스레드 환경이기 때문에 DB에 중복 저장에 대한 예외 처리
				summonerdto = summonerService.findDbSummoner(param.getName());
			}
			
			
			// RANK 관련 데이터 RIOT 서버에서 데이터 받아와서 DB에 저장
			try {
				ranks = summonerService.setLeague(summonerdto);
			}catch(WebClientResponseException e) {
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				ranks = summonerService.getLeague(summonerdto);
			}
			
			
			// MATCH 관련 데이터 RIOT 서버에서 데이터 받아와서 DB에 저장
			try {
				List<MatchDto> recentMatches = summonerService.setMatches(summonerdto);
				matches.addAll(recentMatches);
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}
		}else {
			ranks = summonerService.getLeague(summonerdto);
		}
		
		param.setSummonerid(summonerdto.getSummonerid());
		
		MatchParamDto matchParamDto = new MatchParamDtoBuilder()
				.setName(param.getName())
				.setChampion(param.getChampion())
				.setSummonerid(param.getSummonerid())
				.setGametype(param.getMatchgametype())
				.setCount(param.getCount())
				.build();
		
		MostchampParamDto mostchampParamDto = new MostChampParamDtoBuilder()
				.setSeason(param.getSeason())
				.setGameQueue(param.getMostgametype())
				.setSummonerid(param.getSummonerid())
				.build();
		
		List<MatchDto> oldMatches = summonerService.getMatches(matchParamDto);
		matches.addAll(oldMatches);
		//matchid가 큰 순으로 정렬
		matches.sort((a,b)->{
			if(a.getGameEndTimestamp()-b.getGameEndTimestamp()>0){
				return -1;
			}else {
				return 1;
			}
		});
		
		mostchamps = summonerService.getMostChamp(mostchampParamDto);
		
		
		mv.addObject("params", param);
		mv.addObject("summoner", summonerdto);
		mv.addObject("rank", ranks);
		mv.addObject("matches", matches);
		mv.addObject("mostchamps", mostchamps);
		
		mv.setViewName("summoner");
		
		return mv;
	}
	
	@GetMapping(path = "/ingame")
	public ModelAndView inGame(String name) {
		ModelAndView mv = new ModelAndView();
		
		//사용자 요청 필터링(xxs 방지)
		String unfilteredname = name;
		String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
		String filteredname = unfilteredname.replaceAll(regex, "");
		
		//view로 전달될 데이터(Model)
		InGameDto inGameDto = null;
		SummonerDto summonerDto = null;
		
		try {
			summonerDto = summonerService.findDbSummoner(filteredname);
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}else if(e.getStatusCode().toString().equals(error404)) {
				mv.addObject("name", filteredname);
				mv.setViewName("error_name");
				return mv;
			}
		}
		
		try {
			inGameDto = inGameService.getInGame(summonerDto);
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error404)) {
				inGameService.removeDirtyInGame(summonerDto.getSummonerid(), -1);
				mv.addObject("summoner", summonerDto);
				mv.setViewName("error_ingame");
				return mv;
			}else if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
		}
		
		if(inGameDto == null) {
			inGameService.removeDirtyInGame(summonerDto.getSummonerid(), -1);
			
			mv.addObject("summoner", summonerDto);
			mv.setViewName("error_ingame");
			
			return mv;
		}else {
			inGameService.removeDirtyInGame(summonerDto.getSummonerid(), inGameDto.getGameId());
			
			mv.addObject("summoner", summonerDto);
			mv.addObject("ingame", inGameDto);
			mv.setViewName("inGame");
			
			return mv;
		}
	}
}
