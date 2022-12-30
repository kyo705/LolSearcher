package com.lolsearcher.api.riotgames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.constant.RankConstants;
import com.lolsearcher.constant.RiotGamesConstants;
import com.lolsearcher.model.dto.ingame.BannedChampionDto;
import com.lolsearcher.model.dto.ingame.CurrentGameParticipantDto;
import com.lolsearcher.model.dto.ingame.InGameDto;
import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.riot.ingame.BannedChampionInfo;
import com.lolsearcher.model.riot.ingame.CurrentGameParticipantInfo;
import com.lolsearcher.model.riot.ingame.InGameInfo;
import com.lolsearcher.model.riot.ingame.PerksInfo;
import com.lolsearcher.model.riot.match.MatchDto;
import com.lolsearcher.model.riot.match.ParticipantDto;
import com.lolsearcher.model.riot.match.perk.PerksDto;
import com.lolsearcher.model.riot.rank.RankDto;
import com.lolsearcher.model.riot.summoner.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.model.entity.summoner.Summoner;

import reactor.core.publisher.Mono;

import static com.lolsearcher.constant.RiotGamesConstants.KR_WEB_CLIENT;

@RequiredArgsConstructor
@Component
public class RiotRestApiVer2 implements RiotRestAPI{

	@Value("${riot_api_key}")
	private String key;
	
	private final Map<String, WebClient> webclients;


	@Override
	public Summoner getSummonerById(String id) throws WebClientResponseException {
		String uri = "/lol/summoner/v4/summoners/" + id + "?api_key=" + key;

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();

		return getSummoner(summonerDto);
	}

	@Override
	public Summoner getSummonerByName(String summonerName) throws WebClientResponseException {
		String uri = "/lol/summoner/v4/summoners/by-name/"+summonerName+"?api_key="+key;

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();
		
		return getSummoner(summonerDto);
	}
	
	@Override
	public List<Rank> getLeague(String summonerId) throws WebClientResponseException {
		String uri = "/lol/league/v4/entries/by-summoner/"+summonerId+"?api_key="+key;

		List<RankDto> rankDtos = webclients.get(KR_WEB_CLIENT)
				.get()
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

		List<String> matchIds = new ArrayList<>();

		while(totalCount != 0) {
			int count = RiotGamesConstants.MATCH_ID_DEFAULT_COUNT;

			if(totalCount > 0 && totalCount <= RiotGamesConstants.MATCH_ID_DEFAULT_COUNT){
				count = totalCount;
			}

			String uri = getMatchIdsUri(queue, puuid, start, count);

			String[] apiMatchIds = webclients.get(RiotGamesConstants.ASIA_WEB_CLIENT)
					.get()
					.uri(uri)
					.retrieve()
					.bodyToMono(String[].class)
					.block();

			if(apiMatchIds == null){
				return matchIds;
			}
			for(String apiMatchId : apiMatchIds) {
				if(apiMatchId.equals(lastMatchId)) {
					return matchIds;
				}
				matchIds.add(apiMatchId);
			}
			start += count;
			totalCount -= count;
		}
		return matchIds;
	}

	@Override
	public Mono<Match> getMatchByNonBlocking(String matchId) {

		String uri = "/lol/match/v5/matches/"+matchId+"?api_key="+key;

		return webclients.get(RiotGamesConstants.ASIA_WEB_CLIENT)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(MatchDto.class)
				.flatMap(matchDto -> Mono.just(getMatch(matchDto)));
	}

	@Override
	public Match getMatchByBlocking(String matchId){
		return getMatchByNonBlocking(matchId).block();
	}

	@Cacheable(cacheManager = "cacheManager", key = "#summonerId", value = CacheConstants.IN_GAME_KEY)
	@Override
	public InGameDto getInGameBySummonerId(String summonerId) throws WebClientResponseException {
		String uri = "/lol/spectator/v4/active-games/by-summoner/" +
				summonerId + "?api_key=" + key;

		try{
			InGameInfo inGameInfo = webclients.get(KR_WEB_CLIENT)
					.get()
					.uri(uri)
					.retrieve()
					.bodyToMono(InGameInfo.class)
					.block();

			return getInGameDto(inGameInfo);

		}catch (WebClientResponseException e){
			if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
				return null;
			}
			throw e;
		}
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
		StringBuilder uri = new StringBuilder("/lol/match/v5/matches/by-puuid/"+puuid+"/ids?");
		if(queue != -1) {
			uri.append("queue=").append(queue).append("&");
		}
		uri.append("start=").append(start).append("&count=").append(count).append("&api_key=").append(key);

		return uri.toString();
	}


	private InGameDto getInGameDto(InGameInfo inGameInfo) {

		if(inGameInfo == null){
			return null;
		}

		InGameDto inGameDto = inGameInfo.changeToDto();

		if(inGameInfo.getBannedChampions() != null){
			for(BannedChampionInfo bannedChampionInfo : inGameInfo.getBannedChampions()){

				BannedChampionDto bannedChampionDto = bannedChampionInfo.changeToDto();
				inGameDto.getBannedChampions().add(bannedChampionDto);
			}
		}

		if(inGameInfo.getParticipants() != null){
			for(CurrentGameParticipantInfo currentGameParticipantInfo : inGameInfo.getParticipants()){

				CurrentGameParticipantDto currentGameParticipantDto = currentGameParticipantInfo.changeToDto();
				inGameDto.getParticipants().add(currentGameParticipantDto);

				PerksInfo perksInfo = currentGameParticipantInfo.getPerks();

				com.lolsearcher.model.dto.ingame.PerksDto perksDto = perksInfo.changeToDto();
				currentGameParticipantDto.setPerks(perksDto);
			}
		}

		return inGameDto;
	}
}
