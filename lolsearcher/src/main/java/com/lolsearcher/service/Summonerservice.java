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

//Ʈ������� isolation�� read_commited�� ������ ������ ��ȸ�� �����Ͱ� �߿��� �����Ͱ� �ƴϰ� ������ ������ �����ϴ°��� �����̱� ������
//�������� ���鿡�� level 1(read_commited)�� �����Ͽ���. �׷��� ��ȸ ���� �� �����Ͱ� �����̵Ǹ鼭 �ùٸ������� ������ ��ȸ�ϰ� �� ���� �ִ�.
//(Ư�� Match�������� 20�� ��ȸ�Ҷ�) ������ �ٽ� ��ȸ�ϸ� �Ǵ� ū ������ �ƴ϶� �Ǵ��� �Ǿ isolation�� 1�ܰ�� �Ͽ���.
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class Summonerservice {
	
	private final SummonerRepository summonerrepository;
	private final RiotRestAPI riotApi;
	
	public Summonerservice(SummonerRepository summonerrepository, RiotRestAPI riotApi) {
		this.summonerrepository = summonerrepository;
		this.riotApi = riotApi;
	}
	
	//summonerrepository���� findsummonerByName �޼ҵ带 ������ ���� ���� : 
	//�г������� DB�� ��ȸ�ϰ� �Ǹ� ���ŵ��� ���� Summoner�� ���� DB�� �ߺ��� �г����� ���� ��� �߻�. 
	//�׷��� � Summoner ��ü�� �����;����� �Һи�, ���� �߻�
	//���� ���̾� �������� �����ϴ� api�� Ȱ���Ͽ� �г������� id�� ��ȸ�� �� id������ DB�� ��ȸ => ���� �߻� ����
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
			//apisummoner(�ֽ�)�� dbsummoner(����)�� ����ȭ
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
		
		//entity ��ü�� dto�� �ٲ��ִ� ����
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
	//�ϼ�
	public void setMatches(SummonerDto summonerdto) throws EntityExistsException,WebClientResponseException {
		
		String id = summonerdto.getSummonerid();
		String puuid = summonerdto.getPuuid();
		Summoner summoner = summonerrepository.findsummonerById(id);
		String lastmathid = summoner.getLastmatchid();
		
		List<String> matchlist = riotApi.listofmatch(puuid, 0, "all", 0, 20, lastmathid);
		if(matchlist.size()!=0) {
			
			//���Ӽ� ���ؽ�Ʈ�� ������� ���� summoner ��ü�� ���� �����ϸ� �ڵ����� update������ ����
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
		//ArrayList�� ���� ������¡�� �����ϱ� ���� �ʱ� ������ ����.
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
		
		//��Ʈè�Ǿ� 5���� ���� ���̱� ������ ArrayList ������ default���� 10���� ��
		List<MostChampDto> mostchamps = new ArrayList<>();
		
		List<String> champids = summonerrepository.findMostchampids(summonerid, queue, season);
		
		for(String champid : champids) {
			MostChampDto champ = summonerrepository.findChamp(summonerid, champid, queue, season);
			mostchamps.add(champ);
		}
		return mostchamps;
	}
}
