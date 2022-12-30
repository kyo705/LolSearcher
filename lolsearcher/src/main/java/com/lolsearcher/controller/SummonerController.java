package com.lolsearcher.controller;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.constant.RenewMsConstants;
import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import lombok.RequiredArgsConstructor;
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
	
	private final SummonerService summonerService;
	private final RankService rankService;
	private final MatchService matchService;
	private final MostChampService mostChampService;
	
	@PostMapping(path = "/summoner")
	public ModelAndView getSummonerData(
			@ModelAttribute(name = "params") SummonerUrlParam requestParam,
			@RequestAttribute(name = "name") String name /* 필터된 닉네임 */
	) {
		boolean isRenew = false;
		SummonerDto summoner = getSummoner(name);
		
		if((summoner==null) || (requestParam.isRenew() &&
				System.currentTimeMillis() - summoner.getLastRenewTimeStamp() >= RenewMsConstants.SUMMONER_RENEW_MS)) {

			summoner = summonerService.renewSummoner(name);
			isRenew = true;
		}
		//요청 파라미터 값 갱신
		requestParam.setName(name);
		requestParam.setSummonerId(summoner.getSummonerId());

		
		TotalRanks ranks = getRanks(summoner.getSummonerId(), isRenew);

		List<MatchDto> matches = getMatches(requestParam, isRenew);

		List<MostChampDto> mostChampions = getMostChamps(requestParam);

		ModelAndView mv = new ModelAndView("/summoner_data");
		mv.addObject("summoner", summoner);
		mv.addObject("rank", ranks);
		mv.addObject("matches", matches);
		mv.addObject("mostchamps", mostChampions);
		
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
			List<String> recentMatchIds = matchService.getRecentMatchIds(request.getSummonerId());

			matches.addAll(matchService.getRenewMatches(recentMatchIds));
		}
		MatchParam matchParam = getMatchParam(request);

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

	private MatchParam getMatchParam(SummonerUrlParam requestParam) {

		return MatchParam.builder()
				.name(requestParam.getName())
				.champion(requestParam.getChampion())
				.summonerId(requestParam.getSummonerId())
				.gameType(requestParam.getMatchGameType())
				.count(requestParam.getCount())
				.build();
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
