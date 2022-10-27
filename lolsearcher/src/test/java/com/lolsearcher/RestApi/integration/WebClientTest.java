package com.lolsearcher.RestApi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.domain.entity.summoner.Summoner;

@ActiveProfiles("test")
@SpringBootTest
public class WebClientTest {

	private static final String key = "RGAPI-2a0ac3ef-7f65-4854-97d4-54e2c7b3dbab";
	
	@Autowired
	WebClient webClient;
	
	@Autowired
	RiotRestAPI riotRestApi;
	
	//----------------------getSummonerById() 메소드 Test Case------------------------------------
	
	@Test
	void getSummonerByIdCase1() {
		//test Case 1 : REST API 요청이 올바른 경우
		
		//given
		String summoner_id = "vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw";
		
		//when
		Summoner summoner = riotRestApi.getSummonerById(summoner_id);
		
		//then
		assertThat(summoner.getId()).isEqualTo(summoner_id);
		assertThat(summoner.getLastmatchid()).isEqualTo("");
		assertThat(summoner.getLastInGameSearchTimeStamp()).isEqualTo(0);
	}
	
	@Test
	void getSummonerByIdCase2() {
		//test Case 2 : REST API 요청이 올바르지 못할 경우(존재하지 않는 ID인 경우)
		
		//given
		String summoner_id = "wrong_id_format";
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, 
				() -> riotRestApi.getSummonerById(summoner_id));
		
		assertThat(e.getStatusCode().value()).isEqualTo(400);
	}
	
	//----------------------getSummonerByName() 메소드 Test Case------------------------------------
	
	@Test
	void getSummonerByNameCase1() {
		//test Case 1 : REST API 요청이 올바른 경우
		
		//given
		String summoner_name = "푸켓푸켓";
		
		//when
		Summoner summoner = riotRestApi.getSummonerByName(summoner_name);
		
		//then
		assertThat(summoner.getName()).isEqualTo(summoner_name);
		assertThat(summoner.getLastmatchid()).isEqualTo("");
		assertThat(summoner.getLastInGameSearchTimeStamp()).isEqualTo(0);
	}
	
	@Test
	void getSummonerByNameCase2() {
		//test Case 2 : REST API 요청이 올바르지 못할 경우(존재하지 않는 닉네임인 경우)
		
		//given
		String summoner_name = "이런닉네임없겠지";
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, 
				() -> riotRestApi.getSummonerByName(summoner_name));
		
		assertThat(e.getStatusCode().value()).isEqualTo(404);
	}
	
	//----------------------getAllMatchIds() 메소드 Test Case------------------------------------
	
	@Test
	void getAllMatchIdsCase1() {
		//test Case 1 : REST API 요청이 올바른 경우 가장 최근 저장된 매치 ID가 전체 매치로부터 51번째인 경우
		
		//given
		String puuid = "ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA";
		
		String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?";
		int starts = 0;
		int counts = 100;
		String[] matchids = webClient.get()
				.uri(uri + "start="+starts+"&count="+counts+"&api_key="+key)
				.retrieve()
				.bodyToMono(String[].class)
				.block();
		
		String lastMatchId = matchids[50];
		
		//when
		List<String> recent_matchIds = riotRestApi.getAllMatchIds(puuid, lastMatchId);
		
		//then
		assertThat(recent_matchIds.size()).isEqualTo(50);
	}
	
	@Test
	void getAllMatchIdsCase2() {
		//test Case 2 : REST API 요청이 올바른 경우 가장 최근 저장된 매치 ID가 전체 매치로부터 151번째인 경우
		
		//given
		String puuid = "ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA";
		
		String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?";
		int starts = 100;
		int counts = 100;
		String[] matchids = webClient.get()
				.uri(uri + "start="+starts+"&count="+counts+"&api_key="+key)
				.retrieve()
				.bodyToMono(String[].class)
				.block();
		
		String lastMatchId = matchids[50];
		
		//when
		List<String> recent_matchIds = riotRestApi.getAllMatchIds(puuid, lastMatchId);
		
		//then
		assertThat(recent_matchIds.size()).isEqualTo(150);
	}
	
	@Test
	void getAllMatchIdsCase3() {
		//test Case 3 : REST API 요청이 올바르지 못한 경우(존재하지 않는 puuid인 경우)
		
		//given
		String puuid = "wrong_id_format";
		String lastMatchId = "";
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, 
				() -> riotRestApi.getAllMatchIds(puuid, lastMatchId));
		
		assertThat(e.getStatusCode().value()).isEqualTo(400);
	}
	
	//----------------------getmatch() 메소드 Test Case------------------------------------
	
	void getmatchCase1() {
		//test Case 1 : 
	}
	
	/*
	@Test
	void testGetRank() {
		
		List<RankDto> ranks = riotRestApi.getLeague("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		for(RankDto r : ranks) {
			System.out.println(r.getTier());
			System.out.println(r.getRank());
			System.out.println(r.getWins());
			System.out.println(r.getLosses());
			System.out.println(r.getSummonerId());
			System.out.println(r.getQueueType());
		}
	}*/
}
