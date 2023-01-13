package com.lolsearcher.service.match;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.api.riotgames.RiotGamesAPI;
import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.constant.enumeration.GameType;
import com.lolsearcher.exception.common.IncorrectDataVersionException;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.SummaryMember;
import com.lolsearcher.model.entity.match.Team;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.factory.EntityFactory;
import com.lolsearcher.model.factory.FrontServerResponseDtoFactory;
import com.lolsearcher.model.input.front.RequestMatchDto;
import com.lolsearcher.model.input.riot.match.RiotGamesTotalMatchDto;
import com.lolsearcher.model.output.front.match.MatchDto;
import com.lolsearcher.model.output.kafka.RemainingMatchId;
import com.lolsearcher.repository.match.MatchRepository;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.message.producer.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lolsearcher.constant.BeanNameConstants.*;
import static com.lolsearcher.constant.CacheConstants.MATCH_KEY;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {

	private final Map<String, MessageProducerService> kafkaMessageProducerServices;
	private final RiotGamesAPI riotGamesApi;
	private final SummonerRepository summonerRepository;
	private final MatchRepository matchRepository;
	private final CacheManager redisCacheManager;


	@JpaTransactional
	public Flux<MatchDto> getApiMatchesFlux(RequestMatchDto request) {

		Summoner summoner = summonerRepository.findSummonerById(request.getSummonerId());

		List<String> recentMatchIds = getRecentMatchIds(summoner, request.getCount());

		if(recentMatchIds.size() == 0){
			return Flux.empty();
		}
		String beforeLastMatchId = summoner.getLastMatchId(); //병렬 처리에서 롤백을 위한 값 => 다른 스레드, 다른 트랜잭션이기 때문에 해당 트랜잭션에서 롤백 안됌
		summonerRepository.updateSummonerLastMatchId(summoner, recentMatchIds.get(0)); //last match id 를 최신 정보로 갱신

		RemainingMatchId remainingMatchIds = new RemainingMatchId(recentMatchIds.get(recentMatchIds.size()-1), beforeLastMatchId);
		kafkaMessageProducerServices.get(REMAINING_MATCH_ID_PRODUCER_SERVICE_NAME)
				.send(remainingMatchIds, summoner.getSummonerId(), beforeLastMatchId);

		List<Mono<Match>> matchMonoList = new ArrayList<>();
		for(String matchId : recentMatchIds){

			Match cachedMatch = Objects.requireNonNull(redisCacheManager.getCache(MATCH_KEY)).get(matchId, Match.class);
			if(cachedMatch != null){
				matchMonoList.add(Mono.just(cachedMatch));
				continue;
			}

			Mono<Match> matchMono = riotGamesApi.getMatchByNonBlocking(matchId)
					.onErrorResume(e -> filterError(e, matchId, summoner.getSummonerId(), beforeLastMatchId))
					.map(this::changeRequestDtoToEntity)
					.doOnNext(match -> {
						kafkaMessageProducerServices.get(SUCCESS_MATCH_PRODUCER_SERVICE_NAME)
								.send(match, summoner.getSummonerId(), beforeLastMatchId);

						Objects.requireNonNull(redisCacheManager.getCache(MATCH_KEY)).put(match.getMatchId(), match);
					});

			matchMonoList.add(matchMono);
		}
		return Flux.concat(matchMonoList).mapNotNull(match -> getResponseMatchDto(match, request));
	}


	@JpaTransactional(readOnly = true)
	public List<MatchDto> getDbMatches(RequestMatchDto request){

		List<Match> matches = matchRepository.findMatches(
				request.getSummonerId(), request.getQueueId(), request.getChampionId(), request.getCount()
		);

		return matches.stream()
				.map(FrontServerResponseDtoFactory::getResponseMatchDto)
				.collect(Collectors.toList());
	}



	private List<String> getRecentMatchIds(Summoner summoner, int count) {

		List<String> recentMatchIds = new ArrayList<>();
		List<String> matchIds = riotGamesApi.getMatchIds(summoner.getPuuid(), 0, count, summoner.getLastMatchId());

		for(String matchId : matchIds) {
			if(matchRepository.findMatchByGameId(matchId) == null) {
				recentMatchIds.add(matchId);
			}
		}
		return recentMatchIds;
	}

	private Mono<RiotGamesTotalMatchDto> filterError(Throwable e, String matchId, String summonerId, String beforeLastMatchId) {

		if(e instanceof WebClientResponseException){
			if(((WebClientResponseException) e).getStatusCode() == TOO_MANY_REQUESTS) {
				log.error("너무 많은 API 요청 시도로 인해 MATCH ID : {} 의 요청이 실패했습니다.", matchId);

				kafkaMessageProducerServices.get(FAIL_MATCH_ID_PRODUCER_SERVICE_NAME)
						.send(matchId, summonerId, beforeLastMatchId);

				return Mono.empty();
			}
		}
		log.error(e.getMessage());
		throw new RuntimeException(e);
	}

	private Match changeRequestDtoToEntity(RiotGamesTotalMatchDto riotGamesMatchDto) {
		String requestDataVersion = riotGamesMatchDto.getMetadata().getDataVersion();
		//받아온 데이터 버전 확인
		if (!requestDataVersion.equals(LolSearcherConstants.MATCH_DATA_VERSION)) {
			throw new IncorrectDataVersionException(requestDataVersion);
		}
		try {
			return EntityFactory.getMatchFromRestApiDto(riotGamesMatchDto);
		} catch (IllegalAccessException e) {
			log.error("API 데이터를 Entity 객체로 변환할 수 없음.");
			throw new RuntimeException(e);
		}
	}

	private MatchDto getResponseMatchDto(Match match, RequestMatchDto request) {
		if(isCorrespondWithCondition(match, request)){
			return FrontServerResponseDtoFactory.getResponseMatchDto(match);
		}
		return null;
	}

	private boolean isCorrespondWithCondition(Match successMatch, RequestMatchDto matchInfo) {

		String summonerId = matchInfo.getSummonerId();
		int queueId = matchInfo.getQueueId();
		int championId = matchInfo.getChampionId();

		if(successMatch.getQueueId() != queueId && queueId != GameType.ALL_QUEUE_ID.getQueueId()){
			return false;
		}
		if(championId == -1){
			return true;
		}

		for(Team team : successMatch.getTeams()){
			for(SummaryMember member : team.getMembers()){
				if(!member.getSummonerId().equals(summonerId)){
					continue;
				}
				if(member.getPickChampionId() == championId){
					return true;
				}
			}
		}
		return false;
	}
}
