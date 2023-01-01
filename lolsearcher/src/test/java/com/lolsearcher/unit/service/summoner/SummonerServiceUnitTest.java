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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
	@DisplayName("findOldSummoner : DB에 특정 닉네임이 1개 존재할 때 해당 엔티티를 반환한다.")
	void findOneSummonerByDb() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> oneSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 1);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(oneSummoner);
		//when
		SummonerDto summonerDto = summonerService.findOldSummoner(summonerName);
		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerName);
	}
	
	@Test
	@DisplayName("findOldSummoner : DB에 특정 닉네임이 존재하지 않을 때 null을 리턴한다.")
	void findZeroSummonerByDb() {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> zeroSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 0);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(zeroSummoner);

		//when
		SummonerDto summonerDto = summonerService.findOldSummoner(summonerName);

		//then
		assertThat(summonerDto).isNull();
	}

	@ValueSource(booleans = {true, false})
	@ParameterizedTest
	@DisplayName("findOldSummoner : DB에 특정 닉네임이 둘 이상 존재할 때 API 요청으로 업데이트 후 해당 닉네임에 해당하는 유저를 반환한다.")
	void findSummonersByDb(boolean isExistRealSummoner) {
		//given
		String summonerName = "푸켓푸켓";
		
		List<Summoner> summoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerName, 3);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(summoners);
		for(int i = 0; i < summoners.size(); i++){
			Summoner summoner = summoners.get(i);

			given(riotRestApi.getSummonerById(summoner.getSummonerId()))
					.willReturn(SummonerServiceTestUpSet.changeSummonerName(summoner, isExistRealSummoner && (i==0)));
		}

		//when
		SummonerDto summonerDto = summonerService.findOldSummoner(summonerName);

		//then
		if(isExistRealSummoner){
			assertThat(summonerDto.getName()).isEqualTo(summonerName);
		}else{
			assertThat(summonerDto).isNull();
		}

	}

	//----------------------renewSummoner() 메소드 Test Case------------------------------------
	
	@Test
	@DisplayName("findRecentSummoner : DB에 존재하는 소환사 데이터를 API를 통해 최신 데이터로 갱신한다.")
	public void renewSummonerByExistDB() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummonerByName(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		Summoner dbSummoner = SummonerServiceTestUpSet.getSummonerByName(summonerName);
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId())).willReturn(dbSummoner);

		//when
		SummonerDto renewSummoner = summonerService.findRecentSummoner(summonerName);

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
	@DisplayName("findRecentSummoner : DB에 존재하지 않는 소환사 데이터를 API를 통해 새로 저장한다.")
	public void renewSummonerByNotExistDB() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummonerByName(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId()))
		.willThrow(EmptyResultDataAccessException.class);
		//when
		SummonerDto renewSummoner = summonerService.findRecentSummoner(summonerName);
		//then
		assertThat(renewSummoner.getSummonerId()).isEqualTo(apiSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(apiSummoner.getName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(apiSummoner.getLastRenewTimeStamp());
		
		verify(summonerRepository, times(1)).saveSummoner(apiSummoner);
	}
	
	@Test
	@DisplayName("findRecentSummoner : 잘못된 API 요청으로 실패한 경우 예외를 발생시킨다.")
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
				()->summonerService.findRecentSummoner(summonerName));
		
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(0)).findSummonerById(anyString());
	}
	
	@Test
	@DisplayName("findRecentSummoner : 다수의 클라이언트가 같은 데이터 DB에 저장할 때 처음 요청만 저장하고 나머지는 예외를 발생시킨다.")
	public void renewSummonerByOverlapData() {
		//given
		String summonerName = "푸켓푸켓";
		
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummonerByName(summonerName);
		given(riotRestApi.getSummonerByName(summonerName)).willReturn(apiSummoner);
		
		given(summonerRepository.findSummonerById(apiSummoner.getSummonerId())).willThrow(EmptyResultDataAccessException.class);
		
		willThrow(DataIntegrityViolationException.class).given(summonerRepository).saveSummoner(apiSummoner);
		//when & then
		assertThrows(DataIntegrityViolationException.class,()->{
			summonerService.findRecentSummoner(summonerName);
		});
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(1)).findSummonerById(anyString());
		verify(summonerRepository, times(1)).saveSummoner(any(Summoner.class));
	}

	//----------------------renewSummoner() 메소드 Test Case------------------------------------

	@Test
	@DisplayName("rollbackLastMatchId : 파라미터로 전달받은 summonerId에 해당하는 소환사가 DB에 존재할 경우 lastMatchId 필드 값을 갱신한다.")
	public void rollbackLastMatchIdTest() {

		//given
		String summonerId = "summonerId";
		String beforeLastMatchId = "beforeLastMatchId";

		Summoner summoner = SummonerServiceTestUpSet.getSummonerById(summonerId);

		assertThat(summoner.getLastMatchId()).isNotEqualTo(beforeLastMatchId);

		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);

		//when
		summonerService.rollbackLastMatchId(summonerId, beforeLastMatchId);

		//then
		assertThat(summoner.getLastMatchId()).isEqualTo(beforeLastMatchId);
	}
}
