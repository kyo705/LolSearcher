package com.lolsearcher.controller.search.stats;

import com.lolsearcher.constant.LolSearcherConstants;
import com.lolsearcher.exception.exception.champion.InvalidChampionIdException;
import com.lolsearcher.exception.exception.common.IncorrectGameVersionException;
import com.lolsearcher.model.request.RequestChampDetailStatsDto;
import com.lolsearcher.model.request.RequestChampPositionStatsDto;
import com.lolsearcher.model.response.front.championstats.ChampPositionStatsDto;
import com.lolsearcher.model.response.front.championstats.TotalChampStatDto;
import com.lolsearcher.service.search.stats.ChampionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

import static com.lolsearcher.constant.RedisCacheConstants.CHAMPION_LIST_KEY;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Controller
public class ChampionController {

	private final ChampionService championService;
	private final CacheManager cacheManager;

	@PostMapping(path = "/champion")
	public List<ChampPositionStatsDto> getChampions(@RequestBody @Valid RequestChampPositionStatsDto requestDto) {

		RequestChampPositionStatsDto validRequest = validateChampPositionStatsRequest(requestDto);
		
		return championService.getAllChampPositionStats(validRequest);
	}

	@PostMapping(path = "/champion/detail")
	public TotalChampStatDto championDetail(@RequestBody @Valid RequestChampDetailStatsDto requestDto) {

		RequestChampDetailStatsDto validRequest = validateChampDetailStatsRequest(requestDto);
		
		return championService.getChampDetailStats(validRequest);
	}


	private RequestChampPositionStatsDto validateChampPositionStatsRequest(RequestChampPositionStatsDto requestDto) {

		String gameVersion = requestDto.getGameVersion();

		if(!isValidGameVersion(gameVersion)){
			throw new IncorrectGameVersionException(gameVersion);
		}
		return requestDto;
	}

	private RequestChampDetailStatsDto validateChampDetailStatsRequest(RequestChampDetailStatsDto requestDto) {

		int championId = requestDto.getChampionId();
		String gameVersion = requestDto.getGameVersion();

		if(!isValidGameVersion(gameVersion)){
			throw new IncorrectGameVersionException(gameVersion);
		}
		if(requireNonNull(cacheManager.getCache(CHAMPION_LIST_KEY)).get(championId) == null){
			throw new InvalidChampionIdException(championId);
		}
		return requestDto;
	}

	private boolean isValidGameVersion(String gameVersion){

		return gameVersion.equals(LolSearcherConstants.CURRENT_GAME_VERSION);
	}
}
