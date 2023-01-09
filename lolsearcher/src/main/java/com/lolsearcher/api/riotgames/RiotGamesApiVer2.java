package com.lolsearcher.api.riotgames;

import com.lolsearcher.constant.CacheConstants;
import com.lolsearcher.model.request.riot.ingame.RiotGamesInGameDto;
import com.lolsearcher.model.request.riot.match.RiotGamesTotalMatchDto;
import com.lolsearcher.model.request.riot.rank.RiotGamesRankDto;
import com.lolsearcher.model.request.riot.summoner.RiotGamesSummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.ASIA_WEB_CLIENT_NAME;
import static com.lolsearcher.constant.BeanNameConstants.KR_WEB_CLIENT_NAME;
import static com.lolsearcher.constant.LolSearcherConstants.MATCH_ID_DEFAULT_COUNT;
import static com.lolsearcher.constant.UriConstants.*;

@RequiredArgsConstructor
@Component
public class RiotGamesApiVer2 implements RiotGamesAPI {

	@Value("${riot_api_key}")
	private String key;
	
	private final Map<String, WebClient> webclients;


	@Override
	public RiotGamesSummonerDto getSummonerById(String summonerId) {

		return webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_SUMMONER_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToMono(RiotGamesSummonerDto.class)
				.block();
	}

	@Override
	public RiotGamesSummonerDto getSummonerByName(String summonerName) {

		return webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_SUMMONER_WITH_NAME_URI, summonerName, key)
				.retrieve()
				.bodyToMono(RiotGamesSummonerDto.class)
				.block();
	}
	
	@Override
	public List<RiotGamesRankDto> getLeague(String summonerId) {

		return webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_RANK_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToFlux(RiotGamesRankDto.class)
				.collectList()
				.block();
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
				if(apiMatchId.equals(lastMatchId)) { /* lastMatchId : 해당 유저의 DB 내 저장된 최신 매치 id  */
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
	public Mono<RiotGamesTotalMatchDto> getMatchByNonBlocking(String matchId) {

		return webclients.get(ASIA_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_MATCH_WITH_ID_URI, matchId, key)
				.retrieve()
				.bodyToMono(RiotGamesTotalMatchDto.class);
	}

	@Override
	public RiotGamesTotalMatchDto getMatchByBlocking(String matchId){
		return getMatchByNonBlocking(matchId).block();
	}

	@Cacheable(cacheManager = "redisCacheManager", key = "#summonerId", value = CacheConstants.IN_GAME_KEY)
	@Override
	public RiotGamesInGameDto getInGameBySummonerId(String summonerId) {

		return webclients.get(KR_WEB_CLIENT_NAME)
				.get()
				.uri(RIOTGAMES_INGAME_WITH_ID_URI, summonerId, key)
				.retrieve()
				.bodyToMono(RiotGamesInGameDto.class)
				.block();
	}
}
