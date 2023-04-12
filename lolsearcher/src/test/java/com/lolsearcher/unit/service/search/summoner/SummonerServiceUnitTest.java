package com.lolsearcher.unit.service.search.summoner;

import com.lolsearcher.api.lolsearcher.ReactiveLolSearcherServerApi;
import com.lolsearcher.exception.exception.search.summoner.NotExistedSummonerInDBException;
import com.lolsearcher.exception.exception.search.summoner.NotExistedSummonerInGameServerException;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.search.summoner.RequestSummonerDto;
import com.lolsearcher.model.response.front.search.summoner.SummonerDto;
import com.lolsearcher.repository.search.summoner.SummonerRepository;
import com.lolsearcher.service.search.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	@Mock private ReactiveLolSearcherServerApi reactiveLolSearcherServerApi;
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
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();
		
		List<Summoner> oneSummoner = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), 1);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(oneSummoner);

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerInfo.getSummonerName());
	}
	
	@Test
	@DisplayName("DB에 특정 닉네임이 존재하지 않는다면 에러가 발생한다.")
	void findZeroSummonerByDb() {

		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();
		List<Summoner> zeroSummoner = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), 0);

		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(zeroSummoner);

		//when & then
		NotExistedSummonerInDBException ex = assertThrows(NotExistedSummonerInDBException.class,
				()->summonerService.getSummonerDto(summonerInfo));

		assertThat(ex.getSummonerName()).isEqualTo(summonerInfo.getSummonerName());
	}

	@ParameterizedTest
	@ValueSource(ints = {2,3,10})
	@DisplayName("DB에 특정 닉네임이 둘 이상 존재할 때 API 요청으로 업데이트 후 해당 닉네임에 해당하는 유저를 반환한다.")
	void findSummonersByDbWithRealName(int size) {

		//given
		RequestSummonerDto summonerInfo = SummonerServiceTestSetup.getRequestSummonerInfoWithNoRenew();
		
		List<Summoner> summoners = SummonerServiceTestSetup.getSameNameSummoners(summonerInfo.getSummonerName(), size);
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(summoners);

		List<String> summonerIds = summoners.stream().map(Summoner::getSummonerId).collect(Collectors.toList());
		given(reactiveLolSearcherServerApi.updateSameNameSummoners(summonerIds)).willReturn(summoners.get(0));

		//when
		SummonerDto summonerDto = summonerService.getSummonerDto(summonerInfo);

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
		given(summonerRepository.findSummonerByName(summonerInfo.getSummonerName())).willReturn(summoners);

		List<String> summonerIds = summoners.stream().map(Summoner::getSummonerId).collect(Collectors.toList());
		given(reactiveLolSearcherServerApi.updateSameNameSummoners(summonerIds)).willReturn(null);

		//when & then
		NotExistedSummonerInGameServerException ex = assertThrows(NotExistedSummonerInGameServerException.class,
				()-> summonerService.getSummonerDto(summonerInfo));

		assertThat(ex.getSummonerName()).isEqualTo(summonerInfo.getSummonerName());
	}
}
