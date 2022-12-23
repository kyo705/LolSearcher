package com.lolsearcher.service.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.lolsearcher.model.dto.match.ParticipantDto;
import com.lolsearcher.model.dto.match.perk.PerksDto;
import com.lolsearcher.model.entity.match.Member;
import com.lolsearcher.model.entity.match.PerkStats;
import com.lolsearcher.repository.match.MatchRepository;
import com.lolsearcher.service.producer.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.api.riotgames.RiotRestAPI;
import com.lolsearcher.model.dto.match.MatchDto;
import com.lolsearcher.model.dto.parameter.MatchParam;
import com.lolsearcher.model.dto.match.SuccessMatchesAndFailMatchIds;
import com.lolsearcher.model.entity.summoner.Summoner;
import com.lolsearcher.model.entity.match.Match;
import com.lolsearcher.repository.summoner.SummonerRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchService {

	private final String failMatchIdServiceBeanName = "failMatchIdProducerService";
	private final String successMatchServiceBeanName = "successMatchProducerService";
	
	private final Map<String, ProducerService> kafkaProducerServices;
	private final ExecutorService executorService;
	private final RiotRestAPI riotApi;
	private final SummonerRepository summonerRepository;
	private final MatchRepository matchRepository;
	
	@Transactional(noRollbackFor = WebClientResponseException.class, propagation = Propagation.REQUIRES_NEW)
	public List<MatchDto> getRenewMatches(String summonerId) throws WebClientResponseException, EmptyResultDataAccessException {
		Summoner summoner = summonerRepository.findSummonerById(summonerId);
		
		List<String> matchIds = getRecentMatchIds(summoner);

		SuccessMatchesAndFailMatchIds successMatchesAndFailMatchIds = riotApi.getMatchesByNonBlocking(matchIds);
		
		List<Match> successMatches = successMatchesAndFailMatchIds.getSuccessMatches();
		List<String> failMatchIds = successMatchesAndFailMatchIds.getFailMatchIds();

		for(Match successMatch : successMatches){
			addAdditionalValue(successMatch);
		}

		sendBatchDataToKafka(successMatches, successMatchServiceBeanName);
		sendBatchDataToKafka(failMatchIds, failMatchIdServiceBeanName);

		return getMatchDtoList(successMatches);
	}

	@Transactional(readOnly = true)
	public List<MatchDto> getOldMatches(MatchParam param){

		List<Match> matches = matchRepository.findMatches(
				param.getSummonerId(),
				param.getGameType(),
				param.getChampion(),
				param.getCount()
		);
		
		List<MatchDto> oldMatches = new ArrayList<>(matches.size());
		for(Match match : matches) {
			MatchDto matchDto = getMatchDto(match);
			oldMatches.add(matchDto);
		}
		return oldMatches;
	}
	
	
	private List<String> getRecentMatchIds(Summoner summoner) throws WebClientResponseException {
		String lastMathId = summoner.getLastMatchId();
		String puuid = summoner.getPuuid();
		
		List<String> recentMatchIds = new ArrayList<>();
		List<String> matchIds = riotApi.getAllMatchIds(puuid, lastMathId);
		
		if(matchIds.size()!=0) {
			summoner.setLastMatchId(matchIds.get(0));
		}
		for(String matchId : matchIds) {
			if(matchRepository.findMatchById(matchId)==null) {
				recentMatchIds.add(matchId);
			}
		}
		return recentMatchIds;
	}

	private void addAdditionalValue(Match successMatch) {
		List<Member> members = successMatch.getMembers();

		for(Member member : members){
			PerkStats perkStats = member.getPerks().getPerkStats();

			PerkStats dbPerkStats =
					matchRepository.findPerkStats(perkStats.getDefense(), perkStats.getFlex(), perkStats.getOffense());

			perkStats.setId(dbPerkStats.getId());
		}
	}

	private <T> void sendData(T data, String serviceName) {

		ProducerService<T> producerService = kafkaProducerServices.get(serviceName);

		executorService.submit(()->producerService.send(data));
	}

	private <T> void sendBatchDataToKafka(List<T> data, String serviceName) {

		ProducerService<T> producerService = kafkaProducerServices.get(serviceName);

		executorService.submit(()-> producerService.sendBatch(data));
	}

	private List<MatchDto> getMatchDtoList(List<Match> successMatches) {
		List<MatchDto> recentMatches = new ArrayList<>(successMatches.size());

		for(Match successMatch : successMatches) {
			MatchDto matchDto = getMatchDto(successMatch);
			recentMatches.add(matchDto);
		}
		return recentMatches;
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
