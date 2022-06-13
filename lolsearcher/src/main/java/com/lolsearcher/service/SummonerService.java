package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;


@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
@Service
public class SummonerService {
	
	private final static int seasonId = 22;
	private static final String soloRank = "RANKED_SOLO_5x5";
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	
	private final ApplicationContext applicationContext;
	
	@Autowired
	public SummonerService(SummonerRepository summonerrepository, RiotRestAPI riotApi,ApplicationContext applicationContext) {
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
		this.applicationContext = applicationContext;
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
					summonerrepository.saveSummoner(renew_Summoner);
					
					if(renew_Summoner.getName().equals(summonername)) {
						summonerDto = new SummonerDto(renew_Summoner);
					}
				}catch (WebClientResponseException e) {
					
					if(e.getStatusCode().value() == 404) //아이디가 삭제되었을 경우
						summonerrepository.deleteSummoner(candi_summoner);
					else if(e.getStatusCode().value() == 429) //요청 제한 횟수를 초과한 경우 controller에게 예외 처리 넘겨줌
						throw e;
				}
				
			}
			
		}
		
		return summonerDto;
	}
	
	public SummonerDto setSummoner(String summonername) throws WebClientResponseException, DataIntegrityViolationException {
		Summoner apisummoner = riotApi.getSummonerByName(summonername);
		
		summonerrepository.saveSummoner(apisummoner);
		
		SummonerDto summonerDto = new SummonerDto(apisummoner);
		
		return summonerDto;
	}
	
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
		
		summonerrepository.saveLeagueEntry(dbRanks);
			
		return totalRanks;
	}
	
	public TotalRanksDto getLeague(SummonerDto summonerdto){
		
		String summonerid = summonerdto.getSummonerid();
		
		List<Rank> ranks = summonerrepository.findLeagueEntry(summonerid, seasonId);
		
		TotalRanksDto ranksDto = new TotalRanksDto();
		
		for(Rank r : ranks) {
			if(r.getCk().getQueueType().equals("RANKED_SOLO_5x5")) {
				RankDto solorank = new RankDto(r);
				ranksDto.setSolorank(solorank);
			}else {
				RankDto teamrank = new RankDto(r);
				ranksDto.setTeamrank(teamrank);
			}
		}
		
		return ranksDto;
	}
	
	public void setMatches(SummonerDto summonerdto) throws WebClientResponseException, DataIntegrityViolationException {
		
		String id = summonerdto.getSummonerid();
		
		Summoner summoner = summonerrepository.findSummonerById(id);
		String lastmathid = summoner.getLastmatchid();
		String puuid = summoner.getPuuid();
		
		//아래 코드에서 REST 통신 에러(429) 발생 가능 =>발생 시 Controller에게 예외 처리를 위임함
		List<String> matchlist = riotApi.listofmatch(puuid, 0, "all", 0, 20, lastmathid);
		
		
		// 최근 경기부터 REST 통신으로 데이터 가져와 DB에 저장 -> 해당 로직 실행 중 429에러(TOO MANY REQUEST) 발생 시
		// 스레드를 생성해서 해당 매치부터 마지막 매치까지 2분 후 요청해서 DB에 넣는 방식으로 반복
		if(matchlist.size()!=0) {
			summoner.setLastmatchid(matchlist.get(0));
			
			for(String matchid : matchlist) {
				if(!summonerrepository.findMatchid(matchid)) {
					
					try {
						Match match = riotApi.getmatch(matchid);
						summonerrepository.saveMatch(match);
					}catch(WebClientResponseException e) {
						System.out.println(e.getStatusCode());
						//요청 제한 횟수를 초과한 경우
						if(e.getStatusCode().value()==429) {
							//남은 매치 정보 저장하는 스레드 생성
							Thread thread = makingMatchSaveThread(matchlist, matchid);
							thread.start();
						}
						
						break; //for문 빠져나옴
					}
				}
				
			}
			
		}
	}
	

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
	
	
//--------------------------------- 매치 정보 저장 스레드 관련 메소드 -------------------------------------	
	private Thread makingMatchSaveThread(List<String> matchlist, String stoppedMatchId) {
		
		Thread thread = new Thread(
				()-> {
				System.out.println("스레드 시작");
				
				int start = 0;
				for(String matchId : matchlist) {
					if(!matchId.equals(stoppedMatchId)) {
						start++;
						continue;
					}else
						break;
				}
				
				try {
					System.out.println("스레드 2분 정지");
					Thread.sleep(1000*60*2 + 2000);
					System.out.println("스레드 다시 시작");
				} catch (InterruptedException e2) {
					System.out.println("인터럽트 에러 발생");
				}
				
				saveRemainingMatch(start, matchlist);
				System.out.println("매치 정보들 저장 완료");
				System.out.println("스레드 종료");
			}
		);
		
		return thread;
		
	}
	
	private void saveRemainingMatch(int start, List<String> matchIds){
		
		EntityManagerFactory emf =  applicationContext.getBean(EntityManagerFactory.class);
		EntityManager em = emf.createEntityManager();
		
		EntityTransaction et = em.getTransaction();
		et.begin();
		
		for(int i=start; i<matchIds.size(); i++) {
			
			if(em.find(Match.class, matchIds.get(i))==null) {
				try {
					Match match2 = riotApi.getmatch(matchIds.get(i));
					em.persist(match2);
				}catch(WebClientResponseException e1) {
					em.flush();
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
		
		et.commit();
		
		em.close();
	}
	
}
