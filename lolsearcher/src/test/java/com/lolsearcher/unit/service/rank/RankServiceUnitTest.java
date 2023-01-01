package com.lolsearcher.unit.service.rank;

import static com.lolsearcher.constant.RankConstants.SOLO_RANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.lolsearcher.repository.rank.RankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.rank.RankDto;
import com.lolsearcher.model.dto.rank.TotalRanks;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.entity.rank.RankCompKey;
import com.lolsearcher.service.rank.RankService;

@ExtendWith(MockitoExtension.class)
public class RankServiceUnitTest {
	
	private RankService rankService;
	@Mock private RiotRestAPI riotRestApi;
	@Mock private RankRepository rankRepository;
	
	@BeforeEach
	void upset() {
		rankService = new RankService(riotRestApi, rankRepository);
	}
	
	//----------------------getLeague() 메소드 Test Case------------------------------------
	
	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.rank.RankServiceTestUpSet#getRankParameter")
	@DisplayName("getLeague : DB에 랭크 데이터가 있다면 반환하고 없을 경우 null을 반환한다.")
	public void getTotalRanks(RankCompKey soloRankKey, RankCompKey flexRankKey, Rank soloRank, Rank flexRank) {

		//given
		String summonerId = "summonerId";
		
		given(rankRepository.findRank(soloRankKey)).willReturn(soloRank);
		given(rankRepository.findRank(flexRankKey)).willReturn(flexRank);

		//when
		TotalRanks totalRanks = rankService.getLeague(summonerId);

		//then
		if(totalRanks.getSolorank()!=null) {
			assertEquals(totalRanks.getSolorank(), new RankDto(soloRank));
		}
		if(totalRanks.getTeamrank()!=null) {
			assertEquals(totalRanks.getTeamrank(), new RankDto(flexRank));
		}
	}
	
	//----------------------setLeague() 메소드 Test Case------------------------------------
	
	@EmptySource
	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.rank.RankServiceTestUpSet#setRankParameter")
	@DisplayName("setLeague : API로부터 받은 랭크 데이터를 DB에 저장한다.")
	public void setTotalRanks(List<Rank> ranks) {

		//given
		String summonerId = "summonerId";
		given(riotRestApi.getLeague(summonerId)).willReturn(ranks);

		//when
		TotalRanks totalRanks = rankService.setLeague(summonerId);

		//then
		for(Rank rank : ranks) {
			if(rank.getCk().getQueueType().equals(SOLO_RANK)) {
				assertThat(totalRanks.getSolorank().getWins()).isEqualTo(rank.getWins());
				assertThat(totalRanks.getSolorank().getLosses()).isEqualTo(rank.getLosses());

				assertThat(totalRanks.getSolorank().getRank()).isEqualTo(rank.getRank());
				assertThat(totalRanks.getSolorank().getTier()).isEqualTo(rank.getTier());
			}else {
				assertThat(totalRanks.getTeamrank().getWins()).isEqualTo(rank.getWins());
				assertThat(totalRanks.getTeamrank().getLosses()).isEqualTo(rank.getLosses());

				assertThat(totalRanks.getTeamrank().getRank()).isEqualTo(rank.getRank());
				assertThat(totalRanks.getTeamrank().getTier()).isEqualTo(rank.getTier());
			}
		}
		verify(rankRepository, times(ranks.size())).saveRank(any());
	}

	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.rank.RankServiceTestUpSet#setRankParameter")
	@DisplayName("setLeague : 다수의 클라이언트가 DB에 같은 데이터를 저장할 때 예외가 발생한다.")
	public void setTotalRanksWithOverlabData(List<Rank> ranks) {

		//given
		String summonerId = "summonerId";
		given(riotRestApi.getLeague(summonerId)).willReturn(ranks);
		
		willThrow(DataIntegrityViolationException.class).given(rankRepository).saveRank(any());

		//when & then
		assertThrows(DataIntegrityViolationException.class,()->{
			rankService.setLeague(summonerId);
			});
		verify(rankRepository, times(1)).saveRank(any());
	}
	
	@Test
	@DisplayName("setLeague : API 요청이 실패한 경우 예외가 발생한다.")
	public void setTotalRanksByTooManyRequest() {

		//given
		String summonerId = "summonerId";
		given(riotRestApi.getLeague(summonerId)).willThrow(new WebClientResponseException(
				HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
				null, null, null));

		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->rankService.setLeague(summonerId));
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		verify(rankRepository, times(0)).saveRank(any());
	}
}
