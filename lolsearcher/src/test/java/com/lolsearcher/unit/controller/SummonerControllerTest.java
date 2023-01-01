package com.lolsearcher.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.lolsearcher.exception.summoner.MoreSummonerException;
import com.lolsearcher.exception.summoner.NoSummonerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.controller.SummonerController;
import com.lolsearcher.model.dto.parameter.SummonerUrlParam;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.exception.handler.SummonerExceptionHandler;
import com.lolsearcher.service.ban.SearchIpBanService;
import com.lolsearcher.service.match.MatchService;
import com.lolsearcher.service.mostchamp.MostChampService;
import com.lolsearcher.service.rank.RankService;
import com.lolsearcher.service.summoner.SummonerService;

@ActiveProfiles("test")
@SpringBootTest(classes = {SummonerController.class, SummonerExceptionHandler.class})
@AutoConfigureMockMvc
public class SummonerControllerTest {
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	
	@MockBean private SummonerService summonerService;
	@MockBean private RankService rankService;
	@MockBean private MatchService matchService;
	@MockBean private MostChampService mostChampService;
	@MockBean private SearchIpBanService searchIpBanService;
	
	@BeforeEach
	public void beforeEach() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	@DisplayName("@ModelAttribute 객체가 view로 잘 전달되는지 확인.")
	@Test
	public void getSummonerWithModelAttribute() throws Exception {
		//given
		String name = "푸켓푸켓";
		String view ="/summoner_data";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.build();
		
		SummonerUrlParam params = new SummonerUrlParam();
		params.setName(name);
		params.setSummonerId(summoner.getSummonerId());
		
		given(summonerService.findOldSummoner(name)).willReturn(summoner);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).requestAttr("name", name))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(view().name(view))
		.andExpect(result -> {
			SummonerUrlParam modelAttribute = (SummonerUrlParam) result.getModelAndView().getModel().get("params");
			assertThat(modelAttribute.getName()).isEqualTo(params.getName());
			assertThat(modelAttribute.getChampion()).isEqualTo(params.getChampion());
			assertThat(modelAttribute.getSummonerId()).isEqualTo(params.getSummonerId());
			assertThat(modelAttribute.getSeason()).isEqualTo(params.getSeason());
		});
	}
	
	@DisplayName("존재하지 않는 소환사 이름을 검색할 경우 에러 페이지를 리턴한다.")
	@Test
	public void getNotExistSummoner() throws Exception {
		//given
		String name = "존재하지 않는 소환사";
		String noNameView ="/error/name";
		
		given(summonerService.findOldSummoner(name)).willThrow(NoSummonerException.class);
		given(summonerService.findRecentSummoner(name)).willThrow(new WebClientResponseException(
				HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(),null, null, null));
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).requestAttr("name", name))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(view().name(noNameView))
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof WebClientResponseException));
	}
	
	@DisplayName("신규 소환사 데이터를 둘 이상의 클라이언트에서 요청한 경우 가장 빠른 요청을 DB에 저장하고 나머지 요청은 에러 페이지를 리턴한다.")
	@Test
	public void saveSummonerDuplicated() throws Exception {
		//given
		String name = "신규 소환사";
		String view ="/error/overlap";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.build();
		
		given(summonerService.findOldSummoner(name)).willThrow(NoSummonerException.class).willReturn(summoner);
		given(summonerService.findRecentSummoner(name)).willThrow(DataIntegrityViolationException.class);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).requestAttr("name", name))
		.andExpect(status().isOk())
		.andExpect(view().name(view));
		
		verify(summonerService, times(1)).findOldSummoner(name);
	}
	
	@DisplayName("소환사 이름이 DB에 없을 경우 게임서버에 API 요청으로 가져와 View로 전달한다.")
	@Test
	public void getNotExistInDBSummoner() throws Exception {
		//given
		String name = "푸켓푸켓";
		String view ="/summoner_data";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.build();
		
		given(summonerService.findOldSummoner(name)).willThrow(NoSummonerException.class);
		given(summonerService.findRecentSummoner(name)).willReturn(summoner);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).requestAttr("name", name))
		.andExpect(status().is(HttpStatus.OK.value()))
		.andExpect(view().name(view))
		.andExpect(result -> assertThat(result.getModelAndView().getModel().get("summoner")).isEqualTo(summoner));
		
		verify(summonerService, times(1)).findRecentSummoner(name);
		verify(rankService, times(1)).setLeague(summoner.getSummonerId());
	}
	
	@DisplayName("똑같은 소환사 이름을 가진 유저가 DB 2명 이상일 경우에 DB 업데이트 후 실제 유저 데이터를 View로 전달한다.")
	@Test
	public void getDuplicatedDBSummoner() throws Exception {
		//given
		String name = "푸켓푸켓";
		String view ="/summoner_data";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.build();
		
		given(summonerService.findOldSummoner(name)).willThrow(MoreSummonerException.class);
		given(summonerService.findRecentSummoner(name)).willReturn(summoner);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).requestAttr("name", name))
		.andExpect(status().isOk())
		.andExpect(view().name(view))
		.andExpect(result -> assertThat(result.getModelAndView().getModel().get("summoner")).isEqualTo(summoner));

		verify(summonerService, times(1)).findRecentSummoner(name);
		verify(rankService, times(0)).setLeague(summoner.getSummonerId());
	}
	
	@DisplayName("클라이언트가 갱신 요청을 했을 때 갱신 기간이 5분 이하일 경우 갱신을 하지 않는다.")
	@Test
	public void getSummonerWithImproperRenewRequest() throws Exception {
		//given
		String name = "푸켓푸켓";
		String view ="/summoner_data";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.lastRenewTimeStamp(System.currentTimeMillis())
						.build();
		
		given(summonerService.findOldSummoner(name)).willReturn(summoner);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).param("renew", "true").requestAttr("name", name))
		.andExpect(status().isOk())
		.andExpect(view().name(view))
		.andExpect(result -> assertThat(result.getModelAndView().getModel().get("summoner")).isEqualTo(summoner));
		
		verify(summonerService, times(0)).findRecentSummoner(name);
		verify(rankService, times(0)).setLeague(any());
		verify(matchService, times(0)).getApiMatches(any());
	}
	
	@DisplayName("클라이언트가 갱신 요청을 했을 때 갱신 기간이 5분 이상일 경우 갱신한다.")
	@Test
	public void getSummonerWithProperRenewRequest() throws Exception {
		//given
		String name = "푸켓푸켓";
		String view ="/summoner_data";
		SummonerDto summoner = SummonerDto.builder()
						.summonerId("summonerId")
						.name(name)
						.lastRenewTimeStamp(System.currentTimeMillis()-6*60*1000)
						.build();
		
		given(summonerService.findOldSummoner(name)).willReturn(summoner);
		given(summonerService.findRecentSummoner(name)).willReturn(summoner);
		//when && then
		mockMvc.perform(post("/summoner").param("name", name).param("renew", "true").requestAttr("name", name))
		.andExpect(status().isOk())
		.andExpect(view().name(view))
		.andExpect(result -> assertThat(result.getModelAndView().getModel().get("summoner")).isEqualTo(summoner));
		
		verify(summonerService, times(1)).findRecentSummoner(name);
		verify(rankService, times(1)).setLeague(summoner.getSummonerId());
	}
}
