package com.lolsearcher.unit.service.search.rank;

import com.lolsearcher.search.rank.RankRepository;
import com.lolsearcher.search.rank.RankService;
import com.lolsearcher.search.summoner.SummonerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RankServiceUnitTest {
	
	private RankService rankService;
	@Mock private SummonerService summonerService;
	@Mock private RankRepository rankRepository;
	
	@BeforeEach
	void upset() {
		rankService = new RankService(summonerService, rankRepository);
	}
	
/*
	@Test
	@DisplayName("DB에 정상적인 랭크 데이터가 존재한다면 해당 데이터를 반환한다.")
	public void getValidTotalRanks() {

		//given
		RequestRankDto request = RankServiceTestSetup.getRequestDto();
		String summonerId = request.getSummonerId();

		given(rankRepository.findRanks(summonerId, CURRENT_SEASON_ID))
				.willReturn(RankServiceTestSetup.getValidRanks(summonerId));

		//when
		Map<String, RankDto> totalRankDto = rankService.findAllById(request.getSummonerId(), CURRENT_SEASON_ID);

		//then
		assertThat(totalRankDto.size()).isLessThanOrEqualTo(THE_NUMBER_OF_RANK_TYPE);

		Set<String> set = new HashSet<>();
		for(Map.Entry<String, RankDto> entry : totalRankDto.entrySet()){
			String gameType = entry.getKey();
			RankDto rankDto = entry.getValue();

			assertThat(set.contains(gameType)).isFalse();
			assertThat(rankDto.getSummonerId()).isEqualTo(summonerId);
			assertThat(rankDto.getSeasonId()).isEqualTo(CURRENT_SEASON_ID);
			set.add(gameType);
		}
	}

	@Test
	@DisplayName("DB에 잘못된 랭크 데이터가 존재할 경우 예외가 발생한다.")
	public void getInvalidTotalRanksFromOverData() {

		//given
		RequestRankDto request = RankServiceTestSetup.getRequestDto();
		String summonerId = request.getSummonerId();

		given(rankRepository.findRanks(summonerId, CURRENT_SEASON_ID))
				.willReturn(RankServiceTestSetup.getInvalidRanksToOverData(summonerId));

		//when & then
		assertThrows(IncorrectSummonerRankSizeException.class, ()-> rankService.findAllById(request));
	}

	@Test
	@DisplayName("DB에 같은 타입의 랭크 데이터가 존재한다면 예외가 발생한다.")
	public void getInvalidTotalRanksFromSameData() {

		//given
		RequestRankDto request = RankServiceTestSetup.getRequestDto();
		String summonerId = request.getSummonerId();

		given(rankRepository.findRanks(summonerId, CURRENT_SEASON_ID))
				.willReturn(RankServiceTestSetup.getInvalidRanksToSameData(summonerId));

		//when & then
		assertThrows(NonUniqueRankTypeException.class, ()-> rankService.findAllById(request));
	}*/
}
