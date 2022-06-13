package com.lolsearcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.command.MatchParamDto;
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
		String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
		String filteredname = unfilteredname.replaceAll(regex, "");
		param.setName(filteredname);
		
		SummonerDto summonerdto = null;
		
		TotalRanksDto ranks = null;
		
		List<MatchDto> matches;
		
		List<MostChampDto> mostchamps;
		
		
		try {
			summonerdto = summonerService.findDbSummoner(param.getName());
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error429)) { //요청 제한 횟수를 초과한 경우 대기 메세지 전달
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
					mv.addObject("params", param);
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
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				ranks = summonerService.getLeague(summonerdto);
			}
			
			// MATCH 관련 데이터 RIOT 서버에서 데이터 받아와서 DB에 저장
			try {
				summonerService.setMatches(summonerdto);
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				System.out.println(e.getMessage());
				//만약 한번 중복 저장이 발생하는 것이 아니라 여러번 발생한다면? => exception 터짐
				//=> how to solve this problem???
				summonerService.setMatches(summonerdto);
			}
		}else {
			ranks = summonerService.getLeague(summonerdto);
		}
		
		param.setSummonerid(summonerdto.getSummonerid());
		
		MatchParamDto matchParamDto = new MatchParamDto(param.getName(),param.getSummonerid(),
				param.getChampion(),param.getMatchgametype(),param.getCount());
		
		MostchampParamDto mostchampParamDto = new MostchampParamDto(param.getSummonerid(),
				param.getMostgametype(),param.getSeason());
		
		
		matches = summonerService.getMatches(matchParamDto);
		
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
		
		InGameDto inGameDto = null;
		
		SummonerDto summonerDto = null;
		
		try {
			summonerDto = summonerService.findDbSummoner(name);
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
		}
		
		try {
			inGameDto = inGameService.getInGame(summonerDto);
			inGameService.removeDirtyInGame(summonerDto.getSummonerid(), inGameDto.getGameId());
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error404)) {
				inGameService.removeDirtyInGame(summonerDto.getSummonerid());
				mv.addObject("summoner", summonerDto);
				mv.setViewName("error_ingame");
				return mv;
			}else if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
		}catch(Exception e) { //멀티 스레드에서 동시에 entity를 제거할 때 발생하는 예외 => 다시 한번 비지니스 로직 실행하여 예외 처리
			inGameDto = inGameService.getInGame(summonerDto);
		}
		
		if(inGameDto == null) {
			inGameService.removeDirtyInGame(summonerDto.getSummonerid());
			mv.addObject("summoner", summonerDto);
			mv.setViewName("error_ingame");
		}
		
		mv.addObject("summoner", summonerDto);
		mv.addObject("ingame", inGameDto);
		mv.setViewName("inGame");
		
		return mv;
	}
}
