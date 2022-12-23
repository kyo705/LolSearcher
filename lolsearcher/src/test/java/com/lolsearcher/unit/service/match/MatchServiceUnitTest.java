package com.lolsearcher.unit.service.match;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.parameter.MatchParam;
import com.lolsearcher.model.dto.match.SuccessMatchesAndFailMatchIds;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.repository.match.MatchRepository;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.match.MatchService;
import com.lolsearcher.service.producer.ProducerService;
import org.hibernate.NonUniqueResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MatchServiceUnitTest {
	@Mock private Map<String, ProducerService> producerServices;
	@Mock private ExecutorService executorService;
	@Mock private RiotRestAPI riotRestApi;
	@Mock private SummonerRepository summonerRepository;
	@Mock private MatchRepository matchRepository;
	
	private MatchService matchService;
	
	@BeforeEach
	void upset() {
		matchService = new MatchService(producerServices, executorService, riotRestApi, summonerRepository, matchRepository);
	}
	
	//----------------------getMatches() 메소드 Test Case------------------------------------
	
	
	@Test
	@DisplayName("getRenewMatches : 조건에 맞는 Summoner 객체가 DB에 없을 경우 예외가 발생한다.")
	void getRenewMatchesByNotExistingSummonerInDB() {
		//given
		String SummonerId = "summonerId";
		given(summonerRepository.findSummonerById(SummonerId)).willThrow(NoResultException.class);
		//when & then
		assertThrows(NoResultException.class, ()->{
			matchService.getRenewMatches(SummonerId);
		});
	}
	
	@Test
	@DisplayName("getRenewMatches : 조건에 맞는 Summoner 객체가 DB에 둘 이상 존재할 경우 예외가 발생한다.")
	void getRenewMatchesByExistingSummonersInDB() {
		//given
		String SummonerId = "summonerId";
		given(summonerRepository.findSummonerById(SummonerId)).willThrow(NonUniqueResultException.class);
		//when & then
		assertThrows(NonUniqueResultException.class, ()->{
			matchService.getRenewMatches(SummonerId);
		});
	}
	
	@Test
	@DisplayName("getRenewMatches : matchId를 가져오는 API 요청이 실패한 경우 예외가 발생한다.")
	void getRenewMatchesByTooManyRequestMatchId() {
		//given
		String summonerId = "summonerId";
		Summoner summoner = MatchServiceTestUpSet.getSummoner(summonerId);
		
		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);
		given(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastMatchId()))
		.willThrow(new WebClientResponseException(
				HttpStatus.TOO_MANY_REQUESTS.value(), 
				HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
				null, null, null));
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class, ()->{
			matchService.getRenewMatches(summonerId);
		});
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}
	
	
	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.match.MatchServiceTestUpSet#getMatchIdsParameter")
	@DisplayName("getRenewMatches : API를 통해 새로운 Match 데이터를 가져오는데 성공하면 Summoner 객체의 lastMatchId가 최신으로 갱신된다.")
	void getRenewMatchesBySuccess(int start, int end, List<Integer> mids, int limitedRequestCount) {
		//given
		String summonerId = "summonerId";
		Summoner summoner = MatchServiceTestUpSet.getSummoner(summonerId);

		List<String> allMatchIds = MatchServiceTestUpSet.getMatchIds(start, end);
		Map<String, Match> existedMatches = MatchServiceTestUpSet.getExistedMatches(mids);

		List<String> matchIds = MatchServiceTestUpSet.getNewMatchIds(allMatchIds, existedMatches);
		SuccessMatchesAndFailMatchIds result = MatchServiceTestUpSet.getSuccessMatchesAndFailMatchIds(matchIds, limitedRequestCount);
		
		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);
		given(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastMatchId()))
		.willReturn(allMatchIds);
		for(String matchId : allMatchIds) {
			given(matchRepository.findMatchById(matchId)).willReturn(existedMatches.getOrDefault(matchId, null));
		}
		given(riotRestApi.getMatchesByNonBlocking(matchIds)).willReturn(result);

		//when
		List<MatchDto> renewMatches = matchService.getRenewMatches(summonerId);

		//then
		assertThat(renewMatches.size()).isEqualTo(result.getSuccessMatches().size());
		assertThat(summoner.getLastMatchId()).isEqualTo(allMatchIds.get(0));
	}

	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.match.MatchServiceTestUpSet#getMatchParameter")
	@DisplayName("getOldMatches : 조건에 맞는 Match 데이터들을 DB에서 조회한다.")
	void getOldMatchesBySuccess(MatchParam matchParam) {
		//given
		List<Match> matches = MatchServiceTestUpSet.getDBMatches(matchParam);

		given(matchRepository.findMatches(
				matchParam.getSummonerId(),
				matchParam.getGameType(),
				matchParam.getChampion(),
				matchParam.getCount()
		)).willReturn(matches);

		//when
		List<MatchDto> oldMatches = matchService.getOldMatches(matchParam);

		//then
		for(int i=0;i<oldMatches.size();i++){
			assertThat(oldMatches.get(i).getMatchId())
					.isEqualTo(matches.get(i).getMatchId());
		}
	}
}
