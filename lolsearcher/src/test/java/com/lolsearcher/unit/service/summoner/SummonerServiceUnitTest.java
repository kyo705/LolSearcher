package com.lolsearcher.unit.service.summoner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.summoner.SummonerDto;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.summoner.SummonerService;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	@Mock private RiotRestAPI riotRestApi;
	@Mock private SummonerRepository summonerRepository;
	
	private SummonerService summonerService;
	
	@BeforeEach
	void upset() {
		summonerService = new SummonerService(riotRestApi, summonerRepository);
	}

	//----------------------findDbSummoner() 메소드 Test Case------------------------------------

	@Test
	@DisplayName("findDbSummoner : DB에 특정 닉네임이 1개 존재할 때 해당 엔티티를 반환한다.")
	void findOneSummonerByDb() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> oneSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 1);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(oneSummoner);
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerName);
	}
	
	@Test
	@DisplayName("findDbSummoner : DB에 특정 닉네임이 존재하지 않을 때 예외가 발생한다.")
	void findZeroSummonerByDb() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> zeroSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 0);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(zeroSummoner);
		//when & then
		EmptyResultDataAccessException e = assertThrows(EmptyResultDataAccessException.class, ()->{
			summonerService.findDbSummoner(summonerName);
		});
		assertThat(e.getActualSize()).isEqualTo(0);
	}
	
	@Test
	@DisplayName("findDbSummoner : DB에 특정 닉네임이 2개 이상 존재할 때 예외가 발생한다.")
	void findSummonersByDb() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> summoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 3);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(summoners);
		//when & then
		IncorrectResultSizeDataAccessException e = assertThrows(IncorrectResultSizeDataAccessException.class, ()->{
			summonerService.findDbSummoner(summonerName);
		});
		assertThat(e.getActualSize()).isEqualTo(summoners.size());
		assertThat(e.getExpectedSize()).isEqualTo(1);
	}
	
	
	//----------------------updateDbSummoner() 메소드 Test Case------------------------------------
	
	@Test
	@DisplayName("updateDbSummoner : DB에서 같은 닉네임을 가지는 모든 유저가 실제 존재하는 유저일 경우 모두 갱신한다.")
	public void updateDbSummonerWithAllExist() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> sameNameSummoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 3);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(sameNameSummoners);
		
		for(Summoner summoner : sameNameSummoners) {
			given(riotRestApi.getSummonerById(summoner.getSummonerId()))
			.willReturn(SummonerServiceTestUpSet.getRealSummoner(summoner));
		}
		//when
		summonerService.updateDbSummoner(summonerName);
		//then
		for(Summoner summoner : sameNameSummoners) {
			verify(riotRestApi, times(1)).getSummonerById(summoner.getSummonerId());
			assertThat(summoner.getName()).isNotEqualTo(summonerName);
		}
		
	}
	
	
	@Test
	@DisplayName("updateDbSummoner : 업데이트 해야할 유저 중 한명 이상 존재하지 않는 유저일 경우 DB에서 해당 유저 데이터를 삭제한다.")
	public void updateDbSummonersWithNoExist() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> sameNameSummoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 3);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(sameNameSummoners);
		
		for(int i=0;i<sameNameSummoners.size();i++) {
			Summoner summoner = sameNameSummoners.get(i);
			if(i%2==0) {
				given(riotRestApi.getSummonerById(summoner.getSummonerId()))
				.willReturn(SummonerServiceTestUpSet.getRealSummoner(summoner));
			}else {
				given(riotRestApi.getSummonerById(summoner.getSummonerId()))
				.willThrow(new WebClientResponseException(
						HttpStatus.BAD_REQUEST.value(), 
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						null, null, null));
			}
		}
		//when
		summonerService.updateDbSummoner(summonerName);
		//then
		for(int i=0;i<sameNameSummoners.size();i++) {
			Summoner summoner = sameNameSummoners.get(i);
			if(i%2==0) {
				assertThat(summoner.getName()).isNotEqualTo(summonerName);
			}else {
				verify(summonerRepository, times(1)).deleteSummoner(summoner);
			}
		}
	}
	
	@Test
	@DisplayName("updateDbSummoner : 업데이트 진행 중 API 요청 최대 횟수 초과한 경우 http 429 에러를 발생시키고 종료된다.")
	public void updateDbSummonerWithTooManyRequest() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> sameNameSummoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 4);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(sameNameSummoners);
		
		for(int i=0;i<sameNameSummoners.size();i++) {
			Summoner summoner = sameNameSummoners.get(i);
			if(i>=(sameNameSummoners.size()/2)) {
				given(riotRestApi.getSummonerById(summoner.getSummonerId()))
				.willThrow(new WebClientResponseException(
						HttpStatus.TOO_MANY_REQUESTS.value(), 
						HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), 
						null, null, null));
				break;
			}
			given(riotRestApi.getSummonerById(summoner.getSummonerId()))
			.willReturn(SummonerServiceTestUpSet.getRealSummoner(summoner));
		}
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, ()->{
			summonerService.updateDbSummoner(summonerName);
		});
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		verify(riotRestApi, times(sameNameSummoners.size()/2 + 1)).getSummonerById(any());
	}
	
	
	//----------------------renewSummoner() 메소드 Test Case------------------------------------
	
	@Test
	@DisplayName("renewSummoner : DB에 존재하는 소환사 데이터를 API를 통해 최신 데이터로 갱신한다.")
	public void renewSummonerByExistDB() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummoner(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		Summoner dbSummoner = SummonerServiceTestUpSet.getSummoner(summonerName);
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId())).willReturn(dbSummoner);
		//when
		SummonerDto renewSummoner = summonerService.renewSummoner(summonerName);
		//then
		assertThat(renewSummoner.getSummonerId()).isEqualTo(apiSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(apiSummoner.getName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(apiSummoner.getLastRenewTimeStamp());
		
		assertThat(renewSummoner.getSummonerId()).isEqualTo(dbSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(dbSummoner.getName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(dbSummoner.getLastRenewTimeStamp());
		
		verify(summonerRepository, times(0)).saveSummoner(apiSummoner);
	}
	
	@Test
	@DisplayName("renewSummoner : DB에 존재하지 않는 소환사 데이터를 API를 통해 새로 저장한다.")
	public void renewSummonerByNotExistDB() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummoner(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId()))
		.willThrow(EmptyResultDataAccessException.class);
		//when
		SummonerDto renewSummoner = summonerService.renewSummoner(summonerName);
		//then
		assertThat(renewSummoner.getSummonerId()).isEqualTo(apiSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(apiSummoner.getName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(apiSummoner.getLastRenewTimeStamp());
		
		verify(summonerRepository, times(1)).saveSummoner(apiSummoner);
	}
	
	@Test
	@DisplayName("renewSummoner : 잘못된 API 요청으로 실패한 경우 예외를 발생시킨다.")
	public void renewSummonerByImproperRequest() {
		//given
		String summonerName = "푸켓푸켓";
		
		given(riotRestApi.getSummonerByName(summonerName)).willThrow(
				new WebClientResponseException(
						HttpStatus.NOT_FOUND.value(), 
						HttpStatus.NOT_FOUND.getReasonPhrase(),
						null, null, null));
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.renewSummoner(summonerName));
		
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(0)).findSummonerById(anyString());
	}
	
	@Test
	@DisplayName("renewSummoner : 다수의 클라이언트가 같은 데이터 DB에 저장할 때 처음 요청만 저장하고 나머지는 예외를 발생시킨다.")
	public void renewSummonerByOverlapData() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummoner(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId())).willThrow(EmptyResultDataAccessException.class);
		
		willThrow(DataIntegrityViolationException.class).given(summonerRepository).saveSummoner(apiSummoner);
		//when & then
		assertThrows(DataIntegrityViolationException.class,()->{
			summonerService.renewSummoner(summonerName);
		});
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(1)).findSummonerById(anyString());
		verify(summonerRepository, times(1)).saveSummoner(any(Summoner.class));
	}
}
