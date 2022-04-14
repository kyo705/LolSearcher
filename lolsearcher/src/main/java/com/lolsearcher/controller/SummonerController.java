package com.lolsearcher.controller;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.CurrentGame.InGameDto;
import com.lolsearcher.domain.Dto.Summoner.MatchDto;
import com.lolsearcher.domain.Dto.Summoner.MostChampDto;
import com.lolsearcher.domain.Dto.Summoner.SummonerDto;
import com.lolsearcher.domain.Dto.Summoner.TotalRanksDto;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.command.SummonerParamDto;
import com.lolsearcher.service.InGameService;
import com.lolsearcher.service.SummonerService;

@Controller
public class SummonerController {

	private final SummonerService summonerservice;
	private final InGameService inGameService;
	
	private static final String error404 = "404 NOT_FOUND";
	private static final String error429 = "429 TOO_MANY_REQUESTS";
	
	@Autowired
	public SummonerController(SummonerService summonerservice, InGameService inGameService) {
		this.summonerservice = summonerservice;
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
		
		TotalRanksDto ranks;
		List<MatchDto> matches;
		List<MostChampDto> mostchamps;
		
		SummonerDto summonerdto = null;
		
		try {
			summonerdto = summonerservice.findSummoner(param.getName());
		}catch(WebClientResponseException e) {
			//소환사 정보가 없는 경우 클라이언트에게 에러 페이지 전달.
			if(e.getStatusCode().toString().equals(error404)) {
				mv.addObject("params", param);
				mv.setViewName("error_name");
				return mv;
			}else if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
			
		}
		
		//DB에서 소환사 정보가 없는 경우 || 클라이언트에서 전적 갱신 버튼을 통해 갱신 요청이 들어오는 경우
		if(summonerdto.getSummonerid()==null||param.isRenew()) {
			//RIOT 서버에서 데이터 받아와서 DB에 저장. 멀티 스레드 환경이기 때문에 DB에 중복 저장에 대한 예외 처리
			try {
				summonerdto = summonerservice.setSummoner(param.getName()); //riot 서버로부터 정보 받아옴
				
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				summonerdto = summonerservice.findSummoner(param.getName());
			}
			
			//RIOT 서버에서 데이터 받아와서 DB에 저장. 멀티 스레드 환경이기 때문에 DB에 중복 저장에 대한 예외 처리
			try {
				ranks = summonerservice.setLeague(summonerdto);
				
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
				ranks = null;
				
			}catch(DataIntegrityViolationException e) {
				ranks = summonerservice.getLeague(summonerdto);
			}
			
			//RIOT 서버에서 데이터 받아와서 DB에 저장. 멀티 스레드 환경이기 때문에 DB에 중복 저장에 대한 예외 처리
			try {
				summonerservice.setMatches(summonerdto);
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				System.out.println(e.getMessage());
			}
			
			
		}else {
			ranks = summonerservice.getLeague(summonerdto);
		}
		
		param.setSummonerid(summonerdto.getSummonerid());
		
		MatchParamDto match = new MatchParamDto(param.getName(),param.getSummonerid(),
				param.getChampion(),param.getMatchgametype(),param.getCount());
		
		MostchampParamDto mostchamp = new MostchampParamDto(param.getSummonerid(),
				param.getMostgametype(),param.getSeason());
		
		//멀티스레드 생성해서 동시에 전적리스트, 계정정보 가져오는 방법도 고려
		matches = summonerservice.getMatches(match);
		mostchamps = summonerservice.getMostchamp(mostchamp);
		
		mv.addObject("params", param);
		mv.addObject("summoner", summonerdto);
		mv.addObject("rank", ranks);
		mv.addObject("matches", matches);
		mv.addObject("mostchamps", mostchamps);
		
		mv.setViewName("summoner");
		
		return mv;
		
	}
	
	@GetMapping(path = "/champions")
	public ModelAndView championsStatics() {
		
		//DB에서 챔피언 승률,픽률 등의 데이터 조회
		return null;
	}
	
	@GetMapping(path = "/ingame")
	public ModelAndView inGame(String SummonerId,String name) {
		ModelAndView mv = new ModelAndView();
		InGameDto inGameDto = null;
		
		try {	
			inGameDto = inGameService.getInGame(SummonerId);
			
		}catch(WebClientResponseException e) {
			
			if(e.getStatusCode().toString().equals(error404)) {
				mv.addObject(name);
				mv.setViewName("error_ingame");
				return mv;
				
			}else if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
		}
		
		mv.addObject(inGameDto);
		return mv;
		}
}
