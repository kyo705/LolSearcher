package com.lolsearcher.search.champion;

import com.lolsearcher.search.champion.dto.ChampEnemyStatsDto;
import com.lolsearcher.search.champion.dto.ChampItemStatsDto;
import com.lolsearcher.search.champion.dto.ChampPositionStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChampionController {

	private final ChampionService championService;

	@GetMapping(path = "/stats/champions")
	public List<ChampPositionStatsDto> getChampions(@ModelAttribute @Valid ChampionsRequest request) {
		
		return championService.findAllByPosition(request);
	}

	@GetMapping(path = "/stats/champion/{championId}/item")
	public List<ChampItemStatsDto> findItemStats(@ModelAttribute @Valid ChampionDetailsRequest request) {
		
		return championService.findItemStats(request);
	}

	@GetMapping(path = "/stats/champion/{championId}/enemy")
	public List<ChampEnemyStatsDto> findEnemyStats(@ModelAttribute @Valid ChampionDetailsRequest request) {

		return championService.findEnemyStats(request);
	}
}
