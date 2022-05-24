package com.lolsearcher.RestApi;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.restapi.RiotRestAPI;

@SpringBootTest
public class WebClientTest {

	@Autowired
	RiotRestAPI riotRestApi;
	
	@Test
	void gsonParsing() {
		Summoner s = riotRestApi.getSummonerByName("ÇªÄÏÇªÄÏ");
		System.out.println(s.getId());
		System.out.println(s.getName());
		System.out.println(s.getRevisionDate());
		System.out.println(s.getLastmatchid());
		System.out.println(s.getLastRenewTimeStamp());
		System.out.println(System.currentTimeMillis());
	}
	
	@Test
	void testGetRank() {
		System.out.println("·©Å© ¿£Æ¼Æ¼ Å×½ºÆ® ½ÃÀÛ");
		List<RankDto> ranks = riotRestApi.getLeague("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		for(RankDto r : ranks) {
			System.out.println(r.getTier());
			System.out.println(r.getRank());
			System.out.println(r.getWins());
			System.out.println(r.getLosses());
			System.out.println(r.getSummonerId());
			System.out.println(r.getQueueType());
		}
	}
}
