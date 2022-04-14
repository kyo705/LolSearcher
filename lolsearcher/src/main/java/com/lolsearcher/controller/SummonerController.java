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
	
	//param���� ���� �޴°��� �ƴ϶� command��ü�� ����ؼ� �ѹ��� param�� ����(DTO)
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(SummonerParamDto param) {
		
		ModelAndView mv = new ModelAndView();
		
		//����� ��û ���͸�(xxs ����)
		String unfilteredname = param.getName();
		String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //����,���� ���� �� ���͸�(���� ����)
		String filteredname = unfilteredname.replaceAll(regex, "");
		param.setName(filteredname);
		
		TotalRanksDto ranks;
		List<MatchDto> matches;
		List<MostChampDto> mostchamps;
		
		SummonerDto summonerdto = null;
		
		try {
			summonerdto = summonerservice.findSummoner(param.getName());
		}catch(WebClientResponseException e) {
			//��ȯ�� ������ ���� ��� Ŭ���̾�Ʈ���� ���� ������ ����.
			if(e.getStatusCode().toString().equals(error404)) {
				mv.addObject("params", param);
				mv.setViewName("error_name");
				return mv;
			}else if(e.getStatusCode().toString().equals(error429)) {
				mv.setViewName("error_manyreq");
				return mv;
			}
			
		}
		
		//DB���� ��ȯ�� ������ ���� ��� || Ŭ���̾�Ʈ���� ���� ���� ��ư�� ���� ���� ��û�� ������ ���
		if(summonerdto.getSummonerid()==null||param.isRenew()) {
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
			try {
				summonerdto = summonerservice.setSummoner(param.getName()); //riot �����κ��� ���� �޾ƿ�
				
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}catch(DataIntegrityViolationException e) {
				summonerdto = summonerservice.findSummoner(param.getName());
			}
			
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
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
			
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
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
		
		//��Ƽ������ �����ؼ� ���ÿ� ��������Ʈ, �������� �������� ����� ���
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
		
		//DB���� è�Ǿ� �·�,�ȷ� ���� ������ ��ȸ
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
