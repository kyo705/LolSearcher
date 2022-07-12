package com.lolsearcher.restapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;
import com.lolsearcher.domain.entity.summoner.match.MemberCompKey;
import com.lolsearcher.service.ThreadService;

import reactor.core.publisher.Mono;

public class RiotRestApiv2 implements RiotRestAPI{

	private static final String key = "RGAPI-2a0ac3ef-7f65-4854-97d4-54e2c7b3dbab";
	
	private WebClient webclient;
	private final ExecutorService executorService;
	private final ThreadService threadService;
	
	public RiotRestApiv2(
			WebClient webclient,
			ExecutorService executorService,
			ThreadService threadService
			) {
		this.webclient = webclient;
		this.executorService =executorService;
		this.threadService = threadService;
	}

	@Override
	public Summoner getSummonerById(String id) throws WebClientResponseException {
		
		Summoner summoner = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/summoner/v4/summoners/"+id+"?api_key="+key)
				.retrieve().bodyToMono(Summoner.class).block();
		
		summoner.setLastmatchid("");
        summoner.setLastRenewTimeStamp(System.currentTimeMillis());
        summoner.setLastInGameSearchTimeStamp(0);
		
		return summoner;
	}

	@Override
	public Summoner getSummonerByName(String summonername) throws WebClientResponseException {
		
		Summoner summoner = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/summoner/v4/summoners/by-name/"+summonername+"?api_key="+key)
				.retrieve().bodyToMono(Summoner.class).block();
		
		summoner.setLastmatchid("");
        summoner.setLastRenewTimeStamp(System.currentTimeMillis());
        summoner.setLastInGameSearchTimeStamp(0);
		
		return summoner;
	}
	
	@Override
	public List<String> getAllMatchIds(String puuid, String lastMatchId) throws WebClientResponseException {
		List<String> matchidlist = new ArrayList<>();
		
		boolean plag = true;
		String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?";
		int starts = 0;
		int counts = 100;
		
		while(plag) {
			String[] matchids = webclient.get()
					.uri(uri + "start="+starts+"&count="+counts+"&api_key="+key)
					.retrieve()
					.bodyToMono(String[].class)
					.block();
			
			if(matchids.length!=100) {
				plag = false;
			}
			
			for(String matchid : matchids) {
				if(matchid.equals(lastMatchId)) {
					plag = false;
					break;
				}else {
					matchidlist.add(matchid);
				}
			}
			
			starts += counts;
		}
			
		
		return matchidlist;
	}
	
	@Override
	public List<String> getMatchIds(String puuid, int queue, String type,
			int start, int count, String lastmatchid) throws WebClientResponseException {
		
		List<String> matchidlist = new ArrayList<>();
		
		String uri;
		int starts = start;
		int counts = count;
		
		if(queue==-1) {
			uri = "https://asia.api.riotgames.com"
					+ "/lol/match/v5/matches/by-puuid/"+puuid+"/ids?";
		}else {
			uri = "https://asia.api.riotgames.com"
					+ "/lol/match/v5/matches/by-puuid/"+puuid+"/ids?queue="+queue+"&";
		}
		
		while(count>0) {
			if(count>100) {
				counts = 100;
				count -= 100;
			}else {
				counts = count;
				count = 0;
			}
			
			String[] matchids = webclient.get()
					.uri(uri + "start="+starts+"&count="+counts+"&api_key="+key)
					.retrieve()
					.bodyToMono(String[].class)
					.block();
			
			for(String matchid : matchids) {
				if(matchid.equals(lastmatchid)) {
					count = 0;
					break;
				}else {
					matchidlist.add(matchid);
				}
			}
			
			starts += counts;
		}
		
		return matchidlist;
	}
	
	@SuppressWarnings("rawtypes")
	public Match getOneMatch(String matchId){
		
		Map json = webclient.get().uri("https://asia.api.riotgames.com"
				+ "/lol/match/v5/matches/"+matchId+"?api_key="+key)
				.retrieve().bodyToMono(Map.class).block();
		
		Match match = parsingMatchJson(json);
		
		return match;
	}
	
	public List<Match> getMatches(List<String> matchIds) throws WebClientResponseException{
		List<Match> matches = new ArrayList<>();
		List<String> fail_matchIds = new ArrayList<>();
		
		int count = 0;
		for(String matchId : matchIds) {
			if(count>=20)
				break;
			
			webclient.get().uri("https://asia.api.riotgames.com"+ 
			"/lol/match/v5/matches/"+matchId+"?api_key="+key)
			.retrieve()
			.bodyToMono(Map.class)
			.onErrorResume(e -> {
				if(e instanceof WebClientResponseException) {
					if(((WebClientResponseException) e).getStatusCode().value() == 429) {
						fail_matchIds.add(matchId);
					}
				}
				
				return Mono.just(null);
			})
			.subscribe(result->{
				if(result!=null) {
					Match match = parsingMatchJson(result);
					matches.add(match);
				}
			});
			
			count++;
		}
		
		//webclient의 요청에 대한 응답을 다 받을때까지 기다리는 로직
		while(matches.size()+fail_matchIds.size()!=count) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//matches를 DB에 저장하는 스레드 생성 및 실행
		Runnable saveMatchesToDB = makingRunnableToSaveMatches(matches);
		executorService.submit(saveMatchesToDB);
		
		//429로 인해 match 데이터 조회 실패한 로직 다시 실행
		Runnable saveRemainingMatches1 = makingRunnableToSaveRemainingMatches(fail_matchIds, 0);
		executorService.submit(saveRemainingMatches1);
		
		Runnable saveRemainingMatches2 = makingRunnableToSaveRemainingMatches(matchIds, count);
		executorService.submit(saveRemainingMatches2);
		
		return matches;
	}


