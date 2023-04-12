package com.lolsearcher.unit.service.search.rank;

import com.lolsearcher.exception.exception.search.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.exception.exception.search.rank.NonUniqueRankTypeException;
import com.lolsearcher.model.request.search.rank.RequestRankDto;
import com.lolsearcher.model.response.front.search.rank.RankDto;
import com.lolsearcher.repository.search.rank.RankRepository;
import com.lolsearcher.service.search.rank.RankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;
import static com.lolsearcher.constant.LolSearcherConstants.THE_NUMBER_OF_RANK_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RankServiceUnitTest {
	
	private RankService rankService;
	@Mock private RankRepository rankRepository;
	
	@BeforeEach
	void upset() {
		rankService = new RankService(rankRepository);
	}
	

	@Test
	@DisplayName("DB에 정상적인 랭크 데이터가 존재한다면 해당 데이터를 반환한다.")
	public void getValidTotalRanks() {

		//given
		RequestRankDto request = RankServiceTestSetup.getRequestDto();
		String summonerId = request.getSummonerId();

		given(rankRepository.findRanks(summonerId, CURRENT_SEASON_ID))
				.willReturn(RankServiceTestSetup.getValidRanks(summonerId));

		//when
		Map<String, RankDto> totalRankDto = rankService.getOldRanks(request);

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
		assertThrows(IncorrectSummonerRankSizeException.class, ()-> rankService.getOldRanks(request));
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
		assertThrows(NonUniqueRankTypeException.class, ()-> rankService.getOldRanks(request));
	}
}
