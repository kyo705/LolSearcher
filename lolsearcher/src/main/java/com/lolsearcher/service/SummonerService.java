package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;


@Service
public class SummonerService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static int seasonId = 22;
	private static final String soloRank = "RANKED_SOLO_5x5";
	private static final String flexRank = "RANKED_FLEX_SR";
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	
	@Autowired
	public SummonerService(SummonerRepository summonerrepository, RiotRestAPI riotApi) {
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
	}
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public SummonerDto findDbSummoner(String summonername) throws WebClientResponseException {
		SummonerDto summonerDto = null;
		
		List<Summoner> dbSummoner = summonerrepository.findSummonerByName(summonername);
		
		if(dbSummoner.size()==1) {
			summonerDto =  new SummonerDto(dbSummoner.get(0));
		}else {
			Iterator<Summoner> summonerIter = dbSummoner.iterator();
			
			while(summonerIter.hasNext()) {
				Summoner candi_summoner = summonerIter.next();
				
				try {
					Summoner renew_Summoner = riotApi.getSummonerById(candi_summoner.getId());
					renewSummoner(candi_summoner, renew_Summoner);
					
					if(renew_Summoner.getName().equals(summonername)) {
						summonerDto = new SummonerDto(renew_Summoner);
					}
				}catch (WebClientResponseException e) {
					if(e.getStatusCode().value() == 400) { //???????????? ??????????????? ??????
						summonerrepository.deleteSummoner(candi_summoner);
					}else if(e.getStatusCode().value() == 429) {
						//?????? ?????? ????????? ????????? ?????? controller?????? ?????? ?????? ?????????
						throw e;
					}
				}
			}
		}
		
		return summonerDto;
	}
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public void updateDbSummoner(String name) {
		List<Summoner> dbSummoners = summonerrepository.findSummonerByName(name);
		
		for(Summoner dbSummoner : dbSummoners) {
			try {
				Summoner renewedSummoner = riotApi.getSummonerById(dbSummoner.getId());
				renewSummoner(dbSummoner, renewedSummoner);
			}catch(WebClientResponseException e) {
				if(e.getStatusCode().value()==400) {
					summonerrepository.deleteSummoner(dbSummoner);
				}else if(e.getStatusCode().value()==429) {
					throw e;
				}
			}
		}
	}
	
	@Transactional
	public SummonerDto setSummoner(String summonername) throws WebClientResponseException, DataIntegrityViolationException {
		Summoner apisummoner = riotApi.getSummonerByName(summonername);
		SummonerDto summonerDto = null;
		try {
			Summoner summoner = summonerrepository.findSummonerById(apisummoner.getId());
			renewSummoner(summoner, apisummoner);
			summonerDto = new SummonerDto(summoner);
		}catch(EmptyResultDataAccessException e) {
			summonerrepository.saveSummoner(apisummoner);
			summonerDto = new SummonerDto(apisummoner);
		}
		
		return summonerDto;
	}
	
	@Transactional
	public TotalRanksDto setLeague(SummonerDto summonerdto) throws WebClientResponseException, DataIntegrityViolationException {
		String summonerid = summonerdto.getSummonerid();
		
		List<RankDto> apiRanks = riotApi.getLeague(summonerid);
		
		List<Rank> dbRanks = new ArrayList<>();
		TotalRanksDto totalRanks = new TotalRanksDto();
		
		for(RankDto r : apiRanks) {
			r.setSeasonId(seasonId);
			
			if(r.getQueueType().equals(soloRank)) {
				totalRanks.setSolorank(r);
			}else {
				totalRanks.setTeamrank(r);
			}
			
			dbRanks.add(new Rank(r));
		}
		
		summonerrepository.saveRanks(dbRanks);
			
		return totalRanks;
	}
	
	@Transactional(readOnly = true)
	public TotalRanksDto getLeague(SummonerDto summonerdto){
		
		String summonerid = summonerdto.getSummonerid();
		
		RankCompKey soloRankKey = new RankCompKey(summonerid, soloRank, seasonId);
		RankCompKey flexRankKey = new RankCompKey(summonerid, flexRank, seasonId);
		
		Rank soloRank = summonerrepository.findRank(soloRankKey);
		Rank flexRank = summonerrepository.findRank(flexRankKey);
		
		TotalRanksDto ranksDto = new TotalRanksDto();
		if(soloRank!=null) {
			ranksDto.setSolorank(new RankDto(soloRank));
		}
		if(flexRank!=null) {
			ranksDto.setTeamrank(new RankDto(flexRank));
		}
		
		return ranksDto;
	}
	
	@Transactional
	public List<MatchDto> setMatches(SummonerDto summonerdto) throws WebClientResponseException {
		
		String id = summonerdto.getSummonerid();
		
		Summoner summoner = summonerrepository.findSummonerById(id);
		String lastmathid = summoner.getLastmatchid(); //dto??? ?????? ????????? ????????? entity??? ???????????????
		String puuid = summoner.getPuuid();
		
		//?????? ???????????? REST ?????? ??????(429) ?????? ?????? =>?????? ??? Controller?????? ?????? ????????? ?????????
		List<String> matchIds = riotApi.getAllMatchIds(puuid, lastmathid);
		
		if(matchIds.size()!=0) {
			summoner.setLastmatchid(matchIds.get(0));
		}
		
		List<String> recent_match_ids = new ArrayList<>();
		
		for(String matchId : matchIds) {
			if(!summonerrepository.findMatchid(matchId)) {
				recent_match_ids.add(matchId);
			}
		}
		
		List<MatchDto> recent_match_dtos = new ArrayList<>();
		
		List<Match> recent_matches = riotApi.getMatches(recent_match_ids);
		for(Match recent_match : recent_matches) {
			recent_match_dtos.add(new MatchDto(recent_match));
		}
		
		return recent_match_dtos;
	}
	

	@Transactional(readOnly = true)
	public List<MatchDto> getMatches(MatchParamDto matchdto){
		String champion = matchdto.getChampion();
		int gametype = matchdto.getGametype();
		String summonerid = matchdto.getSummonerid();
		int count = matchdto.getCount();
		
		List<Match> matchlist = summonerrepository.findMatchList(summonerid, gametype, champion, count);
		
		List<MatchDto> matchlistDto = new ArrayList<>();
		for(Match match : matchlist) {
			matchlistDto.add(new MatchDto(match));
		}
		return matchlistDto;
	}

	@Transactional(readOnly = true)
	public List<MostChampDto> getMostChamp(MostchampParamDto param) {
		
		String summonerid = param.getSummonerid();
		int queue = param.getGamequeue();
		int season = param.getSeason();
		
		List<MostChampDto> mostchamps = new ArrayList<>();
		
		List<String> champids = summonerrepository.findMostchampids(summonerid, queue, season);
		
		for(String champid : champids) {
			MostChampDto champ = summonerrepository.findChamp(summonerid, champid, queue, season);
			mostchamps.add(champ);
		}
		
		return mostchamps;
	}
	
	private void renewSummoner(Summoner before, Summoner after) {
		before.setRevisionDate(after.getRevisionDate());
		before.setName(after.getName());
		before.setProfileIconId(after.getProfileIconId());
		before.setSummonerLevel(after.getSummonerLevel());
		before.setLastRenewTimeStamp(after.getLastRenewTimeStamp());
	}
}
