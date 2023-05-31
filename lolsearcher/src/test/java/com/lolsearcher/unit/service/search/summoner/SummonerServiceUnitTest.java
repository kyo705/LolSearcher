package com.lolsearcher.unit.service.search.summoner;

import com.lolsearcher.search.summoner.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	@Mock private SummonerAPI reactiveLolSearcherServerApi;
	@Mock private SummonerRepository summonerRepository;
	
	private SummonerService summonerService;
	
	@BeforeEach
	void upset() {
		summonerService = new SummonerService(reactiveLolSearcherServerApi, summonerRepository);
	}

	@Test
	@DisplayName("DB에 특정 닉네임이 1개 존재할 때 DB 데이터를 DTO로 변환하여 리턴한다.")
	void findOneSummonerByDbWithNoRenew() {

		//given
		String name = "Faker";

		List<Summoner> oneSummoner =
				List.of(
/*					Summoner.builder()
							.summonerId("summonerId1")
							.summonerName(name)
							.summonerLevel(100)
							.id(1L)
							.build()*/
				);
		given(summonerRepository.findByName(name)).willReturn(oneSummoner);

		//when
		SummonerDto summonerDto = summonerService.findByName(name);

		//then
		assertThat(summonerDto.getName()).isEqualTo(name);
	}
	
/*	@Test
	@DisplayName("DB에 특정 닉네임이 존재하지 않는다면 에러가 발생한다.")
	void findZeroSummonerByDb() {

		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();
		List<Summoner> zeroSummoner = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), 0);

		given(summonerRepository.findByName(summonerInfo.getSummonerName())).willReturn(zeroSummoner);

		//when & then
		NotExistedSummonerInDBException ex = assertThrows(NotExistedSummonerInDBException.class,
				()->summonerService.findByName(summonerInfo.getSummonerName()));

		assertThat(ex.getSummonerName()).isEqualTo(summonerInfo.getSummonerName());
	}

	@ParameterizedTest
	@ValueSource(ints = {2,3,10})
	@DisplayName("DB에 특정 닉네임이 둘 이상 존재할 때 API 요청으로 업데이트 후 해당 닉네임에 해당하는 유저를 반환한다.")
	void findSummonersByDbWithRealName(int size) {

		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();
		
		List<Summoner> summoners = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), size);
		given(summonerRepository.findByName(summonerInfo.getSummonerName())).willReturn(summoners);

		List<String> summonerIds = summoners.stream().map(Summoner::getSummonerId).collect(Collectors.toList());
		given(reactiveLolSearcherServerApi.updateSameNameSummoners(summonerIds)).willReturn(Optional.of(summoners.get(0)));

		//when
		SummonerDto summonerDto = summonerService.findByName(summonerInfo.getSummonerName());

		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerInfo.getSummonerName());
	}

	@ParameterizedTest
	@ValueSource(ints = {2,3,10})
	@DisplayName("DB에 특정 닉네임이 둘 이상 존재할 때 API 요청으로 업데이트 후 해당 닉네임에 해당하는 유저가 없으면 예외가 발생한다.")
	void findSummonersByDbWithNoRealName(int size) {

		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();

		List<Summoner> summoners = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), size);
		given(summonerRepository.findByName(summonerInfo.getSummonerName())).willReturn(summoners);

		List<String> summonerIds = summoners.stream().map(Summoner::getSummonerId).collect(Collectors.toList());
		given(reactiveLolSearcherServerApi.updateSameNameSummoners(summonerIds)).willReturn(null);

		//when & then
		NotExistedSummonerInGameServerException ex = assertThrows(NotExistedSummonerInGameServerException.class,
				()-> summonerService.findByName(summonerInfo.getSummonerName()));

		assertThat(ex.getSummonerName()).isEqualTo(summonerInfo.getSummonerName());
	}*/
}
