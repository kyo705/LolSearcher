package com.lolsearcher.controller.statistic;

import com.lolsearcher.exception.champion.NoExistChampionException;
import com.lolsearcher.exception.champion.NoExistPositionException;
import com.lolsearcher.model.response.front.championstatic.ChampPositionDto;
import com.lolsearcher.model.response.front.championstatic.TotalChampStatDto;
import com.lolsearcher.service.statistic.ChampionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.lolsearcher.constant.CacheConstants.CHAMPION_LIST_KEY;
import static com.lolsearcher.constant.LolSearcherConstants.*;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Controller
public class ChampionController {

	private final ChampionService championService;
	private final CacheManager cacheManager;

	@PostMapping(path = "/champions")
	public List<ChampPositionDto> getChampions(@RequestBody(required = false) String position) {

		String validPosition = validatePosition(position);
		
		return championService.getChampions(validPosition);
	}

	@PostMapping(path = "/champions/detail")
	public TotalChampStatDto championDetail(@RequestBody String champion) {

		String validChampionId = validateChampionId(champion);
		
		return championService.getChampionDetail(validChampionId);
	}

	public String validatePosition(String position) {

		if(position==null || position.equals("")) {
			return TOP;
		}
		if(position.equals(TOP)||
				position.equals(JUNGLE)||
				position.equals(MIDDLE)||
				position.equals(BOTTOM)||
				position.equals(UTILITY)
		) {
			return position;
		}
		throw new NoExistPositionException(position);
	}

	public String validateChampionId(String championId) {

		if(requireNonNull(cacheManager.getCache(CHAMPION_LIST_KEY)).get(championId)==null){
			throw new NoExistChampionException(championId);
		}
		return championId;
	}
}
