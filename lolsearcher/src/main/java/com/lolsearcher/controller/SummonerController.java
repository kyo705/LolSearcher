package com.lolsearcher.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MatchParamDtoBuilder;
import com.lolsearcher.domain.Dto.command.MostChampParamDtoBuilder;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.command.SummonerParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.exception.SameNameExistException;
import com.lolsearcher.filter.IpBanFilter;
import com.lolsearcher.service.SummonerService;

@Controller
public class SummonerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, Integer> banCount;
	
	private final SummonerService summonerService;
	
	private final ApplicationContext appContext;
	
	public SummonerController(SummonerService summonerService
								,ApplicationContext appContext) {
		
		banCount = new ConcurrentHashMap<String, Integer>();
		this.summonerService = summonerService;
		this.appContext = appContext;
	}
	
	
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(@ModelAttribute SummonerParamDto param, @RequestAttribute String name) {
		ModelAndView mv = new ModelAndView();
		param.setName(name);
		
		//view로 전달될 데이터(Model)
		SummonerDto summonerdto = null;
		TotalRanksDto ranks = null;
		List<MatchDto> matches = new ArrayList<>();
		List<MostChampDto> mostchamps = null;
		
		try {
			summonerdto = summonerService.findDbSummoner(name);
		}catch (SameNameExistException e) {
			summonerService.updateDbSummoner(name);
			summonerdto = summonerService.findDbSummoner(name);
		}
		
		
		//DB에서 소환사 정보가 없는 경우 || 클라이언트에서 전적 갱신 버튼을 통해 갱신 요청이 들어오는 경우
		if(summonerdto==null||
				(param.isRenew()&&System.currentTimeMillis()-summonerdto.getLastRenewTimeStamp()>=5*60*1000)) {
			
			summonerdto = summonerService.setSummoner(name);
			
			// RANK 관련 데이터 RIOT 서버에서 데이터 받아와서 DB에 저장
			try {
				ranks = summonerService.setLeague(summonerdto);
			}catch(DataIntegrityViolationException e) {
				ranks = summonerService.getLeague(summonerdto);
			}
			
			
			// MATCH 관련 데이터 RIOT 서버에서 데이터 받아와서 DB에 저장 setMatches를 isolation
			List<MatchDto> recentMatches = summonerService.getRenewMatches(summonerdto);
			matches.addAll(recentMatches);
			
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
		
		List<MatchDto> oldMatches = summonerService.getOldMatches(matchParamDto);
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
	
	
	
	@GetMapping(path = "/rejected")
	public ModelAndView rejected() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/rejected_ip");
		return mv;
	}

	
	//------------------해당 컨트롤러 예외 상황 처리------------------------------
	
	@ExceptionHandler(SameNameExistException.class)
    public ModelAndView getSameSummonerExistError(SameNameExistException e, ServletRequest req) {
		//로그 기록
		logger.error(e.getMessage());
		
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName("server_error");
		
		return mv;
	}
	
	@ExceptionHandler(WebClientResponseException.class)
    public ModelAndView getResponseError(WebClientResponseException e, ServletRequest req) {
		ModelAndView mv = new ModelAndView();
		
		logger.error("'{}' error occurred by 'Riot' game server", e.getStatusCode());
		
		if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)||
				e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
			mv.setViewName("error_name");
			
		}else if(e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
			mv.setViewName("error_manyreq");
			
		}else if(e.getStatusCode().equals(HttpStatus.BAD_GATEWAY)||
				e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)||
				e.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)||
				e.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)) {
			//500번대 에러
			mv.setViewName("error_game_server");
			
			return mv;
		}
		
		String user_ip = req.getRemoteAddr();
		
		banCount.put(user_ip, banCount.getOrDefault(user_ip, 0)+1);
		
		if(banCount.get(user_ip)>=30) {
			IpBanFilter banFilter = appContext.getBean(IpBanFilter.class);
			banFilter.addBanList(user_ip);
			
			logger.error(" ip : '{}' user is banned because of too many bad request", user_ip);
			mv.setViewName("rejected_ip");
			banCount.remove(user_ip);
			
			return mv;
		}
		
		
        return mv;
    }
}
