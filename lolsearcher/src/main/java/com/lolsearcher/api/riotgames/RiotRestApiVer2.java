package com.lolsearcher.api.riotgames;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.constant.RankConstants;
import com.lolsearcher.model.entity.ingame.InGame;
import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.riot.match.MatchDto;
import com.lolsearcher.model.riot.match.ParticipantDto;
import com.lolsearcher.model.riot.match.perk.PerksDto;
import com.lolsearcher.model.riot.rank.RankDto;
import com.lolsearcher.model.riot.summoner.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.model.dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.model.dto.match.SuccessMatchesAndFailMatchIds;
import com.lolsearcher.model.entity.summoner.Summoner;

import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class RiotRestApiVer2 implements RiotRestAPI{
	@Value("${riot_api_key}")
	private String key = null;
	
	private final WebClient webclient;

	@Override
	public Summoner getSummonerById(String id) throws WebClientResponseException {
		String uri = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/" + id + "?api_key=" + key;

		SummonerDto summonerDto = webclient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();

		return getSummoner(summonerDto);
	}

	@Override
	public Summoner getSummonerByName(String summonerName) throws WebClientResponseException {
		String uri = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+summonerName+"?api_key="+key;

		SummonerDto summonerDto = webclient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();
		
		return getSummoner(summonerDto);
	}
	
	@Override
	public List<Rank> getLeague(String summonerId) throws WebClientResponseException {
		String uri = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/"+summonerId+"?api_key="+key;

		List<RankDto> rankDtos = webclient.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(RankDto.class)
				.collectList()
				.block();

		return getRanks(rankDtos);
	}

	@Override
	public List<String> getAllMatchIds(String puuid, String lastMatchId) throws WebClientResponseException {
		return getMatchIds(puuid, -1, "all", 0, -1, lastMatchId);
	}
	
	@Override
	public List<String> getMatchIds(
			String puuid, int queue, String type, int start, int totalCount, String lastMatchId
	) throws WebClientResponseException {

		List<String> matchIdList = new ArrayList<>();

		while(totalCount != 0) {
			int count = 100;
			if(totalCount>0 && totalCount<=100){
				count = totalCount;
			}
			String uri = getMatchIdsUri(queue, puuid, start, count);

			String[] matchIds = webclient.get()
					.uri(uri)
					.retrieve()
					.bodyToMono(String[].class)
					.block();

			for(String matchId : matchIds) {
				if(matchId.equals(lastMatchId)) {
					return matchIdList;
				}
				matchIdList.add(matchId);
			}
			start += count;
			totalCount -= count;
		}
		return matchIdList;
	}

	@Override
	public Match getOneMatchByBlocking(String matchId){
		String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/"+matchId+"?api_key="+key;

		MatchDto matchDto = webclient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(MatchDto.class)
				.block();

		return getMatch(matchDto);
	}

	@Override
	public SuccessMatchesAndFailMatchIds getMatchesByNonBlocking(List<String> matchIds) throws WebClientResponseException{
		List<Match> successMatches = new ArrayList<>();
		List<String> failMatchIds = new ArrayList<>();
		
		int requestCount = 0;
		int requestSize = 20;

		for(String matchId : matchIds) {
			if(requestCount >= requestSize) {
				break;
			}
			String uri = "https://asia.api.riotgames.com/lol/match/v5/matches/"+matchId+"?api_key="+key;

			webclient.get()
					.uri(uri)
					.retrieve()
					.bodyToMono(MatchDto.class)
					.onErrorResume(e -> {
						WebClientResponseException webClientResponseException = (WebClientResponseException) e;
						if(webClientResponseException.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
							failMatchIds.add(matchId);

							return Mono.just(null);
						}
						throw webClientResponseException;
					})
					.subscribe(result -> {
						if(result!=null) {
							Match match = getMatch(result);
							successMatches.add(match);
						}
					});

			requestCount++;
		}
		if(matchIds.size()>requestCount) {
			failMatchIds.addAll(matchIds.subList(requestCount, matchIds.size()));
		}
		waitResponse(successMatches, failMatchIds, requestCount);

		return new SuccessMatchesAndFailMatchIds(successMatches, failMatchIds);
	}

	@Override
	public InGame getInGameBySummonerId(String summonerId) throws WebClientResponseException {
		String uri = "https://kr.api.riotgames.com/lol/spectator/v4/active-games/by-summoner/" +
				summonerId + "?api_key=" + key;

		InGameDto currentGame = webclient.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(InGameDto.class)
				.block();
		
		List<CurrentGameParticipantDto> curParticipants = currentGame.getParticipants();
		for(int i=0;i<10;i++) {
			curParticipants.get(i).setNum(i);
		}
		
		return null;
	}

	private Summoner getSummoner(SummonerDto summonerDto) {

		Summoner summoner = summonerDto.changeToSummoner();
		summoner.setLastMatchId("");
		summoner.setLastRenewTimeStamp(System.currentTimeMillis());
		summoner.setLastInGameSearchTimeStamp(0);

		return summoner;
	}

	private List<Rank> getRanks(List<RankDto> rankDtos) {
		List<Rank> ranks = new ArrayList<>();

		for(RankDto rankDto : rankDtos) {
			ranks.add(rankDto.changeToRank());
		}
		return ranks;
	}

	private Match getMatch(MatchDto matchDto) {

		Match match = matchDto.changeToMatch();
		match.setSeason(RankConstants.SEASON_ID);

		List<ParticipantDto> participantDtos = matchDto.getInfo().getParticipants();

		for(int idx = 0; idx < participantDtos.size(); idx++){
			ParticipantDto participantDto = participantDtos.get(idx);

			MemberCompKey memberCompKey = new MemberCompKey(match.getMatchId(), idx);
			Member member = getMember(participantDto, memberCompKey);

			member.setMatch(match); //연관 관계 설정
		}
		return match;
	}

	private Member getMember(ParticipantDto participantDto, MemberCompKey memberCompKey){
		Member member = participantDto.changeToMember();
		member.setCk(memberCompKey);

		PerksDto perksDto = participantDto.getPerks();

		Perks perks = getPerks(perksDto, memberCompKey);
		perks.setMember(member); //연관 관계 설정

		return member;
	}

	private Perks getPerks(PerksDto perksDto, MemberCompKey memberCompKey){

		PerkStats perkStats = perksDto.getStatPerks().changeToPerkStats();

		Perks perks = perksDto.changeToPerks();
		perks.setMemberCompKey(memberCompKey); //pk 생성
		perks.setPerkStats(perkStats); //연관 관계 설정

		return perks;
	}

	private String getMatchIdsUri(int queue, String puuid, int start, int count) {
		StringBuilder uri = new StringBuilder("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?");
		if(queue != -1) {
			uri.append("queue=").append(queue).append("&");
		}
		uri.append("start=").append(start).append("&count=").append(count).append("&api_key=").append(key);

		return uri.toString();
	}

	private void waitResponse(List<Match> matches, List<String> failMatchIds, int count) {
		while(matches.size()+failMatchIds.size()!=count) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
