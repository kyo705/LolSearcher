package com.lolsearcher.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.MatchDto;
import com.lolsearcher.domain.Dto.MostChampDto;
import com.lolsearcher.domain.Dto.SummonerDto;
import com.lolsearcher.domain.Dto.TotalRanksDto;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.command.SummonerParamDto;
import com.lolsearcher.service.Summonerservice;

@Controller
public class SummonerController {

	private final Summonerservice summonerservice;
	
	private static final String error404 = "404 NOT_FOUND";
	private static final String error429 = "429 TOO_MANY_REQUESTS";
	
	@Autowired
	public SummonerController(Summonerservice summonerservice) {
		this.summonerservice = summonerservice;
	}
	
	//param���� ���� �޴°��� �ƴ϶� command��ü�� ����ؼ� �ѹ��� param�� ����(DTO)
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(SummonerParamDto param) {
		
		ModelAndView mv = new ModelAndView();
		
		System.out.println(param.toString());
		
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
			System.out.println(summonerdto.getSummonerid());
			try {
				summonerdto = summonerservice.setSummoner(param.getName()); //riot �����κ��� ���� �޾ƿ�
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
			}
			
			try {
				ranks = summonerservice.setLeague(summonerdto);
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
				ranks = null;
			}
			
			try {
				summonerservice.setMatches(summonerdto);
			}catch(WebClientResponseException e) {
				System.out.println(e.getStatusCode());
				if(e.getStatusCode().toString().equals(error429)) {
					mv.setViewName("error_manyreq");
					return mv;
				}
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
		System.out.println(summonerdto.getLastRenewTimeStamp());
		
		mv.setViewName("summoner");
		
		return mv;
		
	}
	
	@GetMapping(path = "/champions")
	public ModelAndView championsStatics() {
		
		//DB���� è�Ǿ� �·�,�ȷ� ���� ������ ��ȸ
		return null;
	}
}
