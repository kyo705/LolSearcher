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

@RequiredArgsConstructor
@Component
public class RiotRestApiVer2 implements RiotRestAPI{

	@Value("${riot_api_key}")
	private String key;
	
	private final Map<String, WebClient> webclients;


	@Override
	public Summoner getSummonerById(String id) {
		String uri = "/lol/summoner/v4/summoners/" + id + "?api_key=" + key;

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();

		return getSummoner(summonerDto);
	}

	@Override
	public Summoner getSummonerByName(String summonerName) {
		String uri = "/lol/summoner/v4/summoners/by-name/"+summonerName+"?api_key="+key;

		SummonerDto summonerDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(SummonerDto.class)
				.block();
		
		return getSummoner(summonerDto);
	}
	
	@Override
	public List<Rank> getLeague(String summonerId) {
		String uri = "/lol/league/v4/entries/by-summoner/"+summonerId+"?api_key="+key;

		List<RankDto> rankDtos = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(RankDto.class)
				.collectList()
				.block();

		return getRanks(rankDtos);
	}

	@Override
	public List<String> getAllMatchIds(String puuid, String lastMatchId) {
		return getMatchIds(puuid, -1, "all", 0, -1, lastMatchId);
	}
	
	@Override
	public List<String> getMatchIds(String puuid, int queue, String type, int start, int totalCount, String lastMatchId) {

		List<String> matchIds = new ArrayList<>();

		while(totalCount != 0) {
			int count = MATCH_ID_DEFAULT_COUNT;

			if(totalCount > 0 && totalCount <= MATCH_ID_DEFAULT_COUNT){
				count = totalCount;
			}

			String uri = getMatchIdsUri(queue, puuid, start, count);

			String[] apiMatchIds = webclients.get(ASIA_WEB_CLIENT_NAME)
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

		return webclients.get(ASIA_WEB_CLIENT_NAME)
				.get()
				.uri(uri)
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
		String uri = "/lol/spectator/v4/active-games/by-summoner/" +
				summonerId + "?api_key=" + key;

		InGameDto inGameDto = webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(uri)
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

	private String getMatchIdsUri(int queue, String puuid, int start, int count) {
		StringBuilder uri = new StringBuilder("/lol/match/v5/matches/by-puuid/"+puuid+"/ids?");
		if(queue != -1) {
			uri.append("queue=").append(queue).append("&");
		}
		uri.append("start=").append(start).append("&count=").append(count).append("&api_key=").append(key);

		return uri.toString();
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
