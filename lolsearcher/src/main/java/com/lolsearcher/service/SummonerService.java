package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PreDestroy;

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
	
	private final static int seasonId = 22;
	private static final String soloRank = "RANKED_SOLO_5x5";
	private static final String flexRank = "RANKED_FLEX_SR";
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	private final ExecutorService executorService;
	private final ThreadService threadService;
	
	@Autowired
	public SummonerService(
			SummonerRepository summonerrepository, 
			RiotRestAPI riotApi,
			ExecutorService executorService,
			ThreadService threadService
			) {
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
		this.executorService =executorService;
		this.threadService = threadService;
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
					if(e.getStatusCode().value() == 400) { //아이디가 삭제되었을 경우
						summonerrepository.deleteSummoner(candi_summoner);
					}else if(e.getStatusCode().value() == 429) {
						System.out.println("예외");
						//요청 제한 횟수를 초과한 경우 controller에게 예외 처리 넘겨줌
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
		String lastmathid = summoner.getLastmatchid();
		String puuid = summoner.getPuuid();
		
		//아래 코드에서 REST 통신 에러(429) 발생 가능 =>발생 시 Controller에게 예외 처리를 위임함
		List<String> matchIds = riotApi.getAllMatchIds(puuid, lastmathid);
		
		
		// 최근 경기부터 REST 통신으로 데이터 가져와 DB에 저장 -> 해당 로직 실행 중 429에러(TOO MANY REQUEST) 발생 시
		// 스레드를 생성해서 해당 매치부터 마지막 매치까지 2분 후 요청해서 DB에 넣는 방식으로 반복
		List<Match> matches = new ArrayList<>();
		List<MatchDto> matchDtos = new ArrayList<>();
		
		if(matchIds.size()!=0) {
			summoner.setLastmatchid(matchIds.get(0));
			
			for(int i=0;i<matchIds.size();i++){
				String matchid = matchIds.get(i);
				if(!summonerrepository.findMatchid(matchid)) {
					
					try {
						Match match = riotApi.getmatch(matchid);
						matches.add(match);
						matchDtos.add(new MatchDto(match));
					}catch(WebClientResponseException e) {
						//요청 제한 횟수를 초과한 경우
						if(e.getStatusCode().value()==429) {
							//기존 가져온 match를 db에 저장
							Runnable saveMatchesToDB = makingRunnableToSaveMatches(matches);
							executorService.submit(saveMatchesToDB);
							
							//남은 매치 정보 저장하는 스레드 생성
							Runnable runnable = makingRunnableToSaveRemainingMatches(matchIds, i);		
							executorService.submit(runnable);
							
							return matchDtos;
						}
					}catch(NullPointerException e2) {
						//riot api에서 제공해주는 데이터가 다를 경우(이번 버전으로 rest api 제공하기 때문에 dto가 달라서 nullpointexception날림) 이전 데이터까지만 db에 반영
						break;
					}
				}
			}
		}
		
		Runnable saveMatchesToDB = makingRunnableToSaveMatches(matches);
		executorService.submit(saveMatchesToDB);
		
		return matchDtos;
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
	
	
//--------------------------------- 매치 정보 저장 스레드 관련 메소드 -------------------------------------	
	private Runnable makingRunnableToSaveMatches(List<Match> matches) {
		Runnable saveMatchesToDB = new Runnable() {
			@Override
			public void run() {
				threadService.saveMatches(matches);
			}
		};
		
		return saveMatchesToDB;
	}
	
	
	private Runnable makingRunnableToSaveRemainingMatches(List<String> matchIds, int start_index) {
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {			
				try {
					System.out.println("스레드 2분 정지");
					Thread.sleep(1000*60*2 + 2000);
					System.out.println("스레드 다시 시작");
				} catch (InterruptedException e2) {
					System.out.println("인터럽트 에러 발생");
				}
				
				List<Match> matches = new ArrayList<>();
				
				for(int i=start_index; i<matchIds.size(); i++) {
					if(threadService.readMatch(matchIds.get(i))==null) {
						try {
							Match match = riotApi.getmatch(matchIds.get(i));
							matches.add(match);
						}catch(WebClientResponseException e1) {
							threadService.saveMatches(matches);
							matches.clear();
							try {
								System.out.println("스레드 2분 정지");
								Thread.sleep(1000*60*2+2000);
								System.out.println("스레드 다시 시작");
							} catch (InterruptedException e2) {
								e2.printStackTrace();
							}
						}
					}
				}
				
				threadService.saveMatches(matches);
			}
		};
		
		return runnable;
	}
	
	@PreDestroy
	public void shutdownMatchSavingThreadPool() {
		executorService.shutdown();
	}
	
}
