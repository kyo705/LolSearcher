package com.lolsearcher.service;

import java.util.ArrayList;

import java.util.List;
import javax.persistence.EntityExistsException;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.MatchDto;
import com.lolsearcher.domain.Dto.MostChampDto;
import com.lolsearcher.domain.Dto.RankDto;
import com.lolsearcher.domain.Dto.SummonerDto;
import com.lolsearcher.domain.Dto.TotalRanksDto;
import com.lolsearcher.domain.Dto.command.matchparamDto;
import com.lolsearcher.domain.Dto.command.mostchampparamDto;
import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.repository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;

//트랜잭션의 isolation을 read_commited로 설정한 이유는 조회한 데이터가 중요한 데이터가 아니고 빠르게 정보를 전달하는것이 목적이기 때문에
//성능적인 측면에서 level 1(read_commited)로 설정하였다. 그래서 조회 중일 때 데이터가 저장이되면서 올바르지못한 정보를 조회하게 될 수도 있다.
//(특히 Match정보들을 20개 조회할때) 하지만 다시 조회하면 되는 큰 문제가 아니라 판단이 되어서 isolation을 1단계로 하였다.
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class Summonerservice {
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	
	public Summonerservice(SummonerRepository summonerrepository, RiotRestAPI riotApi) {
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
	}
	
	//summonerrepository에서 findsummonerByName 메소드를 만들지 않은 이유 : 
	//닉네임으로 DB에 조회하게 되면 갱신되지 않은 Summoner에 의해 DB에 중복된 닉네임이 있을 경우 발생. 
	//그러면 어떤 Summoner 객체를 가져와야할지 불분명, 버그 발생
	//따라서 라이엇 서버에서 제공하는 api를 활용하여 닉네임으로 id를 조회한 후 id값으로 DB에 조회 => 버그 발생 제거
	public SummonerDto findSummoner(String summonername) throws WebClientResponseException {
		Summoner apisummoner = riotApi.getSummoner(summonername);
		Summoner dbsummoner = summonerrepository.findsummonerById(apisummoner.getId());
		
		SummonerDto summonerDto;
		if(dbsummoner==null) {
			summonerDto = new SummonerDto();
		}else {
			summonerDto = new SummonerDto(dbsummoner);
		}
		
		return summonerDto;
	}
	
	public SummonerDto setSummoner(String summonername) throws EntityExistsException,WebClientResponseException {
		Summoner apisummoner = riotApi.getSummoner(summonername);
		SummonerDto summonerDto = new SummonerDto(apisummoner);
		if(apisummoner==null) {
			return summonerDto;
		}
		Summoner dbsummoner = summonerrepository.findsummonerById(apisummoner.getId());
		
		if(dbsummoner==null) {
			summonerrepository.savesummoner(apisummoner);
		}else if(dbsummoner.getRevisionDate()!=apisummoner.getRevisionDate()){
			//apisummoner(최신)와 dbsummoner(이전)를 동기화
			summonerrepository.updatesummoner(apisummoner,dbsummoner);
		}
		
		return summonerDto;
	}
	
	public TotalRanksDto setLeague(SummonerDto summonerdto) throws EntityExistsException,WebClientResponseException {
		String summonerid = summonerdto.getSummonerid();
		
		List<Rank> apileague = riotApi.getLeague(summonerid);
		List<Rank> dbleague = summonerrepository.findLeagueEntry(summonerid);
		
		if(dbleague.size()==0) {
			summonerrepository.saveLeagueEntry(apileague);
		}else {
			summonerrepository.updateLeagueEntry(apileague, dbleague);
		}
		
		//entity 객체를 dto로 바꿔주는 로직
		TotalRanksDto ranks = new TotalRanksDto();
		
		for(Rank r : apileague) {
			if(r.getCk().getQueueType().equals("RANKED_SOLO_5x5")) {
				RankDto solorank = new RankDto(r);
				ranks.setSolorank(solorank);
			}else {
				RankDto teamrank = new RankDto(r);
				ranks.setSolorank(teamrank);
			}
		}
			
			
		return ranks;
	}
	
	public TotalRanksDto getLeague(SummonerDto summonerdto){
		
		String summonerid = summonerdto.getSummonerid();
		
		List<Rank> ranks = summonerrepository.findLeagueEntry(summonerid);
		
		TotalRanksDto rank = new TotalRanksDto();
		
		for(Rank r : ranks) {
			
			if(r.getCk().getQueueType().equals("RANKED_SOLO_5x5")) {
				RankDto solorank = new RankDto(r);
				rank.setSolorank(solorank);
			}else {
				RankDto teamrank = new RankDto(r);
				rank.setTeamrank(teamrank);
			}
		}
		
		return rank;
	}
	//완성
	public void setMatches(SummonerDto summonerdto) throws EntityExistsException,WebClientResponseException {
		
		String id = summonerdto.getSummonerid();
		String puuid = summonerdto.getPuuid();
		Summoner summoner = summonerrepository.findsummonerById(id);
		String lastmathid = summoner.getLastmatchid();
		
		List<String> matchlist = riotApi.listofmatch(puuid, 0, "all", 0, 20, lastmathid);
		if(matchlist.size()!=0) {
			
			//영속성 컨텍스트의 기능으로 인해 summoner 개체의 값을 수정하면 자동으로 update쿼리문 나감
			summoner.setLastmatchid(matchlist.get(0)); 
		
			int i=0;
			for(String matchid : matchlist) {
				if(i>=20)
					break;
				
				if(!summonerrepository.findMatchid(matchid)) {
					Match match = riotApi.getmatch(matchid);
					summonerrepository.saveMatch(match);
				}
				i++;
			}
		}
	}
	
	public List<MatchDto> getMatches(matchparamDto match){
		//ArrayList의 동적 리사이징을 방지하기 위해 초기 사이즈 지정.
		List<MatchDto> matches = new ArrayList<>(20);
		
		String champion = match.getChampion();
		int gametype = match.getGametype();
		String summonerid = match.getSummonerid();
		int count = match.getCount();
		
		List<String> matchlist = summonerrepository.findMatchList(summonerid, gametype, champion, count);
		
		for(String matchid : matchlist) {
			matches.add(new MatchDto(summonerrepository.findMatch(matchid)));
		}
		
		return matches;
	}

	public List<MostChampDto> getMostchamp(mostchampparamDto param) {
		
		String summonerid = param.getSummonerid();
		int queue = param.getGamequeue();
		int season = param.getSeason();
		
		//모스트챔피언 5개만 받을 것이기 때문에 ArrayList 사이즈 default값인 10으로 둠
		List<MostChampDto> mostchamps = new ArrayList<>();
		
		List<String> champids = summonerrepository.findMostchampids(summonerid, queue, season);
		
		for(String champid : champids) {
			MostChampDto champ = summonerrepository.findChamp(summonerid, champid, queue, season);
			mostchamps.add(champ);
		}
		return mostchamps;
	}
}
