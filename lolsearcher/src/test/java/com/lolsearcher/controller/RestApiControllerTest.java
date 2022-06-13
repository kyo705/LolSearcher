package com.lolsearcher.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.service.RestApiService;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class RestApiControllerTest {
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private RestApiService restApiService;
	
	@Test
	void getOneSummonerById() throws Exception {
		
		final SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("pYDMufKXwDIdMEQlvwlSzukdrtrFo_qmQPGBRMF1A4NhlCQ");
		summoner.setPuuid("ELxmjBy4_fjTu676kny5dJaAZDzFqsE6eDoYNH4Qki9esM8nZxupTRiUFlwj8bOaKGTDirtZkoZkiA");
		summoner.setName("JUGKlNG");
		summoner.setSummonerLevel(363L);
		summoner.setProfileIconId(3554);
		summoner.setLastRenewTimeStamp(1653323114302L);
		
	    when(restApiService.getSummonerById(summoner.getSummonerid()))
	    .thenReturn(summoner);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/id/{id}", summoner.getSummonerid()) // 4
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/summoner-by-id",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					pathParameters(parameterWithName("id").description("Summoner Id")),
	        					responseFields(
	        							fieldWithPath("summonerid").type(JsonFieldType.STRING).description("고유 계정 ID"),
	        							fieldWithPath("puuid").type(JsonFieldType.STRING).description("고유 계정 ID"),
	        							fieldWithPath("name").type(JsonFieldType.STRING).description("계정 닉네임"),
	        							fieldWithPath("profileIconId").type(int.class).description("프로필 아이콘 ID"),
	        							fieldWithPath("summonerLevel").type(long.class).description("계정 레벨"),
	        							fieldWithPath("lastRenewTimeStamp").type(long.class).description("마지막 갱신된 시간(타임스탬프 값)")
	        							
	        							)
	        					)
	        			);
	        	
	}
	
	@Test
	void getOneSummonerByName() throws Exception {
		
		final SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("pYDMufKXwDIdMEQlvwlSzukdrtrFo_qmQPGBRMF1A4NhlCQ");
		summoner.setPuuid("ELxmjBy4_fjTu676kny5dJaAZDzFqsE6eDoYNH4Qki9esM8nZxupTRiUFlwj8bOaKGTDirtZkoZkiA");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(363L);
		summoner.setProfileIconId(3554);
		summoner.setLastRenewTimeStamp(1653323114302L);
		
	    when(restApiService.getSummonerByName(summoner.getName()))
	    .thenReturn(summoner);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/name/{name}", summoner.getName())
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/summoner-by-name",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					pathParameters(parameterWithName("name").description("계정 닉네임")),
	        					responseFields(
	        							fieldWithPath("summonerid").type(JsonFieldType.STRING).description("고유 계정 ID"),
	        							fieldWithPath("puuid").type(JsonFieldType.STRING).description("고유 계정 ID"),
	        							fieldWithPath("name").type(JsonFieldType.STRING).description("계정 닉네임"),
	        							fieldWithPath("profileIconId").type(int.class).description("프로필 아이콘 ID"),
	        							fieldWithPath("summonerLevel").type(long.class).description("계정 레벨"),
	        							fieldWithPath("lastRenewTimeStamp").type(long.class).description("마지막 갱신된 시간(타임스탬프 값)")
	        							
	        							)
	        					)
	        			);
	        	
	}
	
	
	@Test
	void getSoloRankById() throws Exception {
		
		RankDto rank = new RankDto();
		rank.setSummonerId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		rank.setSeasonId(22);
		rank.setQueueType("RANKED_SOLO_5x5");
		rank.setLeagueId("94f7439f-b4d0-4a39-aa44-0a003d0adf3e");
		rank.setTier("GOLD");
		rank.setRank("IV");
		rank.setLeaguePoints(60);
		rank.setWins(222);
		rank.setLosses(200);		
		
	    when(restApiService.getRankById(rank.getSummonerId(),"RANKED_SOLO_5x5",22))
	    .thenReturn(rank);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/id/{id}/rank/solo/season/22", rank.getSummonerId())
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/solorank",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					pathParameters(parameterWithName("id").description("계정 ID")),
	        					responseFields(
	        							fieldWithPath("summonerId").type(JsonFieldType.STRING).description("계정 ID"),
	        							fieldWithPath("seasonId").type(int.class).description("시즌 정보(년도별)"),
	        							fieldWithPath("queueType").type(JsonFieldType.STRING).description("랭크 게임 종류"),
	        							fieldWithPath("leagueId").type(JsonFieldType.STRING).description("소속된 리그 ID"),
	        							fieldWithPath("tier").type(JsonFieldType.STRING).description("랭크 티어"),
	        							fieldWithPath("rank").type(JsonFieldType.STRING).description("랭크 레벨"),
	        							fieldWithPath("leaguePoints").type(int.class).description("랭크 점수"),
	        							fieldWithPath("wins").type(int.class).description("승리 횟수"),
	        							fieldWithPath("losses").type(int.class).description("패배 횟수")
	        							
	        							)
	        					)
	        			);
	        	
	}
	
	@Test
	void getFlexRankById() throws Exception {
		
		RankDto rank = new RankDto();
		rank.setSummonerId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		rank.setSeasonId(22);
		rank.setQueueType("RANKED_FLEX_SR");
		rank.setLeagueId("f6310a65-f7a5-4f40-a876-ece33f269f17");
		rank.setTier("DIAMOND");
		rank.setRank("IV");
		rank.setLeaguePoints(30);
		rank.setWins(55);
		rank.setLosses(30);		
		
	    when(restApiService.getRankById(rank.getSummonerId(),"RANKED_FLEX_SR",22))
	    .thenReturn(rank);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/id/{id}/rank/flex/season/22", rank.getSummonerId())
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/teamrank",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					pathParameters(parameterWithName("id").description("계정 ID")),
	        					responseFields(
	        							fieldWithPath("summonerId").type(JsonFieldType.STRING).description("계정 ID"),
	        							fieldWithPath("seasonId").type(int.class).description("시즌 정보(년도별)"),
	        							fieldWithPath("queueType").type(JsonFieldType.STRING).description("랭크 게임 종류"),
	        							fieldWithPath("leagueId").type(JsonFieldType.STRING).description("소속된 리그 ID"),
	        							fieldWithPath("tier").type(JsonFieldType.STRING).description("랭크 티어"),
	        							fieldWithPath("rank").type(JsonFieldType.STRING).description("랭크 레벨"),
	        							fieldWithPath("leaguePoints").type(int.class).description("랭크 점수"),
	        							fieldWithPath("wins").type(int.class).description("승리 횟수"),
	        							fieldWithPath("losses").type(int.class).description("패배 횟수")
	        							
	        							)
	        					)
	        			);
	        	
	}
	
	@Test
	void getRanksById() throws Exception {
		
		RankDto solorank = new RankDto();
		solorank.setSummonerId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		solorank.setSeasonId(22);
		solorank.setQueueType("RANKED_SOLO_5x5");
		solorank.setLeagueId("94f7439f-b4d0-4a39-aa44-0a003d0adf3e");
		solorank.setTier("GOLD");
		solorank.setRank("IV");
		solorank.setLeaguePoints(60);
		solorank.setWins(222);
		solorank.setLosses(200);
		
		RankDto teamrank = new RankDto();
		teamrank.setSummonerId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		teamrank.setSeasonId(22);
		teamrank.setQueueType("RANKED_FLEX_SR");
		teamrank.setLeagueId("f6310a65-f7a5-4f40-a876-ece33f269f17");
		teamrank.setTier("DIAMOND");
		teamrank.setRank("IV");
		teamrank.setLeaguePoints(30);
		teamrank.setWins(55);
		teamrank.setLosses(30);	
		
		List<RankDto> ranks = new ArrayList<>();
		ranks.add(solorank);
		ranks.add(teamrank);
		
	    when(restApiService.getRanksById("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw", 22))
	    .thenReturn(ranks);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/id/{id}/ranks/season/22", teamrank.getSummonerId())
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/ranks",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					pathParameters(parameterWithName("id").description("계정 ID")),
	        					responseFields(
	        							fieldWithPath("[].summonerId").type(JsonFieldType.STRING).description("계정 ID"),
	        							fieldWithPath("[].seasonId").type(int.class).description("시즌 정보(년도별)"),
	        							fieldWithPath("[].queueType").type(JsonFieldType.STRING).description("랭크 게임 종류"),
	        							fieldWithPath("[].leagueId").type(JsonFieldType.STRING).description("소속된 리그 ID"),
	        							fieldWithPath("[].tier").type(JsonFieldType.STRING).description("랭크 티어"),
	        							fieldWithPath("[].rank").type(JsonFieldType.STRING).description("랭크 레벨"),
	        							fieldWithPath("[].leaguePoints").type(int.class).description("랭크 점수"),
	        							fieldWithPath("[].wins").type(int.class).description("승리 횟수"),
	        							fieldWithPath("[].losses").type(int.class).description("패배 횟수")
	        							
	        							)
	        					)
	        			);
	        	
	}
	
	@Test
	void getMatchIdsBySummonerId() throws Exception {
		
		final SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("pYDMufKXwDIdMEQlvwlSzukdrtrFo_qmQPGBRMF1A4NhlCQ");
		summoner.setPuuid("ELxmjBy4_fjTu676kny5dJaAZDzFqsE6eDoYNH4Qki9esM8nZxupTRiUFlwj8bOaKGTDirtZkoZkiA");
		summoner.setName("JUGKlNG");
		summoner.setSummonerLevel(363L);
		summoner.setProfileIconId(3554);
		summoner.setLastRenewTimeStamp(1653323114302L);	
		
		List<String> matchIds = new ArrayList<>();
		for(int i=0;i<100;i++) {
			matchIds.add("match"+i);
		}
		
	    when(restApiService.getMatchIds(summoner.getSummonerid(), 0, 100))
	    .thenReturn(matchIds);
	    
	    this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/summoner/id/{id}/matcheIds?start=0&count=100", summoner.getSummonerid())
	            .accept(MediaType.APPLICATION_JSON))
	        	.andExpect(status().isOk())
	        	.andDo(
	        			document(
	        					"/doc/matchIds",
	        					preprocessRequest(prettyPrint()),
	        					preprocessResponse(prettyPrint()),
	        					requestParameters(
	        							parameterWithName("start").description("찾을 matchId 처음 위치"),
	        							parameterWithName("count").description("찾을 matchId 갯수")
	        							),
	        					pathParameters(
	        							parameterWithName("id").description("계정 ID")
	        							),
	        					responseFields(
	        							fieldWithPath("[]").description("매치 ID 리스트").optional()
	        							)
	        					)
	        			);
	        	
	}
}
