package com.lolsearcher.unit.service.match;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.parameter.MatchParam;
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
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.lolsearcher.constant.RiotGamesConstants.MATCH_DEFAULT_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MatchServiceUnitTest {
	@Mock private Map<String, ProducerService> producerServices;
	@Mock private ExecutorService executorService;
	@Mock private RiotRestAPI riotRestApi;
	@Mock private SummonerRepository summonerRepository;
	@Mock private MatchRepository matchRepository;
	@Mock private CacheManager redisCacheManager;
	
	private MatchService matchService;
	
	@BeforeEach
	void upset() {
		matchService = new MatchService(producerServices, executorService, riotRestApi,
				summonerRepository, matchRepository, redisCacheManager);
	}
	
	//----------------------getMatches() 메소드 Test Case------------------------------------

	@Test
	@DisplayName("getApiMatches : 조건에 맞는 Summoner 객체가 DB에 없을 경우 예외가 발생한다.")
	void getRenewMatchesByNotExistingSummonerInDB() {
		//given
		String SummonerId = "summonerId";
		given(summonerRepository.findSummonerById(SummonerId)).willThrow(NoResultException.class);
		//when & then
		assertThrows(NoResultException.class, ()->{
			matchService.getApiMatches(SummonerId);
		});
	}

	@Test
	@DisplayName("getApiMatches : 조건에 맞는 Summoner 객체가 DB에 둘 이상 존재할 경우 예외가 발생한다.")
	void getRenewMatchesByExistingSummonersInDB() {
		//given
		String SummonerId = "summonerId";
		given(summonerRepository.findSummonerById(SummonerId)).willThrow(NonUniqueResultException.class);
		//when & then
		assertThrows(NonUniqueResultException.class, ()->{
			matchService.getApiMatches(SummonerId);
		});
	}

	@Test
	@DisplayName("getApiMatches : matchId를 가져오는 API 요청이 실패한 경우 예외가 발생한다.")
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
			matchService.getApiMatches(summonerId);
		});
		assertThat(e.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}


	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.match.MatchServiceTestUpSet#getMatchIdsParameter")
	@DisplayName("getApiMatches : API를 통해 새로운 Match 데이터를 가져오는데 성공하면 Summoner 객체의 lastMatchId가 최신으로 갱신된다.")
	void getRenewMatchesBySuccess(int matchIdCount) {
		//given
		String summonerId = "summonerId";
		Summoner summoner = MatchServiceTestUpSet.getSummoner(summonerId);
		String beforeLastMatchId = summoner.getLastMatchId();

		List<String> matchIds = MatchServiceTestUpSet.getMatchIds(matchIdCount);

		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);
		given(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastMatchId())).willReturn(matchIds);
		given(redisCacheManager.getCache(CacheConstants.MATCH_KEY)).willReturn(new ConcurrentMapCache("No Cache"));

		given(matchRepository.findMatchById(any())).willReturn(null);

		for(int i=0; i < Math.min(matchIds.size(), MATCH_DEFAULT_COUNT); i++) {
			String matchId = matchIds.get(i);
			given(riotRestApi.getMatchByNonBlocking(matchId)).willReturn(MatchServiceTestUpSet.getMatchMono(matchId));
		}

		//when
		List<MatchDto> renewMatches = matchService.getApiMatches(summonerId);

		//then
		String afterLastMatchId = summoner.getLastMatchId();

		assertThat(afterLastMatchId).isNotEqualTo(beforeLastMatchId);
		assertThat(afterLastMatchId).isEqualTo(matchIds.get(0));

		assertThat(renewMatches.size()).isEqualTo(Math.min(matchIds.size(), MATCH_DEFAULT_COUNT));
	}

	@Test
	@DisplayName("getApiMatches : API를 통해 새로운 Match 데이터를 0개 가져온다면 Summoner 객체의 lastMatchId가 최신으로 갱신되지 않는다.")
	void getRenewMatchesByZero() {
		//given
		String summonerId = "summonerId";
		Summoner summoner = MatchServiceTestUpSet.getSummoner(summonerId);
		String beforeLastMatchId = summoner.getLastMatchId();

		List<String> matchIds = MatchServiceTestUpSet.getMatchIds(0);

		given(summonerRepository.findSummonerById(summonerId)).willReturn(summoner);
		given(riotRestApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastMatchId())).willReturn(matchIds);

		//when
		List<MatchDto> renewMatches = matchService.getApiMatches(summonerId);

		//then
		String afterLastMatchId = summoner.getLastMatchId();

		assertThat(beforeLastMatchId).isEqualTo(afterLastMatchId);
		assertThat(renewMatches.size()).isEqualTo(0);
	}

	@ParameterizedTest
	@MethodSource("com.lolsearcher.unit.service.match.MatchServiceTestUpSet#getMatchParameter")
	@DisplayName("getDbMatches : 조건에 맞는 Match 데이터들을 DB에서 조회한다.")
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
		List<MatchDto> oldMatches = matchService.getDbMatches(matchParam);

		//then
		for(int i=0;i<oldMatches.size();i++){
			assertThat(oldMatches.get(i).getMatchId())
					.isEqualTo(matches.get(i).getMatchId());
		}
	}
}
