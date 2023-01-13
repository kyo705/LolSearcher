package com.lolsearcher.controller.statistic;

import com.lolsearcher.constant.enumeration.PositionStatus;
import com.lolsearcher.exception.champion.NoExistChampionException;
import com.lolsearcher.exception.champion.NoExistPositionException;
import com.lolsearcher.model.output.front.championstatic.ChampPositionDto;
import com.lolsearcher.model.output.front.championstatic.TotalChampStatDto;
import com.lolsearcher.service.statistic.ChampionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.lolsearcher.constant.CacheConstants.CHAMPION_LIST_KEY;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Controller
public class ChampionController {

	private final ChampionService championService;
	private final CacheManager cacheManager;

	@PostMapping(path = "/champions")
	public List<ChampPositionDto> getChampions(@RequestBody(required = false) String position) {

		int validPositionId = validatePosition(position);
		
		return championService.getChampions(validPositionId);
	}

	@PostMapping(path = "/champions/detail")
	public TotalChampStatDto championDetail(@RequestBody String championName) {

		int validChampionId = validateChampionId(championName);
		
		return championService.getChampionDetail(validChampionId);
	}

	public int validatePosition(String position) {

		if(position==null || position.equals("")) {
			return PositionStatus.TOP.getId();
		}
		if(position.equals(PositionStatus.TOP.getName())){
			return PositionStatus.TOP.getId();
		}
		if(position.equals(PositionStatus.JUNGLE.getName())){
			return PositionStatus.JUNGLE.getId();
		}
		if(position.equals(PositionStatus.MIDDLE.getName())){
			return PositionStatus.MIDDLE.getId();
		}
		if(position.equals(PositionStatus.BOTTOM.getName())){
			return PositionStatus.BOTTOM.getId();
		}
		if(position.equals(PositionStatus.UTILITY.getName())){
			return PositionStatus.UTILITY.getId();
		}
		throw new NoExistPositionException(position);
	}

	public int validateChampionId(String championName) {

		Integer championId = (Integer)(requireNonNull(requireNonNull(cacheManager.getCache(CHAMPION_LIST_KEY)).get(championName)).get());
		if(championId==null){
			throw new NoExistChampionException(championName);
		}
		return championId;
	}
}
