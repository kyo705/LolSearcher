package com.lolsearcher.service.match;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.constant.GameType;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.model.entity.match.Member;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.request.front.RequestMatchDto;
import com.lolsearcher.model.response.front.match.MatchDto;
import com.lolsearcher.model.response.front.match.ParticipantDto;
import com.lolsearcher.model.response.front.match.perk.PerksDto;
import com.lolsearcher.repository.match.MatchRepository;
import com.lolsearcher.repository.summoner.SummonerRepository;
import com.lolsearcher.service.producer.FailMatchIdProducerService;
import com.lolsearcher.service.producer.ProducerService;
import com.lolsearcher.service.producer.SuccessMatchProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import static com.lolsearcher.constant.BeanNameConstants.FAIL_MATCH_ID_PRODUCER_SERVICE_NAME;
import static com.lolsearcher.constant.BeanNameConstants.SUCCESS_MATCH_PRODUCER_SERVICE_NAME;
import static com.lolsearcher.constant.LolSearcherConstants.ALL;
import static com.lolsearcher.constant.LolSearcherConstants.MATCH_DEFAULT_COUNT;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {
	
	@SuppressWarnings("rawtypes")
	private final Map<String, ProducerService> kafkaProducerServices;
	private final ExecutorService executorService;
	private final RiotRestAPI riotApi;
	private final SummonerRepository summonerRepository;
	private final MatchRepository matchRepository;
	private final CacheManager redisCacheManager;


	@SuppressWarnings("DataFlowIssue")
	@JpaTransactional
	public List<MatchDto> getApiMatches(RequestMatchDto matchInfo) {

		Summoner summoner = summonerRepository.findSummonerById(matchInfo.getSummonerId());

		List<String> recentMatchIds = getRecentMatchIds(summoner);

		if(recentMatchIds.size() == 0){
			return new ArrayList<>();
		}

		String beforeLastMatchId = summoner.getLastMatchId(); //병렬 처리에서 롤백을 위한 값 => 다른 스레드, 다른 트랜잭션이기 때문에 해당 트랜잭션에서 롤백 안됌
		summoner.setLastMatchId(recentMatchIds.get(0)); //last match id 를 최신 정보로 갱신

		List<Match> successMatches = new ArrayList<>(MATCH_DEFAULT_COUNT);
		List<String> failMatchIds = new ArrayList<>(recentMatchIds.size());

		int subscribeCount = 0;
		for(String matchId : recentMatchIds){
			Match cachedMatch = redisCacheManager.getCache(CacheConstants.MATCH_KEY).get(matchId, Match.class);

			if(cachedMatch != null){
				successMatches.add(cachedMatch);
				continue;
			}

			Mono<Match> matchMono = riotApi.getMatchByNonBlocking(matchId);

			matchMono
					.onErrorResume(e -> {
						WebClientResponseException webClientResponseException = (WebClientResponseException) e;
						if(webClientResponseException.getStatusCode() != TOO_MANY_REQUESTS) {
							throw webClientResponseException;
						}
						failMatchIds.add(matchId);

						return Mono.just(null);
					})
					.filter(Objects::nonNull)
					.subscribe(match -> {
						successMatches.add(match);

						redisCacheManager.getCache(CacheConstants.MATCH_KEY).put(match.getMatchId(), match);
					});

			if(++subscribeCount >= MATCH_DEFAULT_COUNT){
				break;
			}
		}

		waitResponseComplete(recentMatchIds, successMatches, failMatchIds);

		//카프카에 데이터 저장하는 로직 => 멀티 스레드로 병렬 처리
		sendSuccessMatchToKafka(successMatches, matchInfo.getSummonerId(), beforeLastMatchId);
		sendFailMatchIdToKafka(failMatchIds, matchInfo.getSummonerId(), beforeLastMatchId);

		return getMatchDtoList(successMatches, matchInfo);
	}


	@JpaTransactional(readOnly = true)
	public List<MatchDto> getDbMatches(RequestMatchDto matchInfo){

		List<Match> matches = matchRepository.findMatches(
				matchInfo.getSummonerId(), matchInfo.getQueueId(), matchInfo.getChampionId(), matchInfo.getCount()
		);

		List<MatchDto> oldMatches = new ArrayList<>(matches.size());
		for(Match match : matches) {
			MatchDto matchDto = getMatchDto(match);
			oldMatches.add(matchDto);
		}
		return oldMatches;
	}

	private List<String> getRecentMatchIds(Summoner summoner) {

		List<String> recentMatchIds = new ArrayList<>();
		List<String> matchIds = riotApi.getAllMatchIds(summoner.getPuuid(), summoner.getLastMatchId());

		for(String matchId : matchIds) {
			if(matchRepository.findMatchById(matchId) == null) {
				recentMatchIds.add(matchId);
			}
		}
		return recentMatchIds;
	}

	private void waitResponseComplete(List<String> matchIds, List<Match> successMatches, List<String> failMatchIds) {

		if(matchIds.size() >= MATCH_DEFAULT_COUNT){
			failMatchIds.addAll(matchIds.subList(MATCH_DEFAULT_COUNT, matchIds.size()));
		}

		while(true){
			if(matchIds.size() == successMatches.size() + failMatchIds.size()){
				return;
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void sendFailMatchIdToKafka(List<String> failMatchIds, String summonerId, String beforeLastMatchId) {

		try{
			FailMatchIdProducerService producerService =
					(FailMatchIdProducerService) kafkaProducerServices.get(FAIL_MATCH_ID_PRODUCER_SERVICE_NAME);

			executorService.submit(()-> producerService.send(failMatchIds, summonerId, beforeLastMatchId));
		}catch (NullPointerException e){
			log.error("{}의 이름을 가진 프로듀서 서비스가 존재하지 않음.", FAIL_MATCH_ID_PRODUCER_SERVICE_NAME);
			log.error(failMatchIds.toString());
		}catch (ClassCastException e){
			log.error("프로듀서 서비스를 {} 클래스 타입으로 변환할 수 없음", FAIL_MATCH_ID_PRODUCER_SERVICE_NAME);
			log.error(failMatchIds.toString());
		}
	}

	private void sendSuccessMatchToKafka(List<Match> successMatches, String summonerId, String beforeLastMatchId) {
		try{
			SuccessMatchProducerService producerService =
					(SuccessMatchProducerService) kafkaProducerServices.get(SUCCESS_MATCH_PRODUCER_SERVICE_NAME);

			executorService.submit(()-> producerService.send(successMatches, summonerId, beforeLastMatchId));
		}catch (NullPointerException e){
			log.error("{}의 이름을 가진 프로듀서 서비스가 존재하지 않음.", SUCCESS_MATCH_PRODUCER_SERVICE_NAME);
			log.error(successMatches.toString());
		}catch (ClassCastException e){
			log.error("프로듀서 서비스를 {} 클래스 타입으로 변환할 수 없음", SUCCESS_MATCH_PRODUCER_SERVICE_NAME);
			log.error(successMatches.toString());
		}
	}

	private List<MatchDto> getMatchDtoList(List<Match> successMatches, RequestMatchDto matchInfo) {

		List<MatchDto> recentMatches = new ArrayList<>(matchInfo.getCount());

		int size = matchInfo.getCount();
		for(Match successMatch : successMatches) {
			if(size == 0){
				break;
			}
			if(!isCorrespondWithCondition(successMatch, matchInfo)){
				continue;
			}
			MatchDto matchDto = getMatchDto(successMatch);
			recentMatches.add(matchDto);

			size--;
		}
		return recentMatches;
	}

	private boolean isCorrespondWithCondition(Match successMatch, RequestMatchDto matchInfo) {

		String summonerId = matchInfo.getSummonerId();
		int queueId = matchInfo.getQueueId();
		String championId = matchInfo.getChampionId();

		if(successMatch.getQueueId() != queueId && queueId != GameType.ALL_QUEUE_ID.getQueueId()){
			return false;
		}
		if(championId.equals(ALL)){
			return true;
		}
		for(Member member : successMatch.getMembers()){
			if(!member.getSummonerId().equals(summonerId)){
				continue;
			}
			if(member.getChampionName().equals(championId)){
				return true;
			}
		}
		return false;
	}

	private MatchDto getMatchDto(Match successMatch) {
		MatchDto matchDto = new MatchDto(successMatch);
		List<ParticipantDto> members = new ArrayList<>();

		for(Member member : successMatch.getMembers()){
			ParticipantDto participantDto = new ParticipantDto(member);

			PerksDto perksDto = new PerksDto(member.getPerks());
			participantDto.setPerksDto(perksDto);

			members.add(participantDto);
		}
		matchDto.setMembers(members);

		return matchDto;
	}
}
