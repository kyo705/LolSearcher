package com.lolsearcher.controller;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.ModelAndView;

import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.parameter.MatchParam;
import com.lolsearcher.model.dto.parameter.MostChampParam;
import com.lolsearcher.model.dto.parameter.SummonerUrlParam;
import com.lolsearcher.model.dto.rank.TotalRanks;
import com.lolsearcher.model.dto.mostchamp.MostChampDto;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.service.match.MatchService;
import com.lolsearcher.service.mostchamp.MostChampService;
import com.lolsearcher.service.rank.RankService;
import com.lolsearcher.service.summoner.SummonerService;

@RequiredArgsConstructor
@Controller
public class SummonerController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final long RENEW_TIME = 1000*60*5;
	
	private final SummonerService summonerService;
	private final RankService rankService;
	private final MatchService matchService;
	private final MostChampService mostChampService;
	
	@PostMapping(path = "/summoner")
	public ModelAndView getSummonerData(
			@ModelAttribute(name = "params") SummonerUrlParam requestParam,
			@RequestAttribute(name = "name") String name
	) {
		boolean isRenew = false;
		SummonerDto summoner = getSummoner(name);
		
		if(summoner==null) {
			summoner = summonerService.renewSummoner(name);
			isRenew = true;
		}
		long renewedTime = System.currentTimeMillis()-summoner.getLastRenewTimeStamp();
		if(renewedTime>=RENEW_TIME && requestParam.isRenew()) {
			summoner = summonerService.renewSummoner(name);
			isRenew = true;
		}
		String summonerId = summoner.getSummonerId();
		requestParam.setName(name);
		requestParam.setSummonerId(summoner.getSummonerId());
		
		TotalRanks ranks = getRanks(summonerId, isRenew);
		List<MatchDto> matches = getMatches(requestParam, isRenew);
		List<MostChampDto> mostchamps = getMostChamps(requestParam);
		
		ModelAndView mv = new ModelAndView("/summoner_data");
		mv.addObject("summoner", summoner);
		mv.addObject("rank", ranks);
		mv.addObject("matches", matches);
		mv.addObject("mostchamps", mostchamps);
		
		return mv;
	}
	
	private SummonerDto getSummoner(String name) throws WebClientResponseException, DataIntegrityViolationException {
		try {
			return summonerService.findDbSummoner(name);
		}catch (NoSummonerException e) {
			return null;
		}catch(MoreSummonerException e) {
			summonerService.updateDbSummoner(name);
			return summonerService.renewSummoner(name);
		}
	}
	
	private TotalRanks getRanks(String summonerId, boolean isRenew) {
		if(!isRenew) {
			return rankService.getLeague(summonerId);
		}
		try {
			return rankService.setLeague(summonerId);
		}catch(DataIntegrityViolationException e) {
			return rankService.getLeague(summonerId);
		}
	}
	
	private List<MatchDto> getMatches(SummonerUrlParam request, boolean isRenew) {
		List<MatchDto> matches = new ArrayList<>();
		
		if(isRenew) {
			matches.addAll(matchService.getRenewMatches(request.getSummonerId()));
		}
		MatchParam matchParam = MatchParam.builder()
				.name(request.getName())
				.champion(request.getChampion())
				.summonerId(request.getSummonerId())
				.gameType(request.getMatchGameType())
				.count(request.getCount())
				.build();

		matches.addAll(matchService.getOldMatches(matchParam));
		sortMatches(matches);

		return matches;
	}

	private List<MostChampDto> getMostChamps(SummonerUrlParam request) {
		MostChampParam mostChampParam = MostChampParam.builder()
				.season(request.getSeason())
				.gameQueue(request.getMostGameType())
				.summonerId(request.getSummonerId())
				.build();

		return mostChampService.getMostChamp(mostChampParam);
	}

	private void sortMatches(List<MatchDto> matches) {
		matches.sort((a,b)->{
			if(a.getGameEndTimestamp()-b.getGameEndTimestamp()>0){
				return -1;
			}
			if(a.getGameEndTimestamp()-b.getGameEndTimestamp()<0){
				return 1;
			}
			return 0;
		});
	}
}
