package com.lolsearcher.api.riotgames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.model.entity.match.*;
import com.lolsearcher.model.entity.rank.Rank;
import com.lolsearcher.model.request.riot.ingame.BannedChampionDto;
import com.lolsearcher.model.request.riot.ingame.CurrentGameParticipantDto;
import com.lolsearcher.model.request.riot.ingame.InGameDto;
import com.lolsearcher.model.request.riot.ingame.PerksDto;
import com.lolsearcher.model.request.riot.match.TotalMatchDto;
import com.lolsearcher.model.request.riot.match.ParticipantDto;
import com.lolsearcher.model.request.riot.rank.RankDto;
import com.lolsearcher.model.request.riot.summoner.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.lolsearcher.model.entity.summoner.Summoner;

import reactor.core.publisher.Mono;

import static com.lolsearcher.constant.BeanNameConstants.ASIA_WEB_CLIENT_NAME;
import static com.lolsearcher.constant.BeanNameConstants.KR_WEB_CLIENT_NAME;
import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_SEASON_ID;
import static com.lolsearcher.constant.LolSearcherConstants.MATCH_ID_DEFAULT_COUNT;
import static com.lolsearcher.constant.UriConstants.*;

@RequiredArgsConstructor
@Component
public class RiotGamesApiVer2 implements RiotGamesAPI {

	@Value("${riot_api_key}")
	private String key;
	
	private final Map<String, WebClient> webclients;


	@Override
	public Summoner getSummonerById(String summonerId) {

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_SUMMONER_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();

		return getSummoner(summonerDto);
	}

	@Override
	public Summoner getSummonerByName(String summonerName) {

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_SUMMONER_WITH_NAME_URI, summonerName, key)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();
		
		return getSummoner(summonerDto);
	}
	
	@Override
	public List<Rank> getLeague(String summonerId) {

		List<RankDto> rankDtos = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_RANK_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToFlux(RankDto.class)
				.collectList()
				.block();

		return getRanks(rankDtos);
	}

	@Override
	public List<String> getAllMatchIds(String puuid, String lastMatchId) {
		return getMatchIds(puuid, 0, -1, lastMatchId);
	}
	
	@Override
	public List<String> getMatchIds(String puuid, int start, int totalCount, String lastMatchId) {

		List<String> matchIds = new ArrayList<>();

		while(totalCount != 0) {
			int count = MATCH_ID_DEFAULT_COUNT;

			if(totalCount > 0 && totalCount <= MATCH_ID_DEFAULT_COUNT){
				count = totalCount;
			}

			String[] apiMatchIds = webclients.get(ASIA_WEB_CLIENT_NAME)
					.get()
					.uri(RIOTGAMES_MATCHIDS_WITH_PUUID_URI, puuid, start, count, key)
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

		return webclients.get(ASIA_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_MATCH_WITH_ID_URI, matchId, key)
				.retrieve()
				.bodyToMono(TotalMatchDto.class)
				.flatMap(totalMatchDto -> Mono.just(getMatch(totalMatchDto)));
	}

	@Override
	public Match getMatchByBlocking(String matchId){
		return getMatchByNonBlocking(matchId).block();
	}

	@Cacheable(cacheManager = "redisCacheManager", key = "#summonerId", value = CacheConstants.IN_GAME_KEY)
	@Override
	public com.lolsearcher.model.response.front.ingame.InGameDto getInGameBySummonerId(String summonerId) {

		InGameDto inGameDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_INGAME_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToMono(InGameDto.class)
				.block();

		return getInGameDto(inGameDto);
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

	private Match getMatch(TotalMatchDto totalMatchDto) {

		Match match = totalMatchDto.changeToMatch();
		match.setSeason(CURRENT_SEASON_ID);

		List<ParticipantDto> participantDtos = totalMatchDto.getInfo().getParticipants();

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

		com.lolsearcher.model.request.riot.match.perk.PerksDto perksDto = participantDto.getPerks();

		Perks perks = getPerks(perksDto, memberCompKey);
		perks.setMember(member); //연관 관계 설정

		return member;
	}

	private Perks getPerks(com.lolsearcher.model.request.riot.match.perk.PerksDto perksDto, MemberCompKey memberCompKey){

		PerkStats perkStats = perksDto.getStatPerks().changeToPerkStats();

		Perks perks = perksDto.changeToPerks();
		perks.setMemberCompKey(memberCompKey); //pk 생성
		perks.setPerkStats(perkStats); //연관 관계 설정

		return perks;
	}


	private com.lolsearcher.model.response.front.ingame.InGameDto getInGameDto(InGameDto riotGamesInGameDto) {

		if(riotGamesInGameDto == null){
			return null;
		}

		com.lolsearcher.model.response.front.ingame.InGameDto inGameDto = riotGamesInGameDto.changeToDto();

		if(riotGamesInGameDto.getBannedChampions() != null){
			for(BannedChampionDto bannedChampionInfo : riotGamesInGameDto.getBannedChampions()){

				com.lolsearcher.model.response.front.ingame.BannedChampionDto bannedChampionDto = bannedChampionInfo.changeToDto();
				inGameDto.getBannedChampions().add(bannedChampionDto);
			}
		}

		if(riotGamesInGameDto.getParticipants() != null){
			for(CurrentGameParticipantDto riotGamesCurrentGameParticipantDto : riotGamesInGameDto.getParticipants()){

				com.lolsearcher.model.response.front.ingame.CurrentGameParticipantDto currentGameParticipantDto = riotGamesCurrentGameParticipantDto.changeToDto();
				inGameDto.getParticipants().add(currentGameParticipantDto);

				PerksDto riotGamesPerksDto = riotGamesCurrentGameParticipantDto.getPerks();

				com.lolsearcher.model.response.front.ingame.PerksDto perksDto = riotGamesPerksDto.changeToDto();
				currentGameParticipantDto.setPerks(perksDto);
			}
		}

		return inGameDto;
	}
}