	@Override
	public List<RankDto> getLeague(String summonerid) throws WebClientResponseException {
		
		List<RankDto> ranks = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/league/v4/entries/by-summoner/"+summonerid+"?api_key="+key)
		.retrieve().bodyToFlux(RankDto.class).collectList().block();
		
		return ranks;
	}

	
	@Override
	public InGameDto getInGameBySummonerId(String summonerid) throws WebClientResponseException {
		
		InGameDto currentGame = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/spectator/v4/active-games/by-summoner/"
				+summonerid+"?api_key="+key)
				.retrieve().bodyToMono(InGameDto.class).block();
		
		List<CurrentGameParticipantDto> curParticipants = currentGame.getParticipants();
		for(int i=0;i<10;i++) {
			curParticipants.get(i).setNum(i);
		}
		
		return currentGame;
	}
	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Match parsingMatchJson(Map json) {
		Match match = new Match();
		Map metadata = (Map) json.get("metadata");
		String matchId = (String)metadata.get("matchId");
		
		Map info = (Map) json.get("info");
		
		match.setGameDuration((int) info.get("gameDuration"));
        match.setMatchId(matchId);
        match.setGameEndTimestamp((long) info.get("gameEndTimestamp"));
        match.setQueueId((int) info.get("queueId"));
        
        String version = (String)info.get("gameVersion");
        String s = version.substring(0,2);
        int season = Integer.parseInt(s);
        match.setSeason(season);
        
        List<Map> participants = (ArrayList)info.get("participants");
        int i = 0;
        for(Map participant : participants) {
        	Member member = new Member();
        	
        	member.setMatch(match); //양방향 매핑 되어있어서 match 객체에 member객체 매핑할 필요 없음
        	
        	member.setCk(new MemberCompKey(matchId,i++));
        	member.setSummonerid((String)participant.get("summonerId"));
        	member.setName((String)participant.get("summonerName"));
        	member.setChampionid((String)participant.get("championName"));
        	member.setPositions((String)participant.get("teamPosition"));
        	member.setWins((boolean)participant.get("win"));
        	
        	member.setTeam((int)participant.get("teamId"));
        	member.setChampLevel((int)participant.get("champLevel"));
        	member.setCs((int)participant.get("totalMinionsKilled")+(int)participant.get("neutralMinionsKilled"));
        	member.setGold((int)participant.get("goldEarned"));
        	member.setBountylevel((int)participant.get("bountyLevel")); //현상금 레벨
        	
        	member.setKills((int)participant.get("kills"));
        	member.setDeaths((int)participant.get("deaths"));
        	member.setAssists((int)participant.get("assists"));
        	
        	member.setVisionWardsBoughtInGame((int)participant.get("visionWardsBoughtInGame"));
        	member.setVisionscore((int)participant.get("visionScore"));
        	member.setWardpalced((int)participant.get("wardsPlaced"));
        	member.setWardkill((int)participant.get("wardsKilled"));
        	member.setDetectorwardplaced((int)participant.get("detectorWardsPlaced"));
        	
        	member.setBaronkills((int)participant.get("baronKills"));
        	member.setDragonkills((int)participant.get("dragonKills"));
        	member.setInhibitorkills((int)participant.get("inhibitorKills"));
        	member.setNexuskills((int)participant.get("nexusKills"));
        	
        	member.setDoublekills((int)participant.get("doubleKills"));
        	member.setTriplekills((int)participant.get("tripleKills"));
        	member.setQuadrakills((int)participant.get("quadraKills"));
        	member.setPentakills((int)participant.get("pentaKills"));
        	
        	member.setItem0((int)participant.get("item0"));
        	member.setItem1((int)participant.get("item1"));
        	member.setItem2((int)participant.get("item2"));
        	member.setItem3((int)participant.get("item3"));
        	member.setItem4((int)participant.get("item4"));
        	member.setItem5((int)participant.get("item5"));
        	member.setItem6((int)participant.get("item6"));	
        	
        }
		
		return match;
	}
	
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
			@SuppressWarnings("rawtypes")
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
				
				for(int i=start_index; i<matchIds.size();) {
					String matchId = matchIds.get(i);
					
					try {
						Map json = webclient.get().uri("https://asia.api.riotgames.com"
								+ "/lol/match/v5/matches/"+matchId+"?api_key="+key)
								.retrieve()
								.bodyToMono(Map.class)
								.block();
						
						Match match = parsingMatchJson(json);
						matches.add(match);
						i++;
					}catch(WebClientResponseException e1) {
						if(e1.getStatusCode().value()==429) {
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
					}catch(Exception e2) {
						break;
					}
				}
				
				threadService.saveMatches(matches);
			}
		};
		
		return runnable;
	}

}
