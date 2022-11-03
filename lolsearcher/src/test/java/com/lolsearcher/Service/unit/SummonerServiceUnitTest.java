package com.lolsearcher.Service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.RecentMatchesDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;
import com.lolsearcher.exception.summoner.SameNameExistException;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.service.SummonerService;
import com.lolsearcher.service.ThreadService;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	static final int currentSeasonId = 22;
	
	SummonerService summonerService;
	@Mock 
	SummonerRepository summonerRepository;
	@Mock 
	RiotRestAPI riotRestApi;
	@Mock 
	ApplicationContext applicationContext;
	@Mock
	EntityManager em;
	@Mock
	ExecutorService executorService;
	@Mock
	ThreadService threadService;
	
	@BeforeEach
	void upset() {
		summonerService = new SummonerService(threadService, summonerRepository, riotRestApi);
	}

	//----------------------findDbSummoner() 메소드 Test Case------------------------------------
	
	@Test
	void findDbSummonerCase1() {
		//testCase1 : DB에 특정 닉네임이 1개 존재할 때
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		//Mock 객체 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id");
		summoner.setName(summonerName);
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerName);
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner.getId());
	}
	
	@Test
	void findDbSummonerCase2() {
		//testCase2 : DB에 특정 닉네임이 존재하지 않을 때
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(new ArrayList<>());
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto).isEqualTo(null);
		verify(riotRestApi, times(0)).getSummonerById(anyString());
	}
	
	@Test
	void findDbSummonerCase3() {
		//testCase3 : DB에 특정 닉네임이 2개 이상 존재하고 해당 닉네임의 진짜 소유주 유저가 DB에 존재할 때
		//닉네임은 게임 내에서 변경이 가능하므로 
		//DB 데이터 갱신이 안되면 닉네임 중복상황이 발생할 수 있음
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setPrimaryId(1);
		dbSummoner1.setId("id1");
		dbSummoner1.setName(summonerName);
		
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setPrimaryId(2);
		dbSummoner2.setId("id2");
		dbSummoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(dbSummoner1);
		dbSummoners.add(dbSummoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		//when
		Exception e = assertThrows(SameNameExistException.class, ()->{
			summonerService.findDbSummoner(summonerName);
		});
		assertThat(e.getClass()).isEqualTo(SameNameExistException.class);
	}
	
	
	//----------------------updateDbSummoner() 메소드 Test Case------------------------------------
	
	@Test
	public void updateDbSummonerCase1() {
		//test Case 1 : DB에서 특정 닉네임을 가진 유저들 모두 실제 존재하는 유저일 경우(닉네임은 특정 닉네임과 일치하지 않음)
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("id1");
		dbSummoner1.setName(summonerName);
		dbSummoner1.setSummonerLevel(333);
		
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("id2");
		dbSummoner2.setName(summonerName);
		dbSummoner2.setSummonerLevel(400);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(dbSummoner1);
		dbSummoners.add(dbSummoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner renew_summoner1 = new Summoner();
		renew_summoner1.setId("id1");
		renew_summoner1.setName("갓버수문장");
		renew_summoner1.setSummonerLevel(340);
		
		when(riotRestApi.getSummonerById(dbSummoner1.getId()))
		.thenReturn(renew_summoner1);
		
		Summoner renew_summoner2 = new Summoner();
		renew_summoner2.setId("id2");
		renew_summoner2.setName("페이커");
		renew_summoner2.setSummonerLevel(410);
		
		when(riotRestApi.getSummonerById(dbSummoner2.getId()))
		.thenReturn(renew_summoner2);
		
		//when
		summonerService.updateDbSummoner(summonerName);
		
		//then
		assertThat(dbSummoner1.getName()).isEqualTo("갓버수문장");
		assertThat(dbSummoner1.getSummonerLevel()).isEqualTo(340);
		
		assertThat(dbSummoner2.getName()).isEqualTo("페이커");
		assertThat(dbSummoner2.getSummonerLevel()).isEqualTo(410);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
	}
	
	
	@Test
	public void updateDbSummonerCase2() {
		//test Case 2 : DB에서 특정 닉네임을 가진 유저들 중 적어도 한 명 이상 실제론 존재하지 않는 유저일 경우
		//(WebClientResponseException 400 error 발생할 경우)
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("id1");
		dbSummoner1.setName(summonerName);
		dbSummoner1.setSummonerLevel(333);
		
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("id2");
		dbSummoner2.setName(summonerName);
		dbSummoner2.setSummonerLevel(400);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(dbSummoner1);
		dbSummoners.add(dbSummoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner renew_summoner1 = new Summoner();
		renew_summoner1.setId("id1");
		renew_summoner1.setName("갓버수문장");
		renew_summoner1.setSummonerLevel(340);
		
		when(riotRestApi.getSummonerById(dbSummoner1.getId()))
		.thenReturn(renew_summoner1);
		
		when(riotRestApi.getSummonerById(dbSummoner2.getId()))
		.thenThrow(new WebClientResponseException(400, "bad request", null, null, null));
		
		//then
		summonerService.updateDbSummoner(summonerName);
		
		//when
		assertThat(dbSummoner1.getName()).isEqualTo("갓버수문장");
		assertThat(dbSummoner1.getSummonerLevel()).isEqualTo(340);
		verify(summonerRepository, times(1)).deleteSummoner(dbSummoner2);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
	}
	
	@Test
	public void updateDbSummonerCase3() {
		//test Case 3 : REST 통신 중 최대 요청 횟수를 초과한 경우(429 ERROR 발생할 경우)
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("id1");
		dbSummoner1.setName(summonerName);
		dbSummoner1.setSummonerLevel(333);
		
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("id2");
		dbSummoner2.setName(summonerName);
		dbSummoner2.setSummonerLevel(400);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(dbSummoner1);
		dbSummoners.add(dbSummoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner renew_summoner1 = new Summoner();
		renew_summoner1.setId("id1");
		renew_summoner1.setName("갓버수문장");
		renew_summoner1.setSummonerLevel(340);
		
		when(riotRestApi.getSummonerById(dbSummoner1.getId()))
		.thenReturn(renew_summoner1);
		
		when(riotRestApi.getSummonerById(dbSummoner2.getId()))
		.thenThrow(new WebClientResponseException(429, "too many request", null, null, null));
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.updateDbSummoner(summonerName));
		
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		assertThat(e.getStatusText()).isEqualTo("too many request");
		
		assertThat(dbSummoner1.getName()).isEqualTo("갓버수문장");
		assertThat(dbSummoner1.getSummonerLevel()).isEqualTo(340);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
	}
	
	
	//----------------------setSummoner() 메소드 Test Case------------------------------------
	
	@Test
	public void setSummonerCase1() {
		//test Case 1 : 게임 플랫폼에 특정 닉네임을 가진 유저가 존재하고 DB에도 해당 유저가 존재할 경우
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner api_summoner1 = new Summoner();
		api_summoner1.setId("id1");
		api_summoner1.setName(summonerName);
		api_summoner1.setSummonerLevel(333);
		
		when(riotRestApi.getSummonerByName(summonerName))
		.thenReturn(api_summoner1);
		
		Summoner db_summoner1 = new Summoner();
		db_summoner1.setId("id1");
		db_summoner1.setName("갓버수문장");
		db_summoner1.setSummonerLevel(320);
		
		when(summonerRepository.findSummonerById("id1"))
		.thenReturn(db_summoner1);
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		
		//then
		assertThat(db_summoner1.getId()).isEqualTo("id1");
		assertThat(db_summoner1.getName()).isEqualTo("푸켓푸켓");
		assertThat(db_summoner1.getSummonerLevel()).isEqualTo(333);
		
		assertThat(summonerDto.getSummonerid()).isEqualTo(db_summoner1.getId());
		assertThat(summonerDto.getName()).isEqualTo(db_summoner1.getName());
		assertThat(summonerDto.getSummonerLevel()).isEqualTo(db_summoner1.getSummonerLevel());
		
		verify(summonerRepository, times(0)).saveSummoner(api_summoner1);
	}
	
	@Test
	public void setSummonerCase2() {
		//test Case 2 : 게임 플랫폼에 특정 닉네임을 가진 유저가 존재하지만 DB에는 해당 유저가 없을 경우
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		summoner1.setSummonerLevel(333);
		
		when(riotRestApi.getSummonerByName(summonerName))
		.thenReturn(summoner1);
		when(summonerRepository.findSummonerById("id1"))
		.thenThrow(EmptyResultDataAccessException.class);
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		
		//then
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner1.getId());
		assertThat(summonerDto.getName()).isEqualTo(summoner1.getName());
		assertThat(summonerDto.getSummonerLevel()).isEqualTo(summoner1.getSummonerLevel());
		
		verify(summonerRepository, times(1)).saveSummoner(summoner1);
	}
	
	@Test
	public void setSummonerCase3() {
		//test Case 3 : REST 통신이 실패한 경우(EX. 해당 닉네임을 가진 유저가 존재하지 않을 경우)
		
		//given
		//테스트할 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//Mock 객체 데이터 셋팅
		when(riotRestApi.getSummonerByName(summonerName))
		.thenThrow(new WebClientResponseException(404,"존재하지 않는 닉네임입니다.", null, null, null));
		
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setSummoner(summonerName));
		
		assertThat(e.getStatusCode().value()).isEqualTo(404);
		assertThat(e.getStatusText()).isEqualTo("존재하지 않는 닉네임입니다.");
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(0)).saveSummoner(any(Summoner.class));
	}
	
	
	//----------------------setLeague() 메소드 Test Case------------------------------------
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void setLeagueCase1() {
		//test Case 1 : REST 통신을 통해 유저의 랭크 관련 정보를 가져온 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		RankDto teamRank = new RankDto();
		teamRank.setSummonerId(summoner.getSummonerid());
		teamRank.setQueueType("RANKED_TEAM_5x5");
		teamRank.setRank("IV");
		teamRank.setTier("GOLD");
		teamRank.setWins(100);
		teamRank.setLosses(50);
		
		RankDto soloRank = new RankDto();
		soloRank.setSummonerId(summoner.getSummonerid());
		soloRank.setQueueType("RANKED_SOLO_5x5");
		soloRank.setRank("III");
		soloRank.setTier("GOLD");
		soloRank.setWins(55);
		soloRank.setLosses(33);
		
		List<RankDto> ranks = new ArrayList<>();
		ranks.add(teamRank);
		ranks.add(soloRank);
		
		when(riotRestApi.getLeague(summoner.getSummonerid()))
		.thenReturn(ranks);
		
		
		//when
		TotalRanksDto totalRanksDto = summonerService.setLeague(summoner);
		
		
		//then
		assertThat(totalRanksDto.getSolorank()).isEqualTo(soloRank);
		assertThat(totalRanksDto.getTeamrank()).isEqualTo(teamRank);
		assertThat(totalRanksDto.getSolorank().getSeasonId()).isEqualTo(22);
		assertThat(totalRanksDto.getTeamrank().getSeasonId()).isEqualTo(22);
		
		verify(riotRestApi, times(1)).getLeague(anyString());
		verify(summonerRepository, times(1)).saveRanks(any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setLeagueCase2() {
		//test Case 2 : REST 통신이 실패한 경우(EX. 요청 제한 횟수를 초과한 경우)
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		when(riotRestApi.getLeague(summoner.getSummonerid()))
		.thenThrow(new WebClientResponseException(429, "많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.", null, null, null));
		
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setLeague(summoner));
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		assertThat(e.getStatusText()).isEqualTo("많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.");
		
		verify(riotRestApi, times(1)).getLeague(anyString());
		verify(summonerRepository, times(0)).saveRanks(any(List.class));
	}
	
	
	
	//----------------------getLeague() 메소드 Test Case------------------------------------
	
	@Test
	public void getLeagueCase1() {
		//test Case 1 : DB에서 유저 Rank 관련 데이터 가져올 때 soloRank 정보만 있을 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		Rank soloRank = new Rank();
		soloRank.setCk(new RankCompKey("id1", "RANKED_SOLO_5x5", 22));
		soloRank.setTier("GOLD");
		soloRank.setRank("III");
		soloRank.setWins(50);
		soloRank.setLosses(30);
		
		RankCompKey soloRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_SOLO_5x5", currentSeasonId);
		RankCompKey flexRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_FLEX_SR", currentSeasonId);
		
		when(summonerRepository.findRank(soloRankKey)).thenReturn(soloRank);
		when(summonerRepository.findRank(flexRankKey)).thenReturn(null);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getTeamrank()).isEqualTo(null);
		
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(soloRank.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(soloRank.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(soloRank.getLosses());
	}
	
	@Test
	public void getLeagueCase2() {
		//test Case 2 : DB에서 유저 Rank 관련 데이터 가져올 때 teamRank 정보만 있을 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		Rank flexRank = new Rank();
		flexRank.setCk(new RankCompKey("id1", "RANKED_FLEX_SR", 22));
		flexRank.setTier("GOLD");
		flexRank.setRank("II");
		flexRank.setWins(70);
		flexRank.setLosses(44);
		
		RankCompKey soloRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_SOLO_5x5", currentSeasonId);
		RankCompKey flexRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_FLEX_SR", currentSeasonId);
		
		when(summonerRepository.findRank(soloRankKey)).thenReturn(null);
		when(summonerRepository.findRank(flexRankKey)).thenReturn(flexRank);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getSolorank()).isEqualTo(null);
		
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(flexRank.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(flexRank.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(flexRank.getLosses());
	}
	
	@Test
	public void getLeagueCase3() {
		//test Case 3 : DB에서 유저 Rank 관련 데이터 가져올 때 
		//teamRank, soloRank 정보 둘다 있을 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		Rank soloRank = new Rank();
		soloRank.setCk(new RankCompKey("id1", "RANKED_SOLO_5x5", currentSeasonId));
		soloRank.setTier("GOLD");
		soloRank.setRank("III");
		soloRank.setWins(50);
		soloRank.setLosses(30);
		
		Rank flexRank = new Rank();
		flexRank.setCk(new RankCompKey("id1", "RANKED_FLEX_SR", currentSeasonId));
		flexRank.setTier("GOLD");
		flexRank.setRank("II");
		flexRank.setWins(70);
		flexRank.setLosses(44);
		
		RankCompKey soloRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_SOLO_5x5", currentSeasonId);
		RankCompKey flexRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_FLEX_SR", currentSeasonId);
		
		when(summonerRepository.findRank(soloRankKey)).thenReturn(soloRank);
		when(summonerRepository.findRank(flexRankKey)).thenReturn(flexRank);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(soloRank.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(soloRank.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(soloRank.getLosses());
		
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(flexRank.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(flexRank.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(flexRank.getLosses());
	}
	
	
	@Test
	public void getLeagueCase4() {
		//test Case 2 : DB에서 유저 Rank 관련 데이터 가져올 때 데이터가 없는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("푸켓푸켓");
		summoner.setSummonerLevel(553);
		
		//Mock 객체 데이터 셋팅
		RankCompKey soloRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_SOLO_5x5", currentSeasonId);
		RankCompKey flexRankKey = new RankCompKey(summoner.getSummonerid(), "RANKED_FLEX_SR", currentSeasonId);
		
		when(summonerRepository.findRank(soloRankKey)).thenReturn(null);
		when(summonerRepository.findRank(flexRankKey)).thenReturn(null);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getSolorank()).isEqualTo(null);
		assertThat(totalRankDto.getTeamrank()).isEqualTo(null);
	}
	
	
	//----------------------setMatches() 메소드 Test Case------------------------------------
	
	@Test
	void getRenewMatchesCase1() {
		//test Case 1 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 모든 matchId에 해당하는 match 데이터들이 DB에 없는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("id1");
		summonerDto.setName("푸켓푸켓");
		
		//Mock 객체 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id1");
		summoner.setPuuid("puuId1");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid("");
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		List<String> matchIds = new ArrayList<>();
		matchIds.add("matchId5");
		matchIds.add("matchId4");
		matchIds.add("matchId3");
		matchIds.add("matchId2");
		matchIds.add("matchId1");
		
		when(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastmatchid()))
		.thenReturn(matchIds);
		
		when(summonerRepository.findMatchById("matchId5")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId4")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId3")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId2")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId1")).thenReturn(null);
		
		List<String> recent_match_ids = new ArrayList<>();
		recent_match_ids.addAll(matchIds);
		
		Match match1 = new Match();
		match1.setMatchId("matchId1");
		Match match2 = new Match();
		match2.setMatchId("matchId2");
		Match match3 = new Match();
		match3.setMatchId("matchId3");
		Match match4 = new Match();
		match4.setMatchId("matchId4");
		Match match5 = new Match();
		match5.setMatchId("matchId5");
		
		List<Match> recent_match = new ArrayList<>();
		recent_match.add(match5);
		recent_match.add(match4);
		recent_match.add(match3);
		recent_match.add(match2);
		recent_match.add(match1);
		
		RecentMatchesDto recentMatchesDto = new RecentMatchesDto(recent_match, null);
		
		when(riotRestApi.getMatchesByNonBlocking(recent_match_ids)).thenReturn(recentMatchesDto);
		
		//when
		List<MatchDto> recent_match_dtos = summonerService.getRenewMatches(summonerDto);
		
		//then
		assertThat(summoner.getLastmatchid()).isEqualTo("matchId5");	
		
		assertThat(recent_match_dtos.size()).isEqualTo(5);
		assertThat(recent_match_dtos.get(0).getMatchid()).isEqualTo("matchId5");
		assertThat(recent_match_dtos.get(1).getMatchid()).isEqualTo("matchId4");
		assertThat(recent_match_dtos.get(2).getMatchid()).isEqualTo("matchId3");
		assertThat(recent_match_dtos.get(3).getMatchid()).isEqualTo("matchId2");
		assertThat(recent_match_dtos.get(4).getMatchid()).isEqualTo("matchId1");
	}
	
	@Test
	void getRenewMatchesCase2() {
		//test Case 2 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 matchIdList에 일부 match정보가 DB에 저장되어있는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("id1");
		summonerDto.setName("푸켓푸켓");
		
		//Mock 객체 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id1");
		summoner.setPuuid("puuId1");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid("");
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		List<String> matchIds = new ArrayList<>();
		matchIds.add("matchId5");
		matchIds.add("matchId4");
		matchIds.add("matchId3");
		matchIds.add("matchId2");
		matchIds.add("matchId1");
		
		when(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastmatchid()))
		.thenReturn(matchIds);
		
		when(summonerRepository.findMatchById("matchId5")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId4")).thenReturn(new Match()); //이미 저장된 매치 정보
		when(summonerRepository.findMatchById("matchId3")).thenReturn(null);
		when(summonerRepository.findMatchById("matchId2")).thenReturn(new Match()); //이미 저장된 매치 정보
		when(summonerRepository.findMatchById("matchId1")).thenReturn(null);
		
		List<String> recent_match_ids = new ArrayList<>();
		recent_match_ids.add("matchId5");
		recent_match_ids.add("matchId3");
		recent_match_ids.add("matchId1");
		
		Match match1 = new Match();
		match1.setMatchId("matchId1");
		Match match3 = new Match();
		match3.setMatchId("matchId3");
		Match match5 = new Match();
		match5.setMatchId("matchId5");
		
		List<Match> recent_matches = new ArrayList<>();
		recent_matches.add(match5);
		recent_matches.add(match3);
		recent_matches.add(match1);
		
		RecentMatchesDto recentMatchesDto = new RecentMatchesDto(recent_matches, null);
		
		when(riotRestApi.getMatchesByNonBlocking(recent_match_ids)).thenReturn(recentMatchesDto);
		
		//when
		List<MatchDto> recent_match_dtos = summonerService.getRenewMatches(summonerDto);
		
		//then
		assertThat(summoner.getLastmatchid()).isEqualTo("matchId5");
		
		assertThat(recent_match_dtos.size()).isEqualTo(3);
		assertThat(recent_match_dtos.get(0).getMatchid()).isEqualTo("matchId5");
		assertThat(recent_match_dtos.get(1).getMatchid()).isEqualTo("matchId3");
		assertThat(recent_match_dtos.get(2).getMatchid()).isEqualTo("matchId1");
	}
	
	@Test
	void getRenewMatchesCase3() {
		//test Case 3 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 matchIdList의 size()가 0인 경우
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("id1");
		summonerDto.setName("푸켓푸켓");
		
		//Mock 객체 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id1");
		summoner.setPuuid("puuId1");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid("");
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		List<String> matchIds = new ArrayList<>();
		
		when(riotRestApi.getAllMatchIds(summoner.getPuuid(),summoner.getLastmatchid()))
		.thenReturn(matchIds);
		
		RecentMatchesDto recentMatchesDto = new RecentMatchesDto(new ArrayList<>(), new ArrayList<>());
		when(riotRestApi.getMatchesByNonBlocking(matchIds)).thenReturn(recentMatchesDto);
		
		//when
		List<MatchDto> recent_match_dtos = summonerService.getRenewMatches(summonerDto);
		
		//then
		assertThat(recent_match_dtos.size()).isEqualTo(0);
		assertThat(summoner.getLastmatchid()).isEqualTo("");
		verify(summonerRepository, times(0)).findMatchid(anyString());
	}
	
	@Test
	void getRenewMatchesCase4() {
		//test Case 4 : REST API 통신으로 matchIdList를 가져오는데 실패한 경우(EX. 요청 제한 횟수를 초과한 경우)
		
		//given
		//테스트할 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("id1");
		summonerDto.setName("푸켓푸켓");
		
		//Mock 객체 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id1");
		summoner.setPuuid("puuId1");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid("matchId5");
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		when(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastmatchid()))
		.thenThrow(new WebClientResponseException(429, "많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.", null, null, null));
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.getRenewMatches(summonerDto));
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		assertThat(e.getStatusText()).isEqualTo("많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요.");
		
		verify(summonerRepository, times(1)).findSummonerById(anyString());
		verify(riotRestApi, times(1)).getAllMatchIds(summoner.getPuuid(), summoner.getLastmatchid());
	}
	
	
	
	//----------------------getMatches() 메소드 Test Case------------------------------------
	
	@Test
	void getOldMatchesCase1() {
		//test Case 1 : parameter로 전달 받은 조건들을 이용해 DB에서 적절한 matchList를 반환하는 상황
		
		//given
		//테스트할 메소드 파라미터 값
		MatchParamDto matchparam = new MatchParamDto();
		matchparam.setName("푸켓푸켓");
		matchparam.setSummonerid("id1");
		
		//Mock 객체 데이터 셋팅
		Match match1 = new Match();
		match1.setMatchId("match1");
		Match match2 = new Match();
		match2.setMatchId("match2");
		Match match3 = new Match();
		match3.setMatchId("match3");
		Match match4 = new Match();
		match4.setMatchId("match4");
		
		List<Match> matchList = new ArrayList<>();
		matchList.add(match4);
		matchList.add(match3);
		matchList.add(match2);
		matchList.add(match1);
		
		when(summonerRepository.findMatchList(
				matchparam.getSummonerid(),
				matchparam.getGametype(),
				matchparam.getChampion(),
				matchparam.getCount()
				)
			)
		.thenReturn(matchList);
		
		//when
		List<MatchDto> matchListDto = summonerService.getOldMatches(matchparam);
		
		//then
		assertThat(matchListDto.size()).isEqualTo(matchList.size());
		assertThat(matchListDto.get(0).getMatchid()).isEqualTo(match4.getMatchId());
		assertThat(matchListDto.get(1).getMatchid()).isEqualTo(match3.getMatchId());
		assertThat(matchListDto.get(2).getMatchid()).isEqualTo(match2.getMatchId());
		assertThat(matchListDto.get(3).getMatchid()).isEqualTo(match1.getMatchId());
	}
	
	@Test
	void getOldMatchesCase2() {
		//test Case 2 : parameter로 전달 받은 조건에 만족하는 matchList가 없는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		MatchParamDto matchparam = new MatchParamDto();
		matchparam.setName("푸켓푸켓");
		matchparam.setSummonerid("id1");
		
		//Mock 객체 데이터 셋팅
		List<Match> matchList = new ArrayList<>();
		
		when(summonerRepository.findMatchList(
				matchparam.getSummonerid(),
				matchparam.getGametype(),
				matchparam.getChampion(),
				matchparam.getCount()
				)
			)
		.thenReturn(matchList);
		
		//when
		List<MatchDto> matchListDto = summonerService.getOldMatches(matchparam);
		
		//then
		assertThat(matchListDto.size()).isEqualTo(matchList.size());
		assertThat(matchListDto.size()).isEqualTo(0);
	}
	
	//----------------------getMostchamp() 메소드 Test Case------------------------------------
	
	@Test
	void getMostChampCase1() {
		//test Case 1 : parameter로 전달받은 조건에 만족하는 모스트 챔피언 통계 데이터를 가져오는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		MostchampParamDto mostChampParam = new MostchampParamDto();
		mostChampParam.setGamequeue(420);
		mostChampParam.setSeason(12);
		mostChampParam.setSummonerid("Id1");
		
		//Mock 객체 데이터 셋팅
		List<String> champIds = new ArrayList<>();
		champIds.add("가렌");
		champIds.add("이블린");
		champIds.add("탈론");
		
		when(summonerRepository.findMostchampids("Id1", 420, 12))
		.thenReturn(champIds);
		
		MostChampDto mostchampDto1 = new MostChampDto();
		mostchampDto1.setChampionid("가렌");
		mostchampDto1.setTotalgame(100);
		MostChampDto mostchampDto2 = new MostChampDto();
		mostchampDto2.setChampionid("이블린");
		mostchampDto1.setTotalgame(80);
		MostChampDto mostchampDto3 = new MostChampDto();
		mostchampDto3.setChampionid("탈론");
		mostchampDto1.setTotalgame(70);
		
		when(summonerRepository.findChamp("Id1", "가렌", 420, 12))
		.thenReturn(mostchampDto1);
		when(summonerRepository.findChamp("Id1", "이블린", 420, 12))
		.thenReturn(mostchampDto2);
		when(summonerRepository.findChamp("Id1", "탈론", 420, 12))
		.thenReturn(mostchampDto3);
		
		//when
		List<MostChampDto> mostChamps = summonerService.getMostChamp(mostChampParam);
		
		//then
		assertThat(mostChamps.size()).isEqualTo(3);
		assertThat(mostChamps.get(0)).isEqualTo(mostchampDto1);
		assertThat(mostChamps.get(1)).isEqualTo(mostchampDto2);
		assertThat(mostChamps.get(2)).isEqualTo(mostchampDto3);
	}
	
	@Test
	void getMostChampCase2() {
		//test Case 2 : parameter로 전달받은 조건에 만족하는 모스트 챔피언 통계 데이터가 존재하지 않는 경우
		
		//given
		//테스트할 메소드 파라미터 값
		MostchampParamDto mostChampParam = new MostchampParamDto();
		mostChampParam.setGamequeue(420);
		mostChampParam.setSeason(12);
		mostChampParam.setSummonerid("Id1");
		
		//Mock 객체 데이터 셋팅
		List<String> champIds = new ArrayList<>();
		
		when(summonerRepository.findMostchampids("Id1", 420, 12))
		.thenReturn(champIds);
		
		
		//when
		List<MostChampDto> mostChamps = summonerService.getMostChamp(mostChampParam);
		
		//then
		assertThat(mostChamps.size()).isEqualTo(0);
		
		verify(summonerRepository, times(1)).findMostchampids(anyString(), anyInt(), anyInt());
		verify(summonerRepository, times(0)).findChamp(anyString(), anyString(), anyInt(), anyInt());
	}
	
}
