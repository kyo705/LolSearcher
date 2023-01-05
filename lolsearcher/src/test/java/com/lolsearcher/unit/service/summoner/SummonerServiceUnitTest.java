package com.lolsearcher.unit.service.summoner;

import com.lolsearcher.api.riotgames.RiotGamesAPI;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.front.RequestSummonerDto;
import com.lolsearcher.model.response.front.summoner.SummonerDto;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	@Mock private RiotGamesAPI riotGamesApi;
	@Mock private SummonerRepository summonerRepository;
	
	private SummonerService summonerService;
	
	@BeforeEach
	void upset() {
		summonerService = new SummonerService(riotGamesApi, summonerRepository);
	}

	//----------------------findDbSummoner() 메소드 Test Case------------------------------------

	@Test
	@DisplayName("DB에 특정 닉네임이 1개 존재하고 클라이언트가 갱신 요청을 하지 않았다면 DB 데이터를 DTO로 변환하여 반환한다.")
	void findOneSummonerByDbWithNoRenew() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();
		
		List<Summoner> oneSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 1);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(oneSummoner);

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerInfo.getSummonerName());
		assertThat(summonerDto.isRenewed()).isEqualTo(false);
	}
	
	@Test
	@DisplayName("DB에 특정 닉네임이 존재하지 않는다면 API 요청으로 데이터를 가져온다.")
	void findZeroSummonerByDb() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();

		List<Summoner> zeroSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 0);
		Summoner apiSummoner = SummonerServiceTestUpSet.getSummonerByNameWithRenewedRecently(summonerInfo.getSummonerName());

		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(zeroSummoner);
		given(riotGamesApi.getSummonerByName(summonerInfo.getSummonerName())).willReturn(apiSummoner);

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(summonerDto).isNotNull();
		assertThat(summonerDto.isRenewed()).isEqualTo(true);
	}

	@Test
	@DisplayName("DB에 특정 닉네임이 존재하지 않아 API 요청을 하였지만 해당 닉네임이 존재하지 않는다면 예외가 발생한다.")
	void findZeroSummonerByDbWithNoApiData() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();

		List<Summoner> zeroSummoner = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 0);

		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(zeroSummoner);
		given(riotGamesApi.getSummonerByName(summonerInfo.getSummonerName()))
				.willThrow(new WebClientResponseException(
						HttpStatus.BAD_REQUEST.value(),
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						null,
						null,
						null
				));

		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()-> summonerService.getSummonerDto(summonerInfo));

		//then
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DisplayName("DB에 특정 닉네임이 둘 이상 존재할 때 API 요청으로 업데이트 후 해당 닉네임에 해당하는 유저를 반환한다.")
	void findSummonersByDbWithRealName() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();
		
		List<Summoner> summoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 3);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(summoners);
		for(int i = 0; i < summoners.size(); i++){
			Summoner summoner = summoners.get(i);

			given(riotGamesApi.getSummonerById(summoner.getSummonerId()))
					.willReturn(SummonerServiceTestUpSet.changeSummonerName(summoner, i==0));
		}

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerInfo.getSummonerName());
		assertThat(summonerDto.isRenewed()).isEqualTo(false);
		verify(riotGamesApi, times(0)).getSummonerByName(any());
	}

	@Test
	@DisplayName("DB에 특정 닉네임에 해당하는 유저가 둘 이상 존재한다면 API 요청으로 업데이트 후 " +
			"해당 유저들 중 특정 닉네임에 해당하는 유저가 없다면 API 요청으로 해당 닉네임에 대해 조회하였을 때 " +
			"API 요청이 성공할 경우 DB에 저장 후 해당 값을 리턴한다.")
	void findSummonersByDbWithNoRealName() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();

		List<Summoner> summoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 3);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(summoners);
		for (Summoner summoner : summoners) {
			given(riotGamesApi.getSummonerById(summoner.getSummonerId()))
					.willReturn(SummonerServiceTestUpSet.changeSummonerName(summoner, false));
		}
		given(riotGamesApi.getSummonerByName(summonerInfo.getSummonerName()))
				.willReturn(SummonerServiceTestUpSet.getSummonerByNameWithRenewedRecently(summonerInfo.getSummonerName()));

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerInfo.getSummonerName());
		assertThat(summonerDto.isRenewed()).isEqualTo(true);
		verify(riotGamesApi, times(1)).getSummonerByName(any());
		verify(summonerRepository, times(1)).saveSummoner(any());
	}

	@Test
	@DisplayName("DB에 특정 닉네임에 해당하는 유저가 둘 이상 존재한다면 API 요청으로 업데이트 후 " +
			"해당 유저들 중 특정 닉네임에 해당하는 유저가 없다면 API 요청으로 해당 닉네임에 대해 조회하였을 때 " +
			"API 요청이 실패할 경우 예외가 발생한다.")
	void findSummonersByDbWithNoRealNameWithNoResponseApi() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithNoRenew();

		List<Summoner> summoners = SummonerServiceTestUpSet.getSameNameSummoners(summonerInfo.getSummonerName(), 3);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(summoners);

		for (Summoner summoner : summoners) {
			given(riotGamesApi.getSummonerById(summoner.getSummonerId()))
					.willReturn(SummonerServiceTestUpSet.changeSummonerName(summoner, false));
		}
		given(riotGamesApi.getSummonerByName(summonerInfo.getSummonerName()))
				.willThrow(new WebClientResponseException(
						HttpStatus.BAD_REQUEST.value(),
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						null, null, null));

		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				() ->summonerService.getSummonerDto(summonerInfo));

		//then
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@DisplayName("DB에 존재하는 소환사 데이터가 갱신한지 특정 시간을 지나지 않았을 경우 갱신 요청이 들어와도 갱신하지 않는다.")
	public void renewSummonerByExistDBWithNoRenew() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithRenew();
		String summonerName = summonerInfo.getSummonerName();

		Summoner dbSummoner = SummonerServiceTestUpSet.getSummonerByNameWithRenewedRecently(summonerName);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(List.of(dbSummoner));

		//when
		SummonerDto renewSummoner = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(renewSummoner.getSummonerId()).isEqualTo(dbSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(dbSummoner.getSummonerName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(dbSummoner.getLastRenewTimeStamp());

		assertThat(renewSummoner.isRenewed()).isEqualTo(false);
		verify(riotGamesApi, times(0)).getSummonerByName(any());
		verify(riotGamesApi, times(0)).getSummonerById(any());
	}
	
	@Test
	@DisplayName("DB에 존재하는 소환사 데이터가 갱신한지 특정 시간이 지났다면 갱신 요청이 들어올 때 갱신한다.")
	public void renewSummonerByNotExistDB() {
		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestUpSet.getRequestSummonerInfoWithRenew();
		String summonerName = summonerInfo.getSummonerName();

		Summoner dbSummoner = SummonerServiceTestUpSet.getSummonerByNameWithNotRenewed(summonerName);
		given(summonerRepository.findSummonerByName(summonerName)).willReturn(List.of(dbSummoner));

		Summoner apiSummoner = SummonerServiceTestUpSet.getSummonerByNameWithRenewedRecently(summonerName);
		given(riotGamesApi.getSummonerByName(summonerName)).willReturn(apiSummoner);

		//when
		SummonerDto renewSummoner = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(renewSummoner.getSummonerId()).isEqualTo(apiSummoner.getSummonerId());
		assertThat(renewSummoner.getName()).isEqualTo(apiSummoner.getSummonerName());
		assertThat(renewSummoner.getLastRenewTimeStamp()).isEqualTo(apiSummoner.getLastRenewTimeStamp());

		assertThat(dbSummoner.getSummonerId()).isEqualTo(apiSummoner.getSummonerId());
		assertThat(dbSummoner.getSummonerName()).isEqualTo(apiSummoner.getSummonerName());
		assertThat(dbSummoner.getLastRenewTimeStamp()).isEqualTo(apiSummoner.getLastRenewTimeStamp());

		assertThat(renewSummoner.isRenewed()).isEqualTo(true);
	}

	//----------------------rollbackLastMatchId() 메소드 Test Case------------------------------------

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
