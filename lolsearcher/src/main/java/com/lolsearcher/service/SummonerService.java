package com.lolsearcher.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.domain.entity.match.Match;
import com.lolsearcher.domain.entity.rank.Rank;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;

//Ʈ������� isolation�� read_commited�� ������ ������ ��ȸ�� �����Ͱ� �߿��� �����Ͱ� �ƴϰ� ������ ������ �����ϴ°��� �����̱� ������
//�������� ���鿡�� level 1(read_commited)�� �����Ͽ���. �׷��� ��ȸ ���� �� �����Ͱ� �����̵Ǹ鼭 �ùٸ������� ������ ��ȸ�ϰ� �� ���� �ִ�.
//(Ư�� Match�������� 20�� ��ȸ�Ҷ�) ������ �ٽ� ��ȸ�ϸ� �Ǵ� ū ������ �ƴ϶� �Ǵ��� �Ǿ isolation�� 1�ܰ�� �Ͽ���.
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
					
					if(e.getStatusCode().value() == 404) //���̵� �����Ǿ��� ���
						summonerrepository.deleteSummoner(candi_summoner);
					else if(e.getStatusCode().value() == 429) //��û ���� Ƚ���� �ʰ��� ���
						throw e; 							//controller���� ���� ó�� �Ѱ���
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
		String puuid = summonerdto.getPuuid();
		
		Summoner summoner = summonerrepository.findSummonerById(id);
		String lastmathid = summoner.getLastmatchid();
		
		//�ش� �������� REST ��� ����(429) �߻� �� Controller���� ���� ó���� ������
		List<String> matchlist = riotApi.listofmatch(puuid, 0, "all", 0, 20, lastmathid);
		
		//RENEW ��� : 
		// �ֱ� ������ REST ������� ������ ������ DB�� ���� -> �������� �� 429���� �߻� ��
		// �����带 �����ؼ� �ش� ��ġ���� ������ ��ġ���� 2�� �� ��û�ؼ� DB�� �ִ� ������� �ݺ�
		
		if(matchlist.size()!=0) {
			summoner.setLastmatchid(matchlist.get(0));
			System.out.println(matchlist.size());
			for(String matchid : matchlist) {
				if(!summonerrepository.findMatchid(matchid)) {
					
					try {
						Match match = riotApi.getmatch(matchid);
						summonerrepository.saveMatch(match);
					}catch(WebClientResponseException e) {
						System.out.println(e.getStatusCode());
						//��û ���� Ƚ���� �ʰ��� ���
						if(e.getStatusCode().value()==429) {
							//2�� sleep �� ��ġ ����Ʈ�� db �����ϴ� ���
							Thread thread = new Thread(
									()-> {
									System.out.println("������ ����");
									List<String> matchIds = matchlist;
									int start = 0;
									for(String matchId : matchIds) {
										if(!matchId.equals(matchid)) {
											start++;
											continue;
										}else
											break;
									}
									
									try {
										System.out.println("������ 2�� ����");
										Thread.sleep(1000*60*2 + 2000);
										System.out.println("������ �ٽ� ����");
									} catch (InterruptedException e2) {
										// TODO Auto-generated catch block
										System.out.println("���ͷ�Ʈ ���� �߻�");
									}
									
									saveRemainingMatch(start, matchIds);
									System.out.println("��ġ ������ ���� �Ϸ�");
									System.out.println("������ ����");
								}
							);
							
							thread.start();
							
						}
						
						break;
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

	public List<MostChampDto> getMostchamp(MostchampParamDto param) {
		
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
	
	
	@Transactional
	public void saveRemainingMatch(int start, List<String> matchIds){
		
		EntityManagerFactory emf =  applicationContext.getBean(EntityManagerFactory.class);
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		
		for(int i=start; i<matchIds.size(); i++) {
			
			if(em.find(Match.class, matchIds.get(i))==null) {
				try {
					Match match2 = riotApi.getmatch(matchIds.get(i));
					em.persist(match2);
					//summonerrepository.saveMatch(match2);
				}catch(WebClientResponseException e1) {
					em.flush();
					try {
						Thread.sleep(1000*60*2+2000);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
			}
			
		}
		
		em.flush();
		em.close();
	}
	
}
