package com.lolsearcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.command.SummonerParamDto;
import com.lolsearcher.domain.Dto.currentgame.InGameDto;
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
	
	//param���� ���� �޴°��� �ƴ϶� command��ü�� ����ؼ� �ѹ��� param�� ����(DTO)
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(SummonerParamDto param) {
		
		ModelAndView mv = new ModelAndView();
		
		//����� ��û ���͸�(xxs ����)
		String unfilteredname = param.getName();
		String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //����,���� ���� �� ���͸�(���� ����)
		String filteredname = unfilteredname.replaceAll(regex, "");
		param.setName(filteredname);
		
		SummonerDto summonerdto = null;
		
		TotalRanksDto ranks = null;
		
		List<MatchDto> matches;
		
		List<MostChampDto> mostchamps;
		
		
		try {
			summonerdto = summonerService.findDbSummoner(param.getName());
		}catch(WebClientResponseException e) {
			if(e.getStatusCode().toString().equals(error429)) { //��û ���� Ƚ���� �ʰ��� ��� ��� �޼��� ����
				mv.setViewName("error_manyreq");
				return mv;
			}
		}
		
		
		//DB���� ��ȯ�� ������ ���� ��� || Ŭ���̾�Ʈ���� ���� ���� ��ư�� ���� ���� ��û�� ������ ���
		if(summonerdto==null||
				(param.isRenew()&&System.currentTimeMillis()-summonerdto.getLastRenewTimeStamp()>=5*60*1000)) {
			
			try {
				summonerdto = summonerService.setSummoner(param.getName());
			}catch(WebClientResponseException e) {
				if(e.getStatusCode().toString().equals(error404)) {
					//�������� �ʴ� �г����� ��
					mv.addObject("params", param);
					mv.setViewName("error_name");
					return mv;
				} else if(e.getStatusCode().toString().equals(error429)) {
					//��û ���� Ƚ���� �ʰ��� ���
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				//��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
				summonerdto = summonerService.findDbSummoner(param.getName());
			}
			
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
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
			
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
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
				//���� �ѹ� �ߺ� ������ �߻��ϴ� ���� �ƴ϶� ������ �߻��Ѵٸ�? => exception ����
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
		
		//��Ƽ������ �����ؼ� ���ÿ� ��������Ʈ, �������� �������� ����� ���
		matches = summonerService.getMatches(matchParamDto);
		
		mostchamps = summonerService.getMostchamp(mostchampParamDto);
		
		mv.addObject("params", param);
		mv.addObject("summoner", summonerdto);
		mv.addObject("rank", ranks);
		mv.addObject("matches", matches);
		mv.addObject("mostchamps", mostchamps);
		
		mv.setViewName("summoner");
		
		return mv;
		
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
		mv.setViewName("inGame");
		
		return mv;
	}
}
