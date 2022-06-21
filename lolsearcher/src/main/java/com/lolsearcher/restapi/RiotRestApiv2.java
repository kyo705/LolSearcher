package com.lolsearcher.restapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;
import com.lolsearcher.domain.entity.summoner.match.MemberCompKey;

public class RiotRestApiv2 implements RiotRestAPI{

	private WebClient webclient;
	private static final String key = "RGAPI-2a0ac3ef-7f65-4854-97d4-54e2c7b3dbab";
	
	public RiotRestApiv2(WebClient webclient) {
		this.webclient = webclient;
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
		long lastmatchidlong;
		String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?";
		int starts = 0;
		int counts = 100;
		
		if(!lastMatchId.equals("")) {
			lastmatchidlong = Long.parseLong(lastMatchId.substring(3));
		}else {
			lastmatchidlong = 0;
		}
		
		while(plag) {
			String[] matchids = webclient.get()
					.uri(uri + "start="+starts+"&count="+counts+"&api_key="+key)
					.retrieve()
					.bodyToMono(String[].class)
					.block();
			
			if(matchids.length!=100) {
				plag = false;
				break;
			}
			
			for(String matchid : matchids) {
				if(lastmatchidlong==Long.parseLong(matchid.substring(3))) {
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
		
		long lastmatchidlong;
		String uri;
		int starts = start;
		int counts = count;
		
		if(!lastmatchid.equals("")) {
			lastmatchidlong = Long.parseLong(lastmatchid.substring(3));
		}else {
			lastmatchidlong = 0;
		}
		
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
				if(lastmatchidlong==Long.parseLong(matchid.substring(3))) {
					break;
				}else {
					matchidlist.add(matchid);
				}
			}
			
			starts += counts;
		}
			
		
		return matchidlist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Match getmatch(String matchid) throws WebClientResponseException{
		
		Match match = new Match();
		
		Map json = webclient.get().uri("https://asia.api.riotgames.com"
				+ "/lol/match/v5/matches/"+matchid+"?api_key="+key)
				.retrieve().bodyToMono(Map.class).block();
		
			
		Map info = (Map) json.get("info");
		
		match.setGameDuration((int) info.get("gameDuration"));
        match.setMatchId(matchid);
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
        	
        	member.setCk(new MemberCompKey(matchid,i++));
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

}
