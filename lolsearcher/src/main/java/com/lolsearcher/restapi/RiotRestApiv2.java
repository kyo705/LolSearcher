package com.lolsearcher.restapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.domain.entity.Match;
import com.lolsearcher.domain.entity.Member;
import com.lolsearcher.domain.entity.MemberCompKey;
import com.lolsearcher.domain.entity.Rank;
import com.lolsearcher.domain.entity.RankCompKey;
import com.lolsearcher.domain.entity.Summoner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RiotRestApiv2 implements RiotRestAPI{

	private WebClient webclient;
	private static final String key = "RGAPI-2a0ac3ef-7f65-4854-97d4-54e2c7b3dbab";
	
	//생성자에서 webclient의 baseUrl을 설정하지 않은 이유는 메소드마다 baseUrl이 다르기 때문
	public RiotRestApiv2(WebClient webclient) {
		this.webclient = webclient;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Summoner getSummoner(String summonername) {
		
		Summoner summoner = new Summoner();
		
		Map json = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/summoner/v4/summoners/by-name/"+summonername+"?api_key="+key)
				.retrieve().bodyToMono(Map.class).block();
		
		summoner.setId((String) json.get("id"));
        summoner.setPuuid((String) json.get("puuid"));
        summoner.setName((String) json.get("name"));
        summoner.setAccountId((String) json.get("accountId"));
        summoner.setProfileIconId((int) json.get("profileIconId"));
        summoner.setRevisionDate((long) json.get("revisionDate"));
        summoner.setSummonerLevel((int) json.get("summonerLevel"));
        summoner.setLastmatchid("");
		
		return summoner;
	}
	
	@Override
	public List<String> listofmatch(String puuid, int queue, String type, int start, int count, String lastmatchid) {
		
		List<String> matchidlist = new ArrayList<>();
		
		boolean plag = true;
		
		int starts = 0;
		int counts = 100;
		long lastmatchidlong;
		if(!lastmatchid.equals("")) {
			lastmatchidlong = Long.parseLong(lastmatchid.substring(3));
		}else {
			lastmatchidlong = 0;
		}
		System.out.println();
		
		while(plag) {
			String[] matchids = webclient.get().uri("https://asia.api.riotgames.com"
					+ "/lol/match/v5/matches/by-puuid/"+puuid+"/ids?"
					+"start="+starts+"&count="+counts+"&api_key="+key)
					.retrieve().bodyToMono(String[].class).block();
			
			for(String matchid : matchids) {
				System.out.println(matchid);
				if(lastmatchidlong==Long.parseLong(matchid.substring(3))) {
					plag = false;
					break;
				}else {
					matchidlist.add(matchid);
				}
			}
			if(matchids.length==0) {
				plag = false;
			}
			starts += counts;
		}
			
		
		return matchidlist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Match getmatch(String matchid) {
		
		Match match = new Match();
		
		Map json = webclient.get().uri("https://asia.api.riotgames.com"
				+ "/lol/match/v5/matches/"+matchid+"?api_key="+key)
				.retrieve().bodyToMono(Map.class).block();
		
			
		Map info = (Map) json.get("info");
		Map metadata = (Map)json.get("metadata");
			
		match.setGameDuration((int) info.get("gameDuration"));
        match.setMatchId((String)metadata.get("matchId"));
        match.setGameEndTimestamp((long) info.get("gameEndTimestamp"));
        match.setQueueId((int) info.get("queueId"));
        
        String version = (String)info.get("gameVersion");
        String s = version.substring(0,2);
        int season = Integer.parseInt(s);
        match.setSeason(season);
        
        List<Map> participants = (ArrayList)info.get("participants");
        for(Map participant : participants) {
        	Member member = new Member();
        	
        	member.setMatch(match); //양방향 매핑 되어있어서 match 객체에 member객체 매핑할 필요 없음
        	
        	member.setCk(new MemberCompKey((String)participant.get("summonerId"),(String)metadata.get("matchId")));
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
        	member.setVisionscore((int)participant.get("visionScore")); //시야점수
        	member.setWardpalced((int)participant.get("wardsPlaced"));
        	member.setWardkill((int)participant.get("wardsKilled"));
        	member.setDetectorwardplaced((int)participant.get("detectorWardsPlaced")); //제어와드 설치 횟수
        	
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

	@SuppressWarnings("rawtypes")
	@Override
	public Set<Rank> getLeague(String summonerid) {
		
		Set<Rank> ranklist = new HashSet<>();
		
		List<Map> ranks = webclient.get().uri("https://kr.api.riotgames.com"
				+ "/lol/league/v4/entries/by-summoner/"+summonerid+"?api_key="+key)
		.retrieve().bodyToFlux(Map.class).collectList().block();
		
		Iterator<Map> iter = ranks.iterator();
		while(iter.hasNext()) {
			Map rankmap = iter.next();
			Rank rank = new Rank();
			
			rank.setCk(new RankCompKey((String)rankmap.get("summonerId"), (String)rankmap.get("queueType")));
			rank.setLeagueId((String) rankmap.get("leagueId"));
			rank.setLeaguePoints((int) rankmap.get("leaguePoints"));
			rank.setWins((int) rankmap.get("wins"));
			rank.setLosses((int) rankmap.get("losses"));
			rank.setRank((String) rankmap.get("rank"));
			rank.setTier((String) rankmap.get("tier"));
			
			ranklist.add(rank);
		}

		
		return ranklist;
	}

}
