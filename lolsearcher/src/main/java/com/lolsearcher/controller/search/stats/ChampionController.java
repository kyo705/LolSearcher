package com.lolsearcher.controller.search.stats;

import com.lolsearcher.exception.exception.common.IncorrectGameVersionException;
import com.lolsearcher.exception.exception.search.champion.InvalidChampionIdException;
import com.lolsearcher.model.request.search.championstats.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.search.championstats.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.search.championstats.TotalChampStatDto;
import com.lolsearcher.service.search.stats.ChampionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.CURRENT_GAME_VERSION;
import static com.lolsearcher.constant.RedisCacheNameConstants.CHAMPION_ID_LIST;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@RestController
public class ChampionController {

	private final ChampionService championService;
	private final CacheManager cacheManager;

	@PostMapping(path = "/stats/champion")
	public List<ChampPositionStatsDto> getChampions(@RequestBody @Valid RequestChampPositionStatsDto requestDto) {

		validateChampStatsRequest(requestDto);
		
		return championService.getAllChampPositionStats(requestDto);
	}

	@PostMapping(path = "/stats/champion/detail")
	public TotalChampStatDto getChampionDetail(@RequestBody @Valid RequestChampDetailStatsDto requestDto) {

		validateChampDetailStatsRequest(requestDto);
		
		return championService.getChampDetailStats(requestDto);
	}


	private void validateChampStatsRequest(RequestChampPositionStatsDto requestDto) {

		String gameVersion = requestDto.getGameVersion();

		if(!gameVersion.equals(CURRENT_GAME_VERSION)){
			throw new IncorrectGameVersionException(gameVersion);
		}
	}

	private void validateChampDetailStatsRequest(RequestChampDetailStatsDto requestDto) {

		int championId = requestDto.getChampionId();
		String gameVersion = requestDto.getGameVersion();

		if(!gameVersion.equals(CURRENT_GAME_VERSION)){
			throw new IncorrectGameVersionException(gameVersion);
		}
		if(requireNonNull(cacheManager.getCache(CHAMPION_ID_LIST)).get(championId) == null){
			throw new InvalidChampionIdException(championId);
		}
	}
}
