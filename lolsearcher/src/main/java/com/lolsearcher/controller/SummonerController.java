package com.lolsearcher.controller;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.domain.Dto.MatchDto;
import com.lolsearcher.domain.Dto.MostChampDto;
import com.lolsearcher.domain.Dto.SummonerDto;
import com.lolsearcher.domain.Dto.TotalRanksDto;
import com.lolsearcher.domain.Dto.command.matchparamDto;
import com.lolsearcher.domain.Dto.command.mostchampparamDto;
import com.lolsearcher.domain.Dto.command.summonerparamDto;
import com.lolsearcher.service.Summonerservice;

@Controller
public class SummonerController {

	private final Summonerservice summonerservice;
	
	@Autowired
	public SummonerController(Summonerservice summonerservice) {
		this.summonerservice = summonerservice;
	}
	
	//param���� ���� �޴°��� �ƴ϶� command��ü�� ����ؼ� �ѹ��� param�� ����(DTO)
	@PostMapping(path = "/summoner")
	public ModelAndView summonerdefault(summonerparamDto param) {
		
		ModelAndView mv = new ModelAndView();
		
		//����� ��û ���͸�(xxs ����)
		String unfilteredname = param.getName();
		String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //����,���� ���� �� ���͸�(���� ����)
		String filteredname = unfilteredname.replaceAll(regex, "");
		param.setName(filteredname);
		
		TotalRanksDto ranks;
		List<MatchDto> matches;
		List<MostChampDto> mostchamps;
		
		SummonerDto summonerdto = summonerservice.findSummoner(param.getName());
		
		//DB���� ��ȯ�� ������ ���� ��� || Ŭ���̾�Ʈ���� ���� ���� ��ư�� ���� ���� ��û�� ������ ���
		if(summonerdto.getSummonerid()==null||param.isRenew()) {
			//��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
			try {
				summonerdto = summonerservice.setSummoner(param.getName()); //riot �����κ��� ���� �޾ƿ�
			}catch(EntityExistsException e) {
				summonerdto = summonerservice.findSummoner(param.getName());
			}
			
			//��ȯ�� ������ ���� ��� Ŭ���̾�Ʈ���� ���� ������ ����.
			if(summonerdto.getSummonerid() == null) {
				mv.setViewName("error_name");
				return mv;
			}
			
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
			try {
				ranks = summonerservice.setLeague(summonerdto);
			}catch(EntityExistsException e) {
				ranks = summonerservice.getLeague(summonerdto);
			}
			//RIOT �������� ������ �޾ƿͼ� DB�� ����. ��Ƽ ������ ȯ���̱� ������ DB�� �ߺ� ���忡 ���� ���� ó��
			try {
				summonerservice.setMatches(summonerdto);
			}catch(EntityExistsException e) {
				System.out.println(e.getMessage());
			}
			
			
		}else {
			ranks = summonerservice.getLeague(summonerdto);
		}
		
		param.setSummonerid(summonerdto.getSummonerid());
		
		matchparamDto match = new matchparamDto(param.getName(),param.getSummonerid(),
				param.getChampion(),param.getMatchgametype(),param.getCount());
		
		mostchampparamDto mostchamp = new mostchampparamDto(param.getSummonerid(),
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
}
