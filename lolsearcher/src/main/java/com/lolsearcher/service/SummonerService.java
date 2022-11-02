package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.RecentMatchesDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;
import com.lolsearcher.exception.SameNameExistException;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;


@Service
public class SummonerService {	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int seasonId = 22;
	private static final String soloRank = "RANKED_SOLO_5x5";
	private static final String flexRank = "RANKED_FLEX_SR";
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	private final ThreadService threadService;
	
	public SummonerService(
			ThreadService threadService,
			SummonerRepository summonerrepository, 
			RiotRestAPI riotApi) {
		this.threadService = threadService;
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
	}
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public SummonerDto findDbSummoner(String summonername) throws WebClientResponseException {
		SummonerDto summonerDto = null;
		
		List<Summoner> dbSummoners = summonerrepository.findSummonerByName(summonername);
		
		if(dbSummoners.size()==0) {
			return null;
		}else if(dbSummoners.size()==1) {
			summonerDto =  new SummonerDto(dbSummoners.get(0));
		}else {
			logger.error("'{}' 닉네임에 해당하는 유저가 둘 이상 존재",summonername);
			throw new SameNameExistException();
		}
		
		return summonerDto;
	}
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public void updateDbSummoner(String name) {
		List<Summoner> dbSummoners = summonerrepository.findSummonerByName(name);
		
		for(Summoner dbSummoner : dbSummoners) {
			try {
				Summoner renewedSummoner = riotApi.getSummonerById(dbSummoner.getId());
				logger.info("유저 '{}'의 닉네임이 '{}'->'{}'로 변경",
						renewedSummoner.getId(), name, renewedSummoner.getName());
				renewSummoner(dbSummoner, renewedSummoner);
			}catch(WebClientResponseException e) {
				if(e.getStatusCode().value()==400) {
					logger.info("'{}' 닉네임에 해당하는 유저는 현재 없음", name);
					summonerrepository.deleteSummoner(dbSummoner);
				}else {
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
	
	@Transactional(noRollbackFor = WebClientResponseException.class)
	public List<MatchDto> getRenewMatches(SummonerDto summonerdto) throws WebClientResponseException {
		
		String id = summonerdto.getSummonerid();
		
		Summoner summoner = summonerrepository.findSummonerById(id);
		String lastmathid = summoner.getLastmatchid(); //dto에 없는 값이기 때문에 entity를 가져와야함
		String puuid = summoner.getPuuid();
		
		//아래 코드에서 REST 통신 에러(429) 발생 가능 =>발생 시 Controller에게 예외 처리를 위임함
		List<String> matchIds = riotApi.getAllMatchIds(puuid, lastmathid);
		
		if(matchIds.size()!=0) {
			//DB에 Summoner 객체의 lastmatchid 필드 값 업데이트
			summoner.setLastmatchid(matchIds.get(0));
		}
		
		List<String> recent_match_ids = new ArrayList<>();
		
		for(String matchId : matchIds) {
			if(summonerrepository.findMatchById(matchId)==null) {
				recent_match_ids.add(matchId);
			}
		}
		
		
		RecentMatchesDto recentMatchesInfo = riotApi.getMatchesByNonBlocking(recent_match_ids);
		
		List<Match> recent_matches = recentMatchesInfo.getMatches();
		List<String> failMatchIds = recentMatchesInfo.getFailMatchIds();
		
		threadService.runSavingMatches(recent_matches);
		threadService.runRemainingMatches(failMatchIds);
		
		List<MatchDto> recent_match_dtos = new ArrayList<>();
		for(Match recent_match : recent_matches) {
			recent_match_dtos.add(new MatchDto(recent_match));
		}
		
		return recent_match_dtos;
	}
	

	@Transactional(readOnly = true)
	public List<MatchDto> getOldMatches(MatchParamDto matchdto){
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
