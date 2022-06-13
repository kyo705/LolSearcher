package com.lolsearcher.restapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

import com.lolsearcher.domain.Dto.ingame.InGameDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;
import com.lolsearcher.domain.entity.summoner.match.MemberCompKey;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;

//riot �������� �����ؼ� ������ �����ͼ� ��ü�� ��� ����
public class RiotRestApiv1{
	

	public Summoner getSummoner(String summonername) {
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		Summoner summoner = new Summoner();
		
		try{
			String url1 = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+summonername+"?";   
			APIJion<Map> apijion = new APIJion<>(url1);
			Map summonerjson = apijion.Apijion();
      	
	        summoner.setId((String)summonerjson.get("id"));
	        summoner.setPuuid((String)summonerjson.get("puuid"));
	        summoner.setName((String)summonerjson.get("name"));
	        summoner.setAccountId((String)summonerjson.get("accountId"));
	        summoner.setProfileIconId((int)summonerjson.get("profileIconId"));
	        summoner.setRevisionDate((long)summonerjson.get("revisionDate"));
	        summoner.setSummonerLevel((int)summonerjson.get("summonerLevel"));
	        summoner.setLastmatchid("");
		}
		catch(HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", ((RestClientResponseException) e).getRawStatusCode());
          result.put("body"  , ((RestClientResponseException) e).getStatusText());
		}catch(Exception e) {
			result.put("statusCode", "999");
          result.put("body"  , "excpetion����");
		}
      
		
		return summoner;
	}
	
	public List<String> listofmatch(String puuid, int queue, String type, int start, int count,String lastmatchid){
		//if()
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<String> list = new ArrayList<>();
		
		try {
			boolean plag = true;
			int starts = 0;
			int counts = 100;
			
			while(plag) {
				
				String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?"
						+"start="+starts+"&count="+counts+"&";
		        APIJion<List<String>> apijion = new APIJion<>(url);
		        List<String> matchlists = apijion.Apijion();
		        if(matchlists.size()==0) {
		        	break;
		        }
		        
	        	Iterator<String> iter = matchlists.iterator();
	        	
	        	while(iter.hasNext()) {
	        		String matchid = iter.next();
	        		
	        		if(matchid.equals(lastmatchid)) {
	        			plag = false;
	        			break;
	        		}
	        		
	        		list.add(matchid);
	        	}
	        	
	        	starts += counts;
			}
			
		}catch(HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", ((RestClientResponseException) e).getRawStatusCode());
          result.put("body"  , ((RestClientResponseException) e).getStatusText());
		}catch(Exception e) {
			result.put("statusCode", "999");
          result.put("body"  , "excpetion����");
		}
		
		return list;
	}
	
	public Match getmatch(String matchid) {
		
		Match match = new Match();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		try{
			String url1 = "https://asia.api.riotgames.com/lol/match/v5/matches/"+matchid+"?";
	        APIJion<Map> apijion = new APIJion<>(url1);
	        Map jsondata = apijion.Apijion();
	        Map info = (Map)jsondata.get("info");
	        Map metadata = (Map)jsondata.get("metadata");
	        
	        //match ���� ����
	        match.setGameDuration((int)info.get("gameDuration"));
	        match.setMatchId((String)metadata.get("matchId"));
	        match.setGameEndTimestamp((long)info.get("gameEndTimestamp"));
	        match.setQueueId((int)info.get("queueId"));
	        
	        String version = (String)info.get("gameVersion");
	        String s = version.substring(0,2);
	        int season = Integer.parseInt(s);
	        match.setSeason(season);
	        
      	//Member ��ü �ޱ�
      	List<Map> participants = (ArrayList)info.get("participants");
      	int i=0;
	        for(Map participant : participants) {
	        	Member member = new Member();
	        	
	        	member.setMatch(match); //����� ���� �Ǿ��־ match ��ü�� member��ü ������ �ʿ� ����
	        	
	        	member.setCk(new MemberCompKey((String)metadata.get("matchId"),i++));
	        	member.setSummonerid((String)participant.get("summonerId"));
	        	member.setName((String)participant.get("summonerName"));
	        	member.setChampionid((String)participant.get("championName"));
	        	member.setPositions((String)participant.get("teamPosition"));
	        	member.setWins((boolean)participant.get("win"));
	        	
	        	member.setTeam((int)participant.get("teamId"));
	        	member.setChampLevel((int)participant.get("champLevel"));
	        	member.setCs((int)participant.get("totalMinionsKilled")+(int)participant.get("neutralMinionsKilled"));
	        	member.setGold((int)participant.get("goldEarned"));
	        	member.setBountylevel((int)participant.get("bountyLevel")); //����� ����
	        	
	        	member.setKills((int)participant.get("kills"));
	        	member.setDeaths((int)participant.get("deaths"));
	        	member.setAssists((int)participant.get("assists"));
	        	
	        	member.setVisionWardsBoughtInGame((int)participant.get("visionWardsBoughtInGame"));
	        	member.setVisionscore((int)participant.get("visionScore")); //�þ�����
	        	member.setWardpalced((int)participant.get("wardsPlaced"));
	        	member.setWardkill((int)participant.get("wardsKilled"));
	        	member.setDetectorwardplaced((int)participant.get("detectorWardsPlaced")); //����͵� ��ġ Ƚ��
	        	
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
	        
		}catch(HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", ((RestClientResponseException) e).getRawStatusCode());
          result.put("body"  , ((RestClientResponseException) e).getStatusText());
          try {
				Thread.sleep(130000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
          match = this.getmatch(matchid);
          
		}catch(Exception e) {
			result.put("statusCode", "999");
          result.put("body"  , "excpetion����");
		}
		
		return match;
		
	}
	
	//�ϼ�
	public List<Rank> getLeague(String summonerid){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<Rank> leagueSet = new ArrayList<>();
		
		try{
			String url = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/"+summonerid+"?";      
	        APIJion<List> apijion = new APIJion<>(url);
	        List<Map> setjson = apijion.Apijion();

	        Iterator<Map> iter = setjson.iterator();
	        while(iter.hasNext()) {
	        	Map mapjson = iter.next();
	        	
	        	Rank rank = new Rank();
	        	
	        	//rank.setCk(new RankCompKey((String)mapjson.get("summonerId"), (String)mapjson.get("queueType")));
	        	rank.setLeagueId((String)mapjson.get("leagueId"));
	        	rank.setTier((String)mapjson.get("tier"));
	        	rank.setRank((String)mapjson.get("rank"));
	        	rank.setLeaguePoints((int)mapjson.get("leaguePoints"));
	        	rank.setWins((int)mapjson.get("wins"));
	        	rank.setLosses((int)mapjson.get("losses"));
	        	
	        	leagueSet.add(rank);
	        }
		}catch(HttpClientErrorException | HttpServerErrorException e) {
			result.put("statusCode", ((RestClientResponseException) e).getRawStatusCode());
          result.put("body"  , ((RestClientResponseException) e).getStatusText());
		}catch(Exception e) {
			result.put("statusCode", "999");
          result.put("body"  , "excpetion����");
		}
		
		return leagueSet;
	}
	
	public InGameDto getInGameBySummonerId(String summonerid) {
		// TODO Auto-generated method stub
		return null;
	}
}

