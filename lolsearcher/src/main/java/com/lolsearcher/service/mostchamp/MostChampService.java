package com.lolsearcher.service.mostchamp;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.annotation.transaction.jpa.JpaTransactional;
import com.lolsearcher.repository.mostchamp.MostChampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lolsearcher.model.dto.parameter.MostChampParam;
import com.lolsearcher.model.dto.mostchamp.MostChampDto;

import static com.lolsearcher.constant.LolSearcherConstants.MOST_CHAMP_LIMITED_COUNT;

@RequiredArgsConstructor
@Service
public class MostChampService {
	
	private final MostChampRepository mostChampRepository;

	@JpaTransactional(readOnly = true)
	public List<MostChampDto> getMostChamp(MostChampParam param) {
		String summonerId = param.getSummonerId();
		int queue = param.getGameQueue();
		int season = param.getSeason();
		
		List<MostChampDto> mostChamps = new ArrayList<>();
		
		List<String> champIds = mostChampRepository.findMostChampionIds(summonerId, queue, season, MOST_CHAMP_LIMITED_COUNT);
		for(String champId : champIds) {
			MostChampDto champ = mostChampRepository.findMostChampion(summonerId, champId, queue, season);
			mostChamps.add(champ);
		}
		return mostChamps;
	}
}
